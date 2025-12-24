package com.forextrading.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object DailyBias : Screen("daily_bias", "Daily Bias", Icons.Default.TrendingUp)
    object Economic : Screen("economic", "Economic Calendar", Icons.Default.CalendarToday)
    object News : Screen("news", "News", Icons.Default.Article)
    object COT : Screen("cot", "COT Report", Icons.Default.Assessment)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.DailyBias,
    Screen.Economic,
    Screen.News,
    Screen.COT
)
