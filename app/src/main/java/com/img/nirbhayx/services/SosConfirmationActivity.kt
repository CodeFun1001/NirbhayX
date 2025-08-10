package com.img.nirbhayx.services

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.img.nirbhayx.ui.theme.AlertRed
import com.img.nirbhayx.ui.theme.BoldCrimson
import com.img.nirbhayx.ui.theme.FireOrange
import kotlin.math.roundToInt

class SosConfirmationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        }

        Log.d("SosConfirmation", "SOS Confirmation activity started")

        setContent {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error),
            ) {
                SosConfirmationScreen(triggerSosConfirmed = ::triggerSosConfirmed)
            }
        }
    }

    private fun triggerSosConfirmed() {
        Log.d("SosConfirmation", "SOS confirmed by user")

        try {
            val sosServiceIntent = Intent(this, SosService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(sosServiceIntent)
            } else {
                startService(sosServiceIntent)
            }

            sendBroadcast(Intent(this, SosConfirmedReceiver::class.java))
            startFakeCall()

        } catch (e: Exception) {
            Log.e("SosConfirmation", "Error triggering SOS", e)
        }

        finish()
    }

    private fun startFakeCall() {
        try {
            val fakeCallIntent = Intent(this, FakeCallActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(fakeCallIntent)
            Log.d("SosConfirmation", "Fake call activity started")
        } catch (e: Exception) {
            Log.e("SosConfirmation", "Error starting fake call", e)
        }
    }
}

@Composable
fun SosConfirmationScreen(
    triggerSosConfirmed: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AlertRed,
                        BoldCrimson
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🚨 EMERGENCY",
                        style = MaterialTheme.typography.headlineLarge,
                        color = AlertRed,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Swipe to Confirm SOS",
                        style = MaterialTheme.typography.headlineSmall,
                        color = BoldCrimson,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Swipe button
            SwipeConfirmButton { triggerSosConfirmed() }
        }
    }
}

@Composable
fun SwipeConfirmButton(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit
) {
    val screenWidthPx = LocalDensity.current.run {
        LocalContext.current.resources.displayMetrics.widthPixels.toDp()
    }
    val trackHeight = 80.dp
    val buttonSize = 64.dp
    val cornerRadius = trackHeight / 2
    val gradient = Brush.horizontalGradient(listOf(AlertRed, FireOrange, BoldCrimson))

    var rawOffset by remember { mutableStateOf(0f) }
    val trackWidth = remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    val animatedOffset by animateFloatAsState(
        targetValue = rawOffset,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight)
            .padding(horizontal = 32.dp)
            .onGloballyPositioned { trackWidth.value = it.size.width.toFloat() }
            .background(gradient, shape = RoundedCornerShape(cornerRadius))
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { },
                    onDragEnd = {
                        val completionX = trackWidth.value - with(density) { buttonSize.toPx() }
                        if (rawOffset >= completionX * 0.7f) {
                            onConfirm()
                            rawOffset = 0f
                        } else {
                            rawOffset = 0f
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        rawOffset = (rawOffset + dragAmount).coerceIn(
                            0f,
                            trackWidth.value - with(density) { buttonSize.toPx() }
                        )
                        change.consume()
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Swipe to Confirm Emergency →",
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .size(buttonSize)
                .background(Color.White, shape = RoundedCornerShape(32.dp))
                .border(3.dp, BoldCrimson, shape = RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Swipe",
                tint = FireOrange,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}