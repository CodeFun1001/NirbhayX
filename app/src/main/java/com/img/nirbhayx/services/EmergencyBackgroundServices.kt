package com.img.nirbhayx.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class EmergencyBackgroundService : Service() {

    private val powerButtonReceiver = PowerButtonReceiver()
    private var isReceiverRegistered = false

    companion object {
        private const val TAG = "EmergencyBackgroundService"
        private const val NOTIFICATION_ID = 1000
        private const val CHANNEL_ID = "EMERGENCY_SERVICE_CHANNEL"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Emergency background service created")
        createNotificationChannel()
        registerPowerButtonReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Emergency background service started")

        val notification = createForegroundNotification()
        startForeground(NOTIFICATION_ID, notification)

        if (!isReceiverRegistered) {
            registerPowerButtonReceiver()
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Emergency Background Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps emergency features running in background"
                enableVibration(false)
                enableLights(false)
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Emergency Protection Active")
            .setContentText("Triple power button press for emergency")
            .setSmallIcon(android.R.drawable.ic_lock_power_off)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .setShowWhen(false)
            .build()
    }

    private fun registerPowerButtonReceiver() {
        if (!isReceiverRegistered) {
            try {
                val filter = IntentFilter().apply {
                    addAction(Intent.ACTION_SCREEN_ON)
                    addAction(Intent.ACTION_SCREEN_OFF)
                    addAction(Intent.ACTION_USER_PRESENT)
                    priority = IntentFilter.SYSTEM_HIGH_PRIORITY
                }

                registerReceiver(powerButtonReceiver, filter)
                isReceiverRegistered = true
                Log.d(TAG, "PowerButtonReceiver registered in background service")

            } catch (e: Exception) {
                Log.e(TAG, "Failed to register PowerButtonReceiver in background service", e)
            }
        }
    }

    private fun unregisterPowerButtonReceiver() {
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(powerButtonReceiver)
                isReceiverRegistered = false
                Log.d(TAG, "PowerButtonReceiver unregistered from background service")
            } catch (e: IllegalArgumentException) {
                Log.w(TAG, "Receiver was not registered in background service", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterPowerButtonReceiver()
        Log.d(TAG, "Emergency background service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}