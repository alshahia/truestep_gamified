package com.example.sensor

import android.os.Build
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.annotation.StringRes
import com.example.R
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.sqrt

enum class SensorMode(
    val displayName: String,
    val technicalName: String,
    @StringRes val displayNameRes: Int = 0
) {
    HARDWARE_STEP_COUNTER("Hardware Step Counter", "Sensor.TYPE_STEP_COUNTER", R.string.sensor_hw_step_counter),
    HARDWARE_STEP_DETECTOR("Hardware Step Detector", "Sensor.TYPE_STEP_DETECTOR", R.string.sensor_hw_step_detector),
    ACCELEROMETER_FALLBACK("Accelerometer Step Detector (Fallback)", "Sensor.TYPE_ACCELEROMETER", R.string.sensor_accelerometer_fallback),
    NONE("No Sensor Available", "None", R.string.sensor_none_available)
}

class StepSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    // Tier 1: Hardware Step Counter (lowest power, hardware accumulator)
    // Note: Request the wake-up version first so the hardware wakes the CPU when screen is turned off,
    // preventing hardware FIFO buffer overflow and dropped steps during pocket walking.
    private val stepCounterSensor: Sensor? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER, true)
            ?: sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    } else {
        sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    // Tier 2: Hardware Step Detector (per-step trigger when accumulator is unavailable)
    private val stepDetectorSensor: Sensor? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR, true)
            ?: sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    } else {
        sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    }

    // Tier 3: 3-Axis Accelerometer (universal fallback available on all devices)
    private val accelerometerSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val activeMode: SensorMode = when {
        stepCounterSensor != null -> SensorMode.HARDWARE_STEP_COUNTER
        stepDetectorSensor != null -> SensorMode.HARDWARE_STEP_DETECTOR
        accelerometerSensor != null -> SensorMode.ACCELEROMETER_FALLBACK
        else -> SensorMode.NONE
    }

    private var baselineSensorValue: Int = -1
    private var isListening: Boolean = false
    private var isPaused: Boolean = false

    private val _isSensorAvailable = MutableStateFlow(activeMode != SensorMode.NONE)
    val isSensorAvailable: StateFlow<Boolean> = _isSensorAvailable.asStateFlow()

    private val _latestRawSensorValue = MutableStateFlow(0)
    val latestRawSensorValue: StateFlow<Int> = _latestRawSensorValue.asStateFlow()

    private val _currentSensorMode = MutableStateFlow(activeMode)
    val currentSensorMode: StateFlow<SensorMode> = _currentSensorMode.asStateFlow()

    private val stepListeners = CopyOnWriteArrayList<(delta: Int, rawValue: Int) -> Unit>()

    var onStepDeltaListener: ((delta: Int, rawValue: Int) -> Unit)?
        get() = stepListeners.firstOrNull()
        set(value) {
            stepListeners.clear()
            if (value != null) stepListeners.add(value)
        }

    fun addStepDeltaListener(listener: (delta: Int, rawValue: Int) -> Unit) {
        if (!stepListeners.contains(listener)) {
            stepListeners.add(listener)
        }
    }

    fun removeStepDeltaListener(listener: (delta: Int, rawValue: Int) -> Unit) {
        stepListeners.remove(listener)
    }

    private fun notifyStepDelta(delta: Int, rawValue: Int) {
        for (listener in stepListeners) {
            try {
                listener.invoke(delta, rawValue)
            } catch (e: Exception) {
                // Prevent one failing listener from blocking telemetry
            }
        }
    }

    // Synthetic accumulator for detector & accelerometer fallbacks
    private var syntheticStepCounter: Int = 0

    // Accelerometer Signal Processing State
    private var smoothedMagnitude: Float = 9.81f
    private var dynamicGravityBaseline: Float = 9.81f
    private var isWaveAboveThreshold: Boolean = false
    private var lastDetectedStepTimestampMs: Long = 0L

    // Sensitivity threshold (m/s^2 above baseline gravity)
    var stepThreshold: Float = 1.55f

    fun isSupported(): Boolean = activeMode != SensorMode.NONE

    fun getActiveSourceDescription(): String = activeMode.displayName

    fun setCarryingModeSensitivity(mode: String) {
        stepThreshold = when (mode.lowercase()) {
            "hand" -> 2.0f       // Higher threshold to reject hand gestures
            "bag", "backpack" -> 1.35f // Slightly more sensitive for dampened bag movement
            "treadmill" -> 1.50f
            else -> 1.55f        // Pocket / standard
        }
    }

    fun startListening() {
        if (isListening || sensorManager == null) return

        // Balanced latency: 2-second batching ensures CPU deep sleep while avoiding OEM driver buffer drops
        val batchLatencyUs = 2_000_000 // 2 seconds

        when (activeMode) {
            SensorMode.HARDWARE_STEP_COUNTER -> {
                stepCounterSensor?.let {
                    sensorManager.registerListener(
                        this,
                        it,
                        SensorManager.SENSOR_DELAY_NORMAL,
                        batchLatencyUs
                    )
                }
            }
            SensorMode.HARDWARE_STEP_DETECTOR -> {
                stepDetectorSensor?.let {
                    sensorManager.registerListener(
                        this,
                        it,
                        SensorManager.SENSOR_DELAY_NORMAL,
                        batchLatencyUs
                    )
                }
            }
            SensorMode.ACCELEROMETER_FALLBACK -> {
                accelerometerSensor?.let {
                    sensorManager.registerListener(
                        this,
                        it,
                        SensorManager.SENSOR_DELAY_UI
                    )
                }
            }
            SensorMode.NONE -> {
                // No sensor available on this hardware
            }
        }
        isListening = true
    }

    fun stopListening() {
        if (!isListening || sensorManager == null) return
        sensorManager.unregisterListener(this)
        isListening = false
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }

    fun setBaseline(lastKnownRawValue: Int) {
        baselineSensorValue = lastKnownRawValue
        syntheticStepCounter = lastKnownRawValue
    }

    fun resetSession() {
        baselineSensorValue = -1
        syntheticStepCounter = 0
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> handleStepCounterEvent(event)
            Sensor.TYPE_STEP_DETECTOR -> handleStepDetectorEvent(event)
            Sensor.TYPE_ACCELEROMETER -> handleAccelerometerEvent(event)
        }
    }

    private fun handleStepCounterEvent(event: SensorEvent) {
        val rawValue = event.values[0].toInt()
        _latestRawSensorValue.value = rawValue

        if (isPaused) {
            baselineSensorValue = rawValue
            return
        }

        if (baselineSensorValue < 0) {
            baselineSensorValue = rawValue
            return
        }

        // Reboot recovery
        if (rawValue < baselineSensorValue) {
            baselineSensorValue = rawValue
            return
        }

        val delta = rawValue - baselineSensorValue
        if (delta > 0) {
            baselineSensorValue = rawValue
            notifyStepDelta(delta, rawValue)
        }
    }

    private fun handleStepDetectorEvent(event: SensorEvent) {
        if (isPaused) return

        val detected = event.values[0].toInt()
        if (detected >= 1) {
            syntheticStepCounter += detected
            _latestRawSensorValue.value = syntheticStepCounter
            notifyStepDelta(detected, syntheticStepCounter)
        }
    }

    private fun handleAccelerometerEvent(event: SensorEvent) {
        if (isPaused) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val rawMagnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        // Low-pass exponential moving average filter (alpha = 0.8) to remove sensor jitter
        smoothedMagnitude = 0.80f * smoothedMagnitude + 0.20f * rawMagnitude

        // Slowly track dynamic gravity baseline
        dynamicGravityBaseline = 0.98f * dynamicGravityBaseline + 0.02f * rawMagnitude

        val deltaFromBaseline = smoothedMagnitude - dynamicGravityBaseline

        // Peak detection with refractory window
        if (!isWaveAboveThreshold && deltaFromBaseline > stepThreshold) {
            isWaveAboveThreshold = true
        } else if (isWaveAboveThreshold && deltaFromBaseline < 0.2f) {
            // Wave passed peak and crossed back toward baseline
            isWaveAboveThreshold = false

            val now = System.currentTimeMillis()
            val elapsed = now - lastDetectedStepTimestampMs

            // Refractory cadence window: Human steps occur between ~250ms (fast sprint) and 2000ms (slow stroll)
            if (elapsed in 250L..2000L || lastDetectedStepTimestampMs == 0L) {
                lastDetectedStepTimestampMs = now
                syntheticStepCounter++
                _latestRawSensorValue.value = syntheticStepCounter
                notifyStepDelta(1, syntheticStepCounter)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }
}
