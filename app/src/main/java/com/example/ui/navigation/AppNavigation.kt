package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.GoalsScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JourneyScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TrustCenterScreen
import com.example.ui.screens.TrustCenterSheet
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.settings.model.SettingsActions
import com.example.ui.viewmodel.StepUiState
import com.example.ui.viewmodel.StepViewModel

private enum class StartupState {
    SPLASH,
    ONBOARDING,
    MAIN
}

@Composable
fun AppNavigation(
    viewModel: StepViewModel,
    uiState: StepUiState,
    modifier: Modifier = Modifier,
    initialShowSplash: Boolean = true,
    initialScreen: Screen = Screen.Home
) {
    var showSplash by rememberSaveable { mutableStateOf(if (initialScreen != Screen.Home) false else initialShowSplash) }
    var isReplayingOnboarding by rememberSaveable { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf<Screen>(initialScreen) }
    var showTrustCenterSheet by remember { mutableStateOf(false) }

    // When on secondary screen, hardware back button navigates back to Home
    if (currentScreen != Screen.Home && uiState.hasCompletedOnboarding && !showSplash && !isReplayingOnboarding) {
        BackHandler {
            currentScreen = Screen.Home
        }
    }

    // When replaying onboarding, hardware back button cancels replay
    if (isReplayingOnboarding) {
        BackHandler {
            isReplayingOnboarding = false
        }
    }

    val targetStartupState = when {
        showSplash -> StartupState.SPLASH
        !uiState.hasCompletedOnboarding || isReplayingOnboarding -> StartupState.ONBOARDING
        else -> StartupState.MAIN
    }

    AnimatedContent(
        targetState = targetStartupState,
        transitionSpec = {
            fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
        },
        label = "root_startup_transition",
        modifier = modifier.fillMaxSize()
    ) { state ->
        when (state) {
            StartupState.SPLASH -> {
                SplashScreen(
                    onSplashFinished = {
                        showSplash = false
                    },
                    isDataReady = uiState.isPreferencesLoaded
                )
            }
            StartupState.ONBOARDING -> {
                OnboardingScreen(
                    onFinish = { goal, carryingMode ->
                        viewModel.updateGoal(goal)
                        viewModel.updateCarryingMode(carryingMode)
                        viewModel.setOnboardingCompleted(true)
                        isReplayingOnboarding = false
                    }
                )
            }
            StartupState.MAIN -> {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .border(
                            BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        )
                        .testTag("bottom_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Screen.navItems.forEach { screen ->
                        val selected = currentScreen == screen
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentScreen = screen },
                            icon = {
                                if (screen.iconDrawableRes != null) {
                                    Icon(
                                        painter = painterResource(id = screen.iconDrawableRes),
                                        contentDescription = stringResource(screen.titleRes),
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = stringResource(screen.titleRes),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = stringResource(screen.titleRes),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.route}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val context = androidx.compose.ui.platform.LocalContext.current
                when (currentScreen) {
                    Screen.Home -> HomeScreen(
                        uiState = uiState,
                        onTogglePause = { viewModel.togglePause(context) },
                        onUpdateGoal = { viewModel.updateGoal(it) },
                        onScrubHour = { viewModel.setScrubHour(it) },
                        onSelectRange = { viewModel.setChartRange(it) },
                        onNavigateToDestination = { currentScreen = Screen.Journey },
                        onNavigateToJourney = { currentScreen = Screen.Journey },
                        onNavigateToGoals = { currentScreen = Screen.Goals },
                        onNavigateToHistory = { currentScreen = Screen.History },
                        onSaveWalkingSession = { viewModel.saveWalkingSession(it) },
                        onDeleteWalkingSession = { viewModel.deleteWalkingSession(it) },
                        onOpenTrustCenter = { showTrustCenterSheet = true },
                        onDismissCelebration = { viewModel.dismissCelebration() },
                        onShareCelebration = { ctx -> viewModel.shareProgress(ctx) },
                        onDismissReviewPrompt = { never -> viewModel.dismissReviewPrompt(never) },
                        onRateOnPlayStore = { ctx -> viewModel.onRateOnPlayStore(ctx) },
                        onSendFeedback = { ctx -> viewModel.onSendConstructiveFeedback(ctx) },
                        onRateApp = { ctx -> viewModel.onRateOnPlayStore(ctx) },
                        onClaimRaidRewards = { s, c, sh -> viewModel.claimRaidRewards(s, c, sh) },
                        onDismissWorkoutSummary = { viewModel.dismissWorkoutSummary() },
                        onDismissSystemNotification = { viewModel.dismissSystemNotification() },
                        onOpenQuestSystemPopup = { quest -> viewModel.openQuestDetail(quest) }
                    )
                    Screen.Journey -> JourneyScreen(
                        uiState = uiState,
                        onEquipGear = { slot, gearId -> viewModel.equipGear(slot, gearId) },
                        onTuneGear = { gearId -> viewModel.tuneGear(gearId) },
                        onAllocateStatPoint = { statType -> viewModel.allocateStatPoint(statType) },
                        onOpenQuestPopup = { quest -> viewModel.openQuestDetail(quest) }
                    )
                    Screen.History -> HistoryScreen(
                        uiState = uiState,
                        onSaveNotes = { date, notes -> viewModel.updateNotes(date, notes) },
                        onNavigateToAnalytics = { currentScreen = Screen.Analytics }
                    )
                    Screen.Goals -> GoalsScreen(
                        uiState = uiState,
                        onUpdateGoal = { viewModel.updateGoal(it) },
                        onUpdateDetailedGoals = { min, target, stretch -> viewModel.updateDetailedGoals(min, target, stretch) },
                        onToggleAdaptiveGoals = { viewModel.toggleAdaptiveGoals(it) },
                        onToggleLockGoals = { viewModel.toggleLockGoals(it) },
                        onToggleRecoveryMode = { viewModel.toggleRecoveryMode(it) },
                        onRecalculateAdaptiveGoal = { viewModel.recalculateAdaptiveGoal() },
                        onUpdateGoalSettings = { viewModel.updateGoalSettings(it) }
                    )
                    Screen.Trust -> TrustCenterScreen(
                        uiState = uiState
                    )
                    Screen.Analytics -> AnalyticsScreen(
                        uiState = uiState
                    )
                    Screen.Settings -> {
                        val settingsState by viewModel.settingsUiState.collectAsStateWithLifecycle()
                        SettingsScreen(
                            uiState = settingsState,
                            actions = SettingsActions(
                                onSelectTheme = { viewModel.setTheme(it) },
                                onSelectDarkMode = { viewModel.setDarkModeConfig(it) },
                                onSelectLanguage = { viewModel.setSelectedLanguage(it) },
                                onSelectCarryingMode = { viewModel.updateCarryingMode(it) },
                                onToggleAdaptiveGoals = { viewModel.toggleAdaptiveGoals(it) },
                                onToggleLockGoals = { viewModel.toggleLockGoals(it) },
                                onToggleRecoveryMode = { viewModel.toggleRecoveryMode(it) },
                                onRecalculateGoal = { viewModel.recalculateAdaptiveGoal() },
                                onToggleLiveNotification = { viewModel.toggleLiveNotification(it) },
                                onToggleHighContrast = { viewModel.toggleHighContrast(it) },
                                onSyncHealthConnect = { ctx -> viewModel.syncHealthConnect(ctx) },
                                onExportData = { ctx, isCsv -> viewModel.shareExportData(ctx, isCsv) },
                                onClearAllData = { viewModel.clearAllData() },
                                onLoadDemoData = { viewModel.loadDemoData() },
                                onOpenTrustCenter = { showTrustCenterSheet = true },
                                onReplayOnboarding = { isReplayingOnboarding = true }
                            )
                        )
                    }
                }
            }
        }
    }
        }
    }

    if (showTrustCenterSheet) {
        TrustCenterSheet(
            uiState = uiState,
            onDismissRequest = { showTrustCenterSheet = false }
        )
    }
}
