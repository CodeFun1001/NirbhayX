package com.img.nirbhayx.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.img.nirbhayx.Screen
import com.img.nirbhayx.services.SosConfirmationActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NirbhayXMainScaffold(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val isOverlayVisible = drawerState.isOpen || showBottomSheet

    val allScreens = listOf(
        Screen.DrawerScreen.Home,
        Screen.DrawerScreen.Profile,
        Screen.DrawerScreen.EmergencyContacts,
        Screen.DrawerScreen.EmergencySharing,
        Screen.DrawerScreen.Community,
        Screen.DrawerScreen.SafetyTips
    )
    val currentScreen = allScreens.find { it.dRoute == currentRoute }
    val screenTitle = currentScreen?.dITitle ?: "NirbhayX"
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NirbhayXDrawer(
                currentRoute = currentRoute,
                onNavigate = onNavigate,
                onClose = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        },
        content = {
            Box {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    topBar = {
                        NirbhayXTopAppBar(
                            title = screenTitle,
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            },
                            onEmergencyClick = { showBottomSheet = true }
                        )
                    },
                    floatingActionButton = {
                        if (currentRoute != Screen.DrawerScreen.EmergencyContacts.dRoute && currentRoute != Screen.DrawerScreen.Profile.dRoute) {
                            SosButton(onSosClick = {
                                val intent =
                                    Intent(context, SosConfirmationActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                                                Intent.FLAG_ACTIVITY_NO_HISTORY
                                    }
                                context.startActivity(intent)
                            })
                        }
                    },
                    bottomBar = {
                        NirbhayXBottomNavBar(
                            currentRoute = currentRoute,
                            onNavigate = onNavigate
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) { paddingValues ->
                    content(paddingValues)
                }
            }
            if (isOverlayVisible) {
                val blurRadius = 20f

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.15f),
                                    Color.Black.copy(alpha = 0.3f)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                )
            }

        }
    )

    if (showBottomSheet) {
        NirbhayXBottomSheetModal(
            onNavigate = onNavigate,
            onDismiss = { showBottomSheet = false }
        )
    }
}