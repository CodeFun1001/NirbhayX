package com.img.nirbhayx

import androidx.compose.runtime.Composable
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
import com.img.nirbhayx.viewmodels.AuthViewModel
import com.img.nirbhayx.viewmodels.EmergencyContactsViewModel
import com.img.nirbhayx.viewmodels.EmergencySharingViewModel
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
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {

    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"


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
            Profile(modifier, navController, authViewModel, currentRoute, onNavigate = { route ->
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
