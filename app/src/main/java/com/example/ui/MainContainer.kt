package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.*
import com.example.R
import com.example.ui.theme.*

sealed class BottomNavScreen(val route: String, val stringId: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : BottomNavScreen("homeRoute", R.string.nav_home, Icons.Filled.Home)
    object Calculator : BottomNavScreen("calculatorRoute", R.string.nav_calculator, Icons.Filled.Calculate)
    object Records : BottomNavScreen("recordsRoute", R.string.nav_records, Icons.AutoMirrored.Filled.List)
    object Pending : BottomNavScreen("pendingRoute", R.string.nav_pending, Icons.Filled.Warning)
    object Settings : BottomNavScreen("settingsRoute", R.string.nav_settings, Icons.Filled.Settings)
}

val bottomNavItems = listOf(
    BottomNavScreen.Home,
    BottomNavScreen.Calculator,
    BottomNavScreen.Records,
    BottomNavScreen.Pending,
    BottomNavScreen.Settings
)

@Composable
fun MainContainer(viewModel: MainViewModel) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = DarkSurface) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = stringResource(screen.stringId)) },
                        label = { Text(stringResource(screen.stringId)) },
                        selected = selected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Saffron,
                            selectedTextColor = Saffron,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = DarkSurface
                        ),
                        onClick = {
                            bottomNavController.navigate(screen.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
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
            navController = bottomNavController,
            startDestination = BottomNavScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavScreen.Home.route) { HomeScreenReal(viewModel, bottomNavController) }
            composable(BottomNavScreen.Calculator.route) { CalculatorScreenReal(viewModel, bottomNavController) }
            composable(BottomNavScreen.Records.route) { RecordsScreenReal(viewModel, bottomNavController) }
            composable(BottomNavScreen.Pending.route) { PendingScreenReal(viewModel, bottomNavController) }
            composable(BottomNavScreen.Settings.route) { SettingsScreenReal(viewModel) }
            // Sub screens
            composable(Screen.NewRecord.route) { NewRecordScreenReal(viewModel, bottomNavController) }
            composable(
                route = Screen.RecordDetail.route,
                arguments = listOf(navArgument("recordId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("recordId") ?: 0
                RecordDetailScreenReal(viewModel, bottomNavController, id)
            }
        }
    }
}

