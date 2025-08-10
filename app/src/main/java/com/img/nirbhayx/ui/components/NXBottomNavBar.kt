package com.img.nirbhayx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.img.nirbhayx.Screen
import com.img.nirbhayx.ui.theme.BottomBarGradient
import com.img.nirbhayx.ui.theme.PureWhite

@Composable
fun NirbhayXBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        Screen.DrawerScreen.Home,
        Screen.DrawerScreen.EmergencySharing,
        Screen.DrawerScreen.Profile
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .background(
                brush = BottomBarGradient,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            contentColor = PureWhite,
            modifier = Modifier
                .height(110.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            items.forEach { screen ->
                val isSelected = currentRoute == screen.dRoute

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = screen.icon),
                            contentDescription = screen.dITitle,
                            modifier = Modifier.size(if (isSelected) 24.dp else 22.dp),
                            tint = if (isSelected) PureWhite else PureWhite.copy(alpha = 0.7f)
                        )
                    },
                    label = {
                        Text(
                            text = screen.dITitle,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) PureWhite else PureWhite.copy(alpha = 0.7f)
                        )
                    },
                    selected = isSelected,
                    onClick = { onNavigate(screen.dRoute) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PureWhite,
                        selectedTextColor = PureWhite,
                        unselectedIconColor = PureWhite.copy(alpha = 0.7f),
                        unselectedTextColor = PureWhite.copy(alpha = 0.7f),
                        indicatorColor = PureWhite.copy(alpha = 0.15f)
                    )
                )
            }
        }
    }
}