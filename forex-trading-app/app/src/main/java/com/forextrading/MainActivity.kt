@file:OptIn(ExperimentalMaterial3Api::class)

package com.forextrading

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.forextrading.ui.Screen
import com.forextrading.ui.bottomNavItems
import com.forextrading.ui.screens.*
import com.forextrading.ui.theme.ForexTradingAppTheme
import com.forextrading.viewmodel.ForexViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForexTradingAppTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    ForexTradingApp()
                }
            }
        }
    }
}

@Composable
fun ForexTradingApp() {
    val navController = rememberNavController()
    val viewModel: ForexViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forex Trading") }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(viewModel)
            }
            composable(Screen.DailyBias.route) {
                DailyBiasScreen(viewModel)
            }
            composable(Screen.Economic.route) {
                EconomicCalendarScreen(viewModel)
            }
            composable(Screen.News.route) {
                NewsScreen(viewModel)
            }
            composable(Screen.COT.route) {
                COTScreen(viewModel)
            }
        }
    }
}
