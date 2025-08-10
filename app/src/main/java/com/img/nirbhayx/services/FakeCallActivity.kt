package com.img.nirbhayx.services

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.img.nirbhayx.R
import com.img.nirbhayx.data.ActivityLog
import com.img.nirbhayx.data.Graph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FakeCallActivity : ComponentActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private var isCallOngoing = false

    @RequiresApi(Build.VERSION_CODES.O_MR1)
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

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        }

        Log.d("FakeCall", "Fake call activity started")

        setupRingtone()

        setupVibration()

        setContent {
            if (isCallOngoing) {
                CallOngoingScreen(activity = this)
            } else {
                FakeCallScreen(
                    onAccept = {
                        mediaPlayer.stop()
                        isCallOngoing = true
                        startCallTimer()
                    },
                    onReject = {
                        mediaPlayer.stop()
                        finish()
                    }
                )
            }
        }

        insertToRoom("📞 Fake call launched")
    }

    private fun setupRingtone() {
        try {
            val ringtoneUri =
                RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE)

            if (ringtoneUri != null) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(this@FakeCallActivity, ringtoneUri)
                    setAudioStreamType(AudioManager.STREAM_RING)
                    isLooping = true
                    prepare()
                    start()
                }
            } else {
                mediaPlayer = MediaPlayer.create(this, R.raw.default_ringtone).apply {
                    isLooping = true
                    start()
                }
            }
        } catch (e: Exception) {
            Log.e("FakeCall", "Error setting up ringtone", e)
            try {
                mediaPlayer = MediaPlayer.create(this, R.raw.default_ringtone).apply {
                    isLooping = true
                    start()
                }
            } catch (ex: Exception) {
                Log.e("FakeCall", "Failed to create default ringtone", ex)
            }
        }
    }

    private fun setupVibration() {
        try {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        1000,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(1000)
            }
        } catch (e: Exception) {
            Log.e("FakeCall", "Error with vibration", e)
        }
    }

    private fun startCallTimer() {
        setContent {
            CallOngoingScreen(activity = this)
        }
    }

    override fun onDestroy() {
        try {
            if (::mediaPlayer.isInitialized) {
                if (mediaPlayer.isPlaying) mediaPlayer.stop()
                mediaPlayer.release()
            }
        } catch (e: Exception) {
            Log.e("FakeCall", "Error releasing media player", e)
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("FakeCall", "Back button pressed - ignoring")
    }

    private fun insertToRoom(message: String) {
        val log = ActivityLog(
            timestamp = System.currentTimeMillis(),
            description = message
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Graph.activityRepository.insert(log)
            } catch (e: Exception) {
                Log.e("FakeCall", "Error inserting to room", e)
            }
        }
    }
}

@Composable
fun FakeCallScreen(onAccept: () -> Unit, onReject: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2A2A2A),
                        Color(0xFF1A1A1A),
                        Color(0xFF0A0A0A)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp)
                .background(Color.Black.copy(alpha = 0.6f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(80.dp))

                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape),
                    tint = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Dad",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "+91 9876543210",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Status
                Text(
                    text = "Incoming call...",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickActionButton(
                        icon = Icons.Default.Message,
                        label = "Message",
                        onClick = { }
                    )
                    QuickActionButton(
                        icon = Icons.Default.Call,
                        label = "Remind me",
                        onClick = { }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CallActionButton(
                        icon = Icons.Default.CallEnd,
                        backgroundColor = Color.Red,
                        iconTint = Color.White,
                        size = 70.dp,
                        onClick = onReject
                    )

                    CallActionButton(
                        icon = Icons.Default.Call,
                        backgroundColor = Color.Green,
                        iconTint = Color.White,
                        size = 70.dp,
                        onClick = onAccept
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun CallOngoingScreen(activity: FakeCallActivity) {
    var seconds by remember { mutableStateOf(0) }
    val formatter = remember(seconds) {
        val mins = seconds / 60
        val secs = seconds % 60
        String.format("%02d:%02d", mins, secs)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            seconds++
        }
    }

    val buttonStates = remember { mutableStateMapOf<String, Boolean>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2A2A2A),
                        Color(0xFF1A1A1A),
                        Color(0xFF0A0A0A)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(15.dp)
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    tint = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Mom",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "+91 9876543210",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatter,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            CallControlButton(
                                icon = Icons.Default.Notes,
                                label = "Notes",
                                isToggled = buttonStates["notes"] == true,
                                onClick = {
                                    buttonStates["notes"] = !(buttonStates["notes"] ?: false)
                                }
                            )
                            CallControlButton(
                                icon = Icons.Default.Add,
                                label = "Add call",
                                isToggled = false,
                                onClick = { }
                            )
                            CallControlButton(
                                icon = Icons.Default.MicOff,
                                label = "Mute",
                                isToggled = buttonStates["mute"] == true,
                                onClick = {
                                    buttonStates["mute"] = !(buttonStates["mute"] ?: false)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            CallControlButton(
                                icon = Icons.Default.VolumeUp,
                                label = "Speaker",
                                isToggled = buttonStates["speaker"] == true,
                                onClick = {
                                    buttonStates["speaker"] = !(buttonStates["speaker"] ?: false)
                                }
                            )
                            CallControlButton(
                                icon = Icons.Default.Videocam,
                                label = "Video call",
                                isToggled = false,
                                onClick = { }
                            )
                            CallControlButton(
                                icon = Icons.Default.Pause,
                                label = "Hold",
                                isToggled = buttonStates["hold"] == true,
                                onClick = {
                                    buttonStates["hold"] = !(buttonStates["hold"] ?: false)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CallActionButton(
                                icon = Icons.Default.CallEnd,
                                backgroundColor = Color.Red,
                                iconTint = Color.White,
                                size = 64.dp,
                                onClick = { activity.finish() }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp
        )
    }
}

@Composable
fun CallControlButton(
    icon: ImageVector,
    label: String,
    isToggled: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    if (isToggled) Color.White.copy(alpha = 0.3f) else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isToggled) Color.Yellow else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp
        )
    }
}

@Composable
fun CallActionButton(
    icon: ImageVector,
    backgroundColor: Color,
    iconTint: Color,
    size: Dp = 70.dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(size * 0.4f)
        )
    }
}