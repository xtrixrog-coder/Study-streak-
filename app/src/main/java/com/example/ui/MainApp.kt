package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.SubjectsScreen
import com.example.ui.screens.TimerScreen

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

sealed class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Splash : Screen("splash", "Splash", Icons.Default.Home)
    object Dashboard : Screen("dashboard", "Home", Icons.Default.Home)
    object Subjects : Screen("subjects", "Subjects", Icons.Default.LibraryBooks)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Timer : Screen("timer", "Timer", Icons.Default.Home)
}

@Composable
fun MainApp(viewModel: StudyViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarScreens = listOf(
        Screen.Dashboard,
        Screen.Subjects,
        Screen.Calendar,
        Screen.Profile
    )
    val showBottomBar = currentRoute in bottomBarScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color(0xFF1A1C1E),
                    tonalElevation = 0.dp
                ) {
                    bottomBarScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Dashboard.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onNavigateToHome = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToTimer = { navController.navigate(Screen.Timer.route) }
                )
            }
            composable(Screen.Subjects.route) {
                SubjectsScreen(viewModel = viewModel)
            }
            composable(Screen.Calendar.route) {
                CalendarScreen(viewModel = viewModel)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(viewModel = viewModel)
            }
            composable(Screen.Timer.route) {
                TimerScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
