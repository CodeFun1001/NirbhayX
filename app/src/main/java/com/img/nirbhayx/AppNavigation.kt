package com.img.nirbhayx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.img.nirbhayx.ui.screens.HomeScreen
import com.img.nirbhayx.ui.screens.auth.LoginScreen
import com.img.nirbhayx.ui.screens.auth.SignUpScreen
import com.img.nirbhayx.ui.screens.auth.SplashScreen
import com.img.nirbhayx.ui.screens.features.EmergencyContacts
import com.img.nirbhayx.ui.screens.features.EmergencySharing
import com.img.nirbhayx.ui.screens.features.Profile
import com.img.nirbhayx.ui.screens.features.SafetyTips
import com.img.nirbhayx.ui.screens.more.About
import com.img.nirbhayx.ui.screens.more.Help
import com.img.nirbhayx.ui.screens.more.Settings
import com.img.nirbhayx.viewmodels.ActivityViewModel
import com.img.nirbhayx.viewmodels.AuthState
import com.img.nirbhayx.viewmodels.AuthViewModel
import com.img.nirbhayx.viewmodels.EmergencyContactsViewModel
import com.img.nirbhayx.viewmodels.EmergencySharingViewModel
import com.img.nirbhayx.viewmodels.MedicalInfoViewModel
import com.img.nirbhayx.viewmodels.SafetyTipsViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,
    activityViewModel: ActivityViewModel,
    emergencyContactsViewModel: EmergencyContactsViewModel,
    emergencySharingViewModel: EmergencySharingViewModel,
    safetyTipsViewModel: SafetyTipsViewModel,
    medicalInfoViewModel: MedicalInfoViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route ?: "splash"

    // Observe authentication state
    val authState by authViewModel.authState.observeAsState()

    // Handle authentication state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Unauthenticated -> {
                // Only navigate to login if we're not already on auth screens
                if (currentRoute !in listOf("login", "signup", "splash")) {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            is AuthState.Authenticated -> {
                // If user is authenticated and on auth screens, navigate to home
                if (currentRoute in listOf("login", "signup", "splash")) {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            else -> { /* Loading or Error states */ }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("login") {
            LoginScreen(modifier, navController, authViewModel)
        }
        composable("signup") {
            SignUpScreen(modifier, navController, authViewModel)
        }
        composable("home") {
            HomeScreen(
                modifier,
                navController,
                activityViewModel,
                currentRoute,
                onNavigate = { route ->
                    if (route != "home") {
                        navController.navigate(route)
                    }
                })
        }
        composable("profile") {
            Profile(modifier, navController, authViewModel,medicalInfoViewModel, currentRoute, onNavigate = { route ->
                if (route != "profile") {
                    navController.navigate(route)
                }
            })
        }
        composable("emergency_contacts") {
            EmergencyContacts(
                modifier,
                navController,
                emergencyContactsViewModel,
                currentRoute,
                onNavigate = { route ->
                    if (route != "emergency_contacts") {
                        navController.navigate(route)
                    }
                })
        }

        composable("emergency_sharing") {
            EmergencySharing(
                modifier,
                navController,
                emergencySharingViewModel,
                emergencyContactsViewModel,
                currentRoute,
                onNavigate = { route ->
                    if (route != "emergency_sharing") {
                        navController.navigate(route)
                    }
                })
        }

        composable("community") {
            com.img.nirbhayx.ui.screens.features.Community(onNavigate = { route ->
                if (route != "community") {
                    navController.navigate(route)
                }
            })
        }

        composable("safety_tips") {
            SafetyTips(navController, safetyTipsViewModel, currentRoute, onNavigate = { route ->
                if (route != "safety_tips") {
                    navController.navigate(route)
                }
            })
        }

        composable("settings") {
            Settings(navController, isDarkTheme, onThemeChange, currentRoute) { route ->
                if (route != "settings") navController.navigate(route)
            }
        }
        composable("about") {
            About(navController, currentRoute) { route ->
                if (route != "about") navController.navigate(route)
            }
        }
        composable("help") {
            Help(navController, currentRoute) { route ->
                if (route != "help") navController.navigate(route)
            }
        }
    }
}