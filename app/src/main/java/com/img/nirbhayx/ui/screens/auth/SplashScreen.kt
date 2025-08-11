package com.img.nirbhayx.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.img.nirbhayx.R
import com.img.nirbhayx.ui.theme.SaffronGlow
import com.img.nirbhayx.ui.theme.SunshineYellow
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(1500)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
                launchSingleTop = true
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(350.dp)
                    .border(
                        width = 12.dp,
                        color = SaffronGlow,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .background(SunshineYellow)
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nirbhayx_round_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.tagline),
                color = SaffronGlow,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}