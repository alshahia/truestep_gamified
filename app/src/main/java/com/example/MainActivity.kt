package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.di.ServiceLocator
import com.example.data.sync.HealthConnectHelper
import com.example.data.sync.StepSyncWorker
import com.example.service.StepCounterForegroundService
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.DarkModeConfig
import com.example.ui.theme.StepTrackTheme
import com.example.ui.viewmodel.StepViewModel
import com.example.ui.viewmodel.StepViewModelFactory
import com.example.util.AppLanguage
import com.example.util.LocaleHelper
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: android.content.Context) {
        val lang = LocaleHelper.getPersistedLanguage(newBase)
        super.attachBaseContext(LocaleHelper.wrap(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Schedule periodic background sync worker
        scheduleSyncWorker()

        setContent {
            val repository = ServiceLocator.provideStepRepository(this)
            val sensorManager = ServiceLocator.provideStepSensorManager(this)
            val userPreferencesRepository = ServiceLocator.provideUserPreferencesRepository(this)

            val stepViewModel: StepViewModel = viewModel(
                factory = StepViewModelFactory(repository, sensorManager, userPreferencesRepository)
            )

            val uiState by stepViewModel.uiState.collectAsStateWithLifecycle()

            // Health Connect permissions contract
            val healthConnectLauncher = rememberLauncherForActivityResult(
                contract = androidx.health.connect.client.PermissionController.createRequestPermissionResultContract()
            ) { grantedPermissions ->
                if (grantedPermissions.containsAll(HealthConnectHelper.REQUIRED_PERMISSIONS)) {
                    stepViewModel.syncHealthConnect(this@MainActivity)
                }
            }

            // Runtime permission launcher for Notifications (Android 13+)
            val notificationLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { _ ->
                // After notification permission handled, proceed to Health Connect if available
                lifecycleScope.launch {
                    if (HealthConnectHelper.isHealthConnectAvailable(this@MainActivity)) {
                        if (!HealthConnectHelper.hasPermissions(this@MainActivity)) {
                            healthConnectLauncher.launch(HealthConnectHelper.REQUIRED_PERMISSIONS)
                        } else {
                            stepViewModel.syncHealthConnect(this@MainActivity)
                        }
                    }
                }
            }

            // Runtime permission launcher for Activity Recognition (Android 10+)
            val activityRecognitionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    StepCounterForegroundService.start(this@MainActivity)
                }
                // Sequentially proceed to notification permission check
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        // Notifications already granted, check Health Connect
                        lifecycleScope.launch {
                            if (HealthConnectHelper.isHealthConnectAvailable(this@MainActivity)) {
                                if (!HealthConnectHelper.hasPermissions(this@MainActivity)) {
                                    healthConnectLauncher.launch(HealthConnectHelper.REQUIRED_PERMISSIONS)
                                } else {
                                    stepViewModel.syncHealthConnect(this@MainActivity)
                                }
                            }
                        }
                    }
                }
            }

            val initialScreen = remember {
                val action = intent?.action
                if (action == "androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE" ||
                    action == "android.intent.action.VIEW_PERMISSION_USAGE"
                ) {
                    com.example.ui.navigation.Screen.Trust
                } else {
                    com.example.ui.navigation.Screen.Home
                }
            }

            LaunchedEffect(uiState.hasCompletedOnboarding, uiState.isPaused) {
                if (uiState.hasCompletedOnboarding && !uiState.isPaused) {
                    val hasActivityPerm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.ACTIVITY_RECOGNITION
                        ) == PackageManager.PERMISSION_GRANTED
                    } else true

                    if (hasActivityPerm) {
                        StepCounterForegroundService.start(this@MainActivity)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        activityRecognitionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                    }

                    if (HealthConnectHelper.isHealthConnectAvailable(this@MainActivity) &&
                        HealthConnectHelper.hasPermissions(this@MainActivity)
                    ) {
                        stepViewModel.syncHealthConnect(this@MainActivity)
                    }
                }
            }

            val isDark = when (uiState.darkModeConfig) {
                DarkModeConfig.SYSTEM -> isSystemInDarkTheme()
                DarkModeConfig.LIGHT -> false
                DarkModeConfig.DARK -> true
            }

            val localizedContext = remember(uiState.selectedLanguage) {
                LocaleHelper.wrap(this@MainActivity, uiState.selectedLanguage)
            }
            val localizedConfig = remember(localizedContext) {
                localizedContext.resources.configuration
            }

            val isRtl = uiState.selectedLanguage.isRtl ||
                (uiState.selectedLanguage == AppLanguage.SYSTEM &&
                 (java.util.Locale.getDefault().language.startsWith("ar") ||
                  localizedConfig.layoutDirection == android.view.View.LAYOUT_DIRECTION_RTL))

            val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            LaunchedEffect(uiState.selectedLanguage) {
                LocaleHelper.applyLanguage(this@MainActivity, uiState.selectedLanguage)
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedConfig,
                LocalLayoutDirection provides layoutDirection,
                LocalActivityResultRegistryOwner provides this@MainActivity,
                LocalOnBackPressedDispatcherOwner provides this@MainActivity
            ) {
                StepTrackTheme(
                    themeChoice = uiState.selectedTheme,
                    darkTheme = isDark
                ) {
                    AppNavigation(
                        viewModel = stepViewModel,
                        uiState = uiState,
                        initialScreen = initialScreen
                    )
                }
            }
        }
    }

    private fun scheduleSyncWorker() {
        val syncRequest = PeriodicWorkRequestBuilder<StepSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "StepTrackSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
