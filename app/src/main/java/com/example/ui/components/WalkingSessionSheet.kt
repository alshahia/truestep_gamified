package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.NaturePeople
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FlagCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.RollerSkating
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.local.WalkingSession
import com.example.domain.model.PaceZone
import com.example.ui.theme.tokens.appShapes
import com.example.ui.theme.tokens.dimens
import com.example.ui.theme.tokens.spacing
import com.example.util.MetricFormatter
import kotlinx.coroutines.delay
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

enum class WalkMode(val label: String, val icon: ImageVector, @androidx.annotation.StringRes val labelRes: Int = 0) {
    INDOOR("Indoor Walk", Icons.AutoMirrored.Filled.DirectionsWalk, R.string.walk_mode_indoor),
    OUTDOOR("Outdoor Walk", Icons.Default.NaturePeople, R.string.walk_mode_outdoor),
    TREADMILL("Treadmill", Icons.Default.FitnessCenter, R.string.walk_mode_treadmill)
}

enum class SessionState {
    IDLE,
    RUNNING,
    PAUSED,
    COMPLETED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalkingSessionSheet(
    sheetState: SheetState,
    walkingSessions: List<WalkingSession>,
    currentDaySteps: Int,
    onSaveSession: (WalkingSession) -> Unit,
    onDeleteSession: (Long) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedMode by remember { mutableStateOf(WalkMode.INDOOR) }
    var useGpsForOutdoor by remember { mutableStateOf(false) }
    var gpsPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val registryOwner = LocalActivityResultRegistryOwner.current
    val locationPermissionLauncher = if (registryOwner != null) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            gpsPermissionGranted = isGranted
            if (!isGranted) {
                useGpsForOutdoor = false
            }
        }
    } else null

    var showLocationDisclosure by remember { mutableStateOf(false) }

    var sessionState by remember { mutableStateOf(SessionState.IDLE) }
    var elapsedSeconds by remember { mutableLongStateOf(0L) }
    var briskSeconds by remember { mutableLongStateOf(0L) }
    var sessionSteps by remember { mutableIntStateOf(0) }
    var startDaySteps by remember { mutableIntStateOf(0) }
    var sessionNote by remember { mutableStateOf("") }
    var startTimeMillis by remember { mutableLongStateOf(0L) }
    var lapCount by remember { mutableIntStateOf(1) }
    var lapToastText by remember { mutableStateOf<String?>(null) }

    // Session stopwatch timer and brisk pace detection
    LaunchedEffect(sessionState) {
        if (sessionState == SessionState.RUNNING) {
            while (true) {
                delay(1000L)
                elapsedSeconds++
                val currentSpm = if (elapsedSeconds > 3) {
                    ((sessionSteps.toDouble() / elapsedSeconds) * 60).roundToInt()
                } else 0
                if (currentSpm >= 100) {
                    briskSeconds++
                }
            }
        }
    }

    // Step calculation during session
    LaunchedEffect(currentDaySteps, sessionState) {
        if (sessionState == SessionState.RUNNING) {
            if (startDaySteps == 0 && currentDaySteps > 0) {
                startDaySteps = currentDaySteps
            }
            val delta = maxOf(0, currentDaySteps - startDaySteps)
            sessionSteps = delta
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = MaterialTheme.appShapes.bottomSheet,
        modifier = modifier.testTag("walking_session_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.xl, vertical = MaterialTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.walk_session_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.walk_session_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            when (sessionState) {
                SessionState.IDLE -> {
                    // Mode Selector
                    Text(text = stringResource(R.string.choose_session_type), style = MaterialTheme.typography.labelLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                    ) {
                        WalkMode.entries.forEach { mode ->
                            FilterChip(
                                selected = selectedMode == mode,
                                onClick = { selectedMode = mode },
                                label = { Text(stringResource(mode.labelRes)) },
                                leadingIcon = { Icon(imageVector = mode.icon, contentDescription = null, modifier = Modifier.size(MaterialTheme.dimens.iconSmall)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Optional GPS toggle for Outdoor
                    AnimatedVisibility(visible = selectedMode == WalkMode.OUTDOOR) {
                        Card(
                            shape = MaterialTheme.appShapes.medium,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(MaterialTheme.spacing.md),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(imageVector = Icons.Default.GpsFixed, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
                                    Column {
                                        Text(text = stringResource(R.string.optional_gps_route), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        Text(text = stringResource(R.string.optional_gps_route_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Switch(
                                    checked = useGpsForOutdoor,
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            if (!gpsPermissionGranted) {
                                                showLocationDisclosure = true
                                            } else {
                                                useGpsForOutdoor = true
                                            }
                                        } else {
                                            useGpsForOutdoor = false
                                        }
                                    }
                                )
                            }
                        }
                    }

                    if (showLocationDisclosure) {
                        AlertDialog(
                            onDismissRequest = {
                                showLocationDisclosure = false
                                useGpsForOutdoor = false
                            },
                            title = { Text(text = stringResource(R.string.location_disclosure_title)) },
                            text = { Text(text = stringResource(R.string.location_disclosure_desc)) },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showLocationDisclosure = false
                                        useGpsForOutdoor = true
                                        locationPermissionLauncher?.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                    }
                                ) {
                                    Text(text = stringResource(R.string.allow_location))
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        showLocationDisclosure = false
                                        useGpsForOutdoor = false
                                    }
                                ) {
                                    Text(text = stringResource(R.string.cancel))
                                }
                            }
                        )
                    }

                    Button(
                        onClick = {
                            sessionState = SessionState.RUNNING
                            elapsedSeconds = 0L
                            briskSeconds = 0L
                            sessionSteps = 0
                            startDaySteps = currentDaySteps
                            startTimeMillis = System.currentTimeMillis()
                        },
                        shape = MaterialTheme.appShapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MaterialTheme.dimens.buttonHeight)
                            .testTag("start_walk_session_button")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
                        Text(text = stringResource(R.string.start_session_format, stringResource(selectedMode.labelRes)))
                    }

                    // Recent sessions list if any
                    if (walkingSessions.isNotEmpty()) {
                        Text(text = stringResource(R.string.past_walking_sessions), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        val timeFormatter = remember { DateTimeFormatter.ofPattern("MMM d • h:mm a").withZone(ZoneId.systemDefault()) }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                        ) {
                            walkingSessions.take(4).forEach { session ->
                                Card(
                                    shape = MaterialTheme.appShapes.medium,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(MaterialTheme.spacing.md),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            val mins = (session.durationSeconds / 60).toInt()
                                            Text(text = stringResource(R.string.session_summary_item_format, session.sessionType, mins), fontWeight = FontWeight.Bold)
                                            Text(
                                                text = stringResource(R.string.session_steps_km_format, session.steps, session.distanceKm),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        IconButton(onClick = { onDeleteSession(session.id) }) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(R.string.delete_session), tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                SessionState.RUNNING, SessionState.PAUSED -> {
                    // Active Session Dashboard with Cybernetic Cadence Supercharge HUD
                    val currentCadence = if (elapsedSeconds > 3) {
                        ((sessionSteps.toDouble() / elapsedSeconds) * 60).roundToInt().coerceAtMost(250)
                    } else 0
                    val isSupercharged = currentCadence >= 115
                    val paceZone = PaceZone.fromSpm(currentCadence)
                    val distKm = (sessionSteps * 0.000762 * 100.0).roundToInt() / 100.0
                    val paceMinPerKm = if (distKm > 0) (elapsedSeconds / 60.0 / distKm) else 5.2
                    val paceMins = paceMinPerKm.toInt()
                    val paceSecs = ((paceMinPerKm - paceMins) * 60).toInt()
                    val paceFormatted = "${paceMins}'${paceSecs.toString().padStart(2, '0')}\""

                    Column(
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
                        modifier = Modifier.fillMaxWidth().testTag("active_walk_session_card")
                    ) {
                        // Live HUD Header Strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(MaterialTheme.appShapes.pill)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (selectedMode == WalkMode.OUTDOOR && useGpsForOutdoor) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                                )
                                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                                Text(
                                    text = if (selectedMode == WalkMode.OUTDOOR && useGpsForOutdoor) "GPS TRACKING" else stringResource(selectedMode.labelRes).uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Cadence Pace Zone Indicator Pill
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(MaterialTheme.appShapes.pill)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs)
                            ) {
                                Text(
                                    text = "${stringResource(paceZone.titleRes)} • ${currentCadence} SPM",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Central Hero Telemetry Card with Circular Cadence Dial
                        Card(
                            shape = MaterialTheme.appShapes.extraLarge,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(MaterialTheme.spacing.lg),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(selectedMode.labelRes).uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = MetricFormatter.formatSteps(sessionSteps),
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = MetricFormatter.formatDuration(elapsedSeconds),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                // Circular Cadence Gauge
                                val primaryCadenceColor = MaterialTheme.colorScheme.primary
                                val cadenceGaugeTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                val cadenceGaugeActiveColor = if (isSupercharged) primaryCadenceColor else MaterialTheme.colorScheme.secondary
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(92.dp)
                                ) {
                                    val sweepFraction = (currentCadence.toFloat() / 160f).coerceIn(0f, 1f)
                                    Canvas(modifier = Modifier.size(86.dp)) {
                                        drawArc(
                                            color = cadenceGaugeTrackColor,
                                            startAngle = 140f,
                                            sweepAngle = 260f,
                                            useCenter = false,
                                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                        )
                                        drawArc(
                                            color = cadenceGaugeActiveColor,
                                            startAngle = 140f,
                                            sweepAngle = sweepFraction * 260f,
                                            useCenter = false,
                                            style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "$currentCadence",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "SPM",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // 4-tile Biometrics Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                        ) {
                            // Distance
                            Card(
                                shape = MaterialTheme.appShapes.medium,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(MaterialTheme.spacing.sm)) {
                                    Text(text = stringResource(R.string.km_unit).uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                                    Text(text = "${MetricFormatter.formatDistance(distKm.toFloat())} km", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                            // Avg Pace
                            Card(
                                shape = MaterialTheme.appShapes.medium,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(MaterialTheme.spacing.sm)) {
                                    Text(text = "PACE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                                    Text(text = "$paceFormatted/km", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                            // Brisk Time
                            Card(
                                shape = MaterialTheme.appShapes.medium,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(MaterialTheme.spacing.sm)) {
                                    Text(text = stringResource(R.string.brisk_active_minutes).uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                                    Text(text = "${(briskSeconds / 60)} min", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                            // Calories
                            Card(
                                shape = MaterialTheme.appShapes.medium,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(MaterialTheme.spacing.sm)) {
                                    Text(text = stringResource(R.string.kcal_unit).uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                                    Text(text = "${(sessionSteps * 0.04).roundToInt()} kcal", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Lap toast if active
                        lapToastText?.let { toast ->
                            Text(
                                text = toast,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.appShapes.pill)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(vertical = MaterialTheme.spacing.xs),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        // In-Stride Encounter Challenge Card (Gamified Athletic HUD)
                        val superchargedColor = MaterialTheme.colorScheme.tertiary
                        Card(
                            shape = MaterialTheme.appShapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSupercharged) superchargedColor.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("hud_encounter_challenge_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(MaterialTheme.spacing.md),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (isSupercharged) superchargedColor else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SportsMartialArts,
                                            contentDescription = null,
                                            tint = if (isSupercharged) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
                                    Column {
                                        Text(
                                            text = stringResource(R.string.hud_encounter_title),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSupercharged) superchargedColor else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = stringResource(R.string.hud_encounter_desc),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(MaterialTheme.appShapes.pill)
                                        .background(if (isSupercharged) superchargedColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isSupercharged) stringResource(R.string.hud_encounter_optimal) else stringResource(R.string.hud_encounter_reward),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSupercharged) superchargedColor else MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        // Controls Row (Lap Split / Pause / Finish)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                        ) {
                            // Split Lap
                            FilledTonalButton(
                                onClick = {
                                    lapCount++
                                    lapToastText = "Lap $lapCount: $sessionSteps steps"
                                },
                                shape = MaterialTheme.appShapes.medium,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.FlagCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                                Text("Lap $lapCount", fontSize = 12.sp)
                            }

                            // Pause / Resume
                            FilledTonalButton(
                                onClick = {
                                    sessionState = if (sessionState == SessionState.RUNNING) SessionState.PAUSED else SessionState.RUNNING
                                },
                                shape = MaterialTheme.appShapes.medium,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (sessionState == SessionState.RUNNING) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                                Text(stringResource(if (sessionState == SessionState.RUNNING) R.string.pause else R.string.resume), fontSize = 12.sp)
                            }

                            // Finish Button
                            Button(
                                onClick = {
                                    sessionState = SessionState.COMPLETED
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                shape = MaterialTheme.appShapes.medium,
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("finish_walk_session_button")
                            ) {
                                Icon(imageVector = Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                                Text(stringResource(R.string.finish_walk_button), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                SessionState.COMPLETED -> {
                    // Calm, Biometric Walking Session Summary & Save
                    val distKm = (sessionSteps * 0.000762 * 100.0).roundToInt() / 100.0
                    val avgCadence = if (elapsedSeconds > 0) ((sessionSteps.toDouble() / elapsedSeconds) * 60).roundToInt() else 0
                    val caloriesKcal = (sessionSteps * 0.04).roundToInt()

                    Card(
                        shape = MaterialTheme.appShapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.dimens.elevationLow),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("session_summary_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(MaterialTheme.spacing.lg),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
                        ) {
                            Text(text = stringResource(R.string.walk_completed_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                            // Biometrics recap
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = stringResource(R.string.steps_unit).uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = MetricFormatter.formatSteps(sessionSteps), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                Column {
                                    Text(text = stringResource(R.string.km_unit).uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "${MetricFormatter.formatDistance(distKm.toFloat())} km", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                Column {
                                    Text(text = "CADENCE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "$avgCadence SPM", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Column {
                                    Text(text = stringResource(R.string.kcal_unit).uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "$caloriesKcal kcal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Privacy & Data Transparency Indicator
                            Card(
                                shape = MaterialTheme.appShapes.medium,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
                                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "Privacy-safe local recording • 0 cloud transmission",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = sessionNote,
                                onValueChange = { sessionNote = it },
                                label = { Text(stringResource(R.string.session_notes_label)) },
                                placeholder = { Text(stringResource(R.string.session_notes_placeholder)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    val session = WalkingSession(
                                        sessionType = selectedMode.label,
                                        startTime = startTimeMillis,
                                        endTime = System.currentTimeMillis(),
                                        durationSeconds = elapsedSeconds,
                                        steps = sessionSteps,
                                        distanceKm = distKm,
                                        notes = sessionNote
                                    )
                                    onSaveSession(session)
                                    onDismiss()
                                },
                                shape = MaterialTheme.appShapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(MaterialTheme.dimens.buttonHeight)
                                    .testTag("save_walk_session_button")
                            ) {
                                Text(stringResource(R.string.save_walk_to_history))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
        }
    }
}
