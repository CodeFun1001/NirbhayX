package com.img.nirbhayx.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.img.nirbhayx.R
import com.img.nirbhayx.ui.theme.DangerButtonGradient
import com.img.nirbhayx.ui.theme.GlowGradient
import com.img.nirbhayx.ui.theme.PureWhite

@Composable
fun SosButton(onSosClick: () -> Unit) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ), label = "glow"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(120.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(glowScale)
                .background(
                    brush = GlowGradient,
                    shape = CircleShape
                )
        )

        FloatingActionButton(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(
                            longArrayOf(0, 100, 50, 100),
                            -1
                        )
                    )
                } else {
                    vibrator.vibrate(200)
                }
                onSosClick()
            },
            containerColor = Color.Transparent,
            contentColor = PureWhite,
            modifier = Modifier
                .size(80.dp)
                .scale(scale)
                .border(4.dp, PureWhite, CircleShape)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape
                )
                .background(
                    brush = DangerButtonGradient,
                    shape = CircleShape
                )
                .clip(CircleShape),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sos),
                    contentDescription = "SOS",
                    tint = PureWhite,
                    modifier = Modifier.size(40.dp)

                )
            }
        }
    }
}