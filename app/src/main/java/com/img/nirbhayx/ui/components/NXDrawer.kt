package com.img.nirbhayx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.img.nirbhayx.R
import com.img.nirbhayx.Screen
import com.img.nirbhayx.ui.theme.BrightOrange
import com.img.nirbhayx.ui.theme.HeroGradient
import com.img.nirbhayx.ui.theme.PureWhite

@Composable
fun NirbhayXDrawer(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.width(300.dp)
    ) {
        DrawerContent(
            currentRoute = currentRoute,
            onNavigate = onNavigate,
            onClose = onClose
        )
    }
}

@Composable
private fun DrawerContent(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit
) {
    val drawerItems = listOf(
        Screen.DrawerScreen.Home,
        Screen.DrawerScreen.Profile,
        Screen.DrawerScreen.EmergencyContacts,
        Screen.DrawerScreen.EmergencySharing,
        Screen.DrawerScreen.Community,
        Screen.DrawerScreen.SafetyTips
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        DrawerHeader()

        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = drawerItems,
                key = { it.dRoute }
            ) { screen ->
                ModernDrawerMenuItem(
                    screen = screen,
                    isSelected = currentRoute == screen.dRoute,
                    onClick = {
                        onNavigate(screen.dRoute)
                        onClose()
                    }
                )
            }
        }
    }
}

@Composable
private fun DrawerHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = HeroGradient,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            PureWhite.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_logo),
                        contentDescription = "NirbhayX",
                        tint = PureWhite,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PureWhite,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )

                Text(
                    text = stringResource(R.string.tagline),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PureWhite.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun ModernDrawerMenuItem(
    screen: Screen.DrawerScreen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        BrightOrange.copy(alpha = 0.15f)
    } else {
        Color.Transparent
    }

    val contentColor = if (isSelected) {
        BrightOrange
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isSelected) BrightOrange.copy(alpha = 0.15f)
                        else if (isSystemInDarkTheme()) Color.White
                        else Color.Black,
                        RoundedCornerShape(12.dp)
                    )
                    .shadow(
                        if (isSelected) 4.dp else 2.dp,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = screen.icon),
                    contentDescription = screen.dITitle,
                    tint = contentColor,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            if (isSelected) BrightOrange.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(12.dp)
                        )

                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Text(
                text = screen.dITitle,
                fontSize = 16.sp,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 0.3.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(BrightOrange, RoundedCornerShape(4.dp))
                )
            }
        }
    }
}