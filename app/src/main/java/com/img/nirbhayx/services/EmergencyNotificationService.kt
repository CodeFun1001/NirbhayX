package com.img.nirbhayx.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.img.nirbhayx.MainActivity
import com.img.nirbhayx.R

class EmergencyNotificationService : Service() {

    companion object {
        private const val TAG = "EmergencyNotificationService"
        private const val NOTIFICATION_ID = 9999
        private const val CHANNEL_ID = "EMERGENCY_ALERTS_CHANNEL"
        private const val CHANNEL_NAME = "Emergency Alerts"
        private const val CHANNEL_DESCRIPTION = "High-priority emergency notifications from nearby users"

        // Action constants
        const val ACTION_SHOW_EMERGENCY = "SHOW_EMERGENCY"
        const val ACTION_DISMISS_EMERGENCY = "DISMISS_EMERGENCY"
        const val ACTION_VIEW_LOCATION = "VIEW_LOCATION"

        // Extra keys
        const val EXTRA_EMERGENCY_MESSAGE = "emergency_message"
        const val EXTRA_USERNAME = "username"
        const val EXTRA_CONTACT = "contact"
        const val EXTRA_LATITUDE = "latitude"
        const val EXTRA_LONGITUDE = "longitude"
        const val EXTRA_TIMESTAMP = "timestamp"
    }

    private lateinit var notificationManager: NotificationManager
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator
    private var isPlayingSiren = false

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "EmergencyNotificationService created")

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createEmergencyNotificationChannel()
        initializeMediaPlayer()
        initializeVibrator()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "EmergencyNotificationService started with action: ${intent?.action}")

        when (intent?.action) {
            ACTION_SHOW_EMERGENCY -> {
                val message = intent.getStringExtra(EXTRA_EMERGENCY_MESSAGE) ?: "Emergency alert from nearby user"
                val username = intent.getStringExtra(EXTRA_USERNAME) ?: "Unknown"
                val contact = intent.getStringExtra(EXTRA_CONTACT) ?: "N/A"
                val latitude = intent.getDoubleExtra(EXTRA_LATITUDE, 0.0)
                val longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, 0.0)
                val timestamp = intent.getLongExtra(EXTRA_TIMESTAMP, System.currentTimeMillis())

                showEmergencyNotification(message, username, contact, latitude, longitude, timestamp)
                startEmergencyAlerts()
            }

            ACTION_DISMISS_EMERGENCY -> {
                stopEmergencyAlerts()
                stopSelf()
            }

            ACTION_VIEW_LOCATION -> {
                val latitude = intent.getDoubleExtra(EXTRA_LATITUDE, 0.0)
                val longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, 0.0)
                openLocationInMaps(latitude, longitude)
            }
        }

        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createEmergencyNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
                setSound(null, null) // We'll handle sound manually
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setAllowBubbles(true)
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initializeMediaPlayer() {
        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer.isLooping = true
            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize MediaPlayer", e)
        }
    }

    private fun initializeVibrator() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private fun showEmergencyNotification(
        message: String,
        username: String,
        contact: String,
        latitude: Double,
        longitude: Double,
        timestamp: Long
    ) {
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            action = ACTION_VIEW_LOCATION
            putExtra(EXTRA_LATITUDE, latitude)
            putExtra(EXTRA_LONGITUDE, longitude)
        }

        val mainPendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val dismissIntent = Intent(this, EmergencyNotificationService::class.java).apply {
            action = ACTION_DISMISS_EMERGENCY
        }

        val dismissPendingIntent = PendingIntent.getService(
            this, 1, dismissIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val locationIntent = Intent(this, EmergencyNotificationService::class.java).apply {
            action = ACTION_VIEW_LOCATION
            putExtra(EXTRA_LATITUDE, latitude)
            putExtra(EXTRA_LONGITUDE, longitude)
        }

        val locationPendingIntent = PendingIntent.getService(
            this, 2, locationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🚨 Emergency Alert - $username needs help!")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setSmallIcon(R.drawable.ic_sos)
            .setLargeIcon(null as android.graphics.Bitmap?) // You can add user avatar here if available
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentIntent(mainPendingIntent)
            .addAction(R.drawable.ic_stop, "Dismiss", dismissPendingIntent)
            .addAction(R.drawable.ic_emergency_contacts, "View Location", locationPendingIntent)
            .setColor(getColor(R.color.red))
            .setLights(getColor(R.color.red), 1000, 1000)
            .setVibrate(longArrayOf(0, 1000, 500, 1000, 500, 1000))
            .setSound(null) // We'll handle sound manually
            .build()

        startForeground(NOTIFICATION_ID, notification)
        Log.d(TAG, "Emergency notification displayed")
    }

    private fun startEmergencyAlerts() {
        try {
            startSirenSound()

            startEmergencyVibration()

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                if (isPlayingSiren) {
                    stopSirenSound()
                }
            }, 30000) // 30 seconds

        } catch (e: Exception) {
            Log.e(TAG, "Failed to start emergency alerts", e)
        }
    }

    private fun startSirenSound() {
        try {
            if (!isPlayingSiren) {
                val sirenUri = Uri.parse("android.resource://${packageName}/raw/siren")
                mediaPlayer.setDataSource(this, sirenUri)
                mediaPlayer.prepare()
                mediaPlayer.start()
                isPlayingSiren = true
                Log.d(TAG, "Siren sound started")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play siren sound, trying default alarm", e)
            try {
                val defaultAlarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                mediaPlayer.setDataSource(this, defaultAlarmUri)
                mediaPlayer.prepare()
                mediaPlayer.start()
                isPlayingSiren = true
                Log.d(TAG, "Default alarm sound started as fallback")
            } catch (fallbackException: Exception) {
                Log.e(TAG, "Failed to play fallback alarm sound", fallbackException)
            }
        }
    }

    private fun stopSirenSound() {
        try {
            if (isPlayingSiren && mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                isPlayingSiren = false
                Log.d(TAG, "Siren sound stopped")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop siren sound", e)
        }
    }

    private fun startEmergencyVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationPattern = longArrayOf(0, 1000, 500, 1000, 500, 1000)
                val vibrationEffect = VibrationEffect.createWaveform(vibrationPattern, 0)
                vibrator.vibrate(vibrationEffect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 1000, 500, 1000, 500, 1000), 0)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start vibration", e)
        }
    }

    private fun stopEmergencyVibration() {
        try {
            vibrator.cancel()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop vibration", e)
        }
    }

    private fun stopEmergencyAlerts() {
        stopSirenSound()
        stopEmergencyVibration()
        Log.d(TAG, "Emergency alerts stopped")
    }

    private fun openLocationInMaps(latitude: Double, longitude: Double) {
        try {
            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(Emergency Location)")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }

            if (mapIntent.resolveActivity(packageManager) != null) {
                mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(mapIntent)
            } else {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(fallbackIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open location in maps", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopEmergencyAlerts()

        try {
            mediaPlayer.release()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to release MediaPlayer", e)
        }

        Log.d(TAG, "EmergencyNotificationService destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}