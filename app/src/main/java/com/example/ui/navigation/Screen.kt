package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.R

sealed class Screen(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector,
    val iconDrawableRes: Int? = null
) {
    data object Home : Screen("today", R.string.nav_home, Icons.AutoMirrored.Filled.DirectionsWalk, R.drawable.ic_cyber_hud_radar)
    data object Journey : Screen("journey", R.string.nav_journey, Icons.Default.Explore, R.drawable.ic_cyber_dagger_expedition)
    data object Analytics : Screen("analytics", R.string.nav_analytics, Icons.Default.Insights, R.drawable.ic_cyber_analytics)
    data object History : Screen("history", R.string.nav_history, Icons.Default.CalendarToday, R.drawable.ic_chrono_archives)
    data object Goals : Screen("goals", R.string.nav_goals, Icons.Default.Tune, R.drawable.ic_system_matrix)
    data object Trust : Screen("trust", R.string.nav_trust, Icons.Default.Shield, R.drawable.ic_cyber_trust_shield)
    data object Settings : Screen("settings", R.string.nav_settings, Icons.Default.Settings, R.drawable.ic_cyber_neural_gear)

    companion object {
        val navItems: List<Screen>
            get() = listOf(Home, Journey, History, Goals, Settings)
    }
}
