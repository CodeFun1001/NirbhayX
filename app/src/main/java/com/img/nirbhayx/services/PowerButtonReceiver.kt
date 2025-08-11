package com.img.nirbhayx.services

import android.Manifest
import android.R
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.img.nirbhayx.data.Graph

class PowerButtonReceiver : BroadcastReceiver() {

    companion object {
        private const val PRESS_TIMEOUT = 2000L
        private const val REQUIRED_PRESSES = 3
        private const val TAG = "PowerButtonReceiver"
        private const val PREFS_NAME = "power_button_prefs"
        private const val KEY_LAST_TIME = "last_time"
        private const val KEY_PRESS_COUNT = "press_count"
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "Received intent: ${intent?.action}")

        context?.let {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    Log.d(TAG, "Screen OFF - Power button pressed")
                    handlePowerButtonPress(it)
                }

                Intent.ACTION_SCREEN_ON -> {
                    Log.d(TAG, "Screen ON - Power button pressed")
                }

                Intent.ACTION_USER_PRESENT -> {
                    Log.d(TAG, "User present - resetting counter")
                    resetPressCount(it)
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun handlePowerButtonPress(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentTime = System.currentTimeMillis()
        val lastTime = sharedPrefs.getLong(KEY_LAST_TIME, 0)
        var pressCount = sharedPrefs.getInt(KEY_PRESS_COUNT, 0)

        Log.d(
            TAG,
            "Power button press detected. Current: $currentTime, Last: $lastTime, Count: $pressCount"
        )

        if (currentTime - lastTime < PRESS_TIMEOUT) {
            pressCount++
            Log.d(TAG, "Incremented press count to: $pressCount")
        } else {
            pressCount = 1
            Log.d(TAG, "Reset press count to 1 (timeout exceeded)")
        }

        sharedPrefs.edit()
            .putLong(KEY_LAST_TIME, currentTime)
            .putInt(KEY_PRESS_COUNT, pressCount)
            .apply()

        if (pressCount >= REQUIRED_PRESSES) {
            Log.d(TAG, "🚨 TRIPLE POWER PRESS DETECTED! Triggering SOS")
            resetPressCount(context)

            showMedicalInfoNotification(context)

            launchSosConfirmation(context)
        }
    }

    private fun resetPressCount(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_PRESS_COUNT, 0)
            .apply()
        Log.d(TAG, "Press count reset to 0")
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showMedicalInfoNotification(context: Context) {
        try {
            Log.d(TAG, "🩺 Showing medical info notification...")

            try {
                val testAccess = Graph.medicalInfoRepository
                Log.d(TAG, "✅ Graph repositories are accessible")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Graph repositories NOT initialized - this might cause issues", e)

                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    Log.d(TAG, "🔧 Attempting to initialize repositories for user: $userId")
                    try {
                        Graph.createRepositories(userId)
                        Log.d(TAG, "✅ Repositories initialized successfully")
                    } catch (initError: Exception) {
                        Log.e(TAG, "❌ Failed to initialize repositories", initError)
                    }
                }
            }

            val medicalIntent = Intent(context, EmergencyMedicalDisplayService::class.java).apply {
                action = EmergencyMedicalDisplayService.ACTION_SHOW_MEDICAL_INFO
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "🚀 Starting foreground service (API >= O)")
                try {
                    context.startForegroundService(medicalIntent)
                    Log.d(TAG, "✅ startForegroundService called successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "❌ startForegroundService failed", e)
                    try {
                        context.startService(medicalIntent)
                        Log.d(TAG, "✅ Fallback startService called")
                    } catch (fallbackError: Exception) {
                        Log.e(TAG, "❌ Both service start methods failed", fallbackError)
                    }
                }
            } else {
                Log.d(TAG, "🚀 Starting regular service (API < O)")
                context.startService(medicalIntent)
            }

            Log.d(TAG, "✅ Medical info notification service start requested")

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                Log.d(TAG, "🔍 Checking if medical service is running...")

                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
                val runningServices = activityManager.getRunningServices(Integer.MAX_VALUE)

                val medicalServiceRunning = runningServices.any {
                    it.service.className == "com.img.nirbhayx.services.EmergencyMedicalDisplayService"
                }

                Log.d(TAG, if (medicalServiceRunning) "✅ Medical service is running" else "❌ Medical service NOT running")

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val activeNotifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    notificationManager.activeNotifications
                } else {
                    emptyArray()
                }

                Log.d(TAG, "📱 Active notifications count: ${activeNotifications.size}")
                activeNotifications.forEach { notification ->
                    Log.d(TAG, "📱 Active notification: ID=${notification.id}, Tag=${notification.tag}")
                }

            }, 2000)

        } catch (e: Exception) {
            Log.e(TAG, "💥 Critical error in showMedicalInfoNotification", e)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun launchSosConfirmation(context: Context) {
        try {
            Log.d(TAG, "Launching SOS confirmation...")

            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wl = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE,
                "NirbhayX:SOS_WakeLock"
            )
            wl.acquire(5000)

            val intent = Intent(context, SosConfirmationActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_NO_HISTORY
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, "SOS_CHANNEL")
                .setContentTitle("🚨 EMERGENCY SOS TRIGGERED")
                .setContentText("Triple power button pressed! Tap to confirm.")
                .setSmallIcon(R.drawable.ic_dialog_alert)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setFullScreenIntent(pendingIntent, true)
                .setOngoing(false)
                .build()

            val notificationManager = NotificationManagerCompat.from(context)
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(1001, notification)
                Log.d(TAG, "SOS notification sent")
            }

            try {
                context.startActivity(intent)
                Log.d(TAG, "SOS confirmation activity started")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start SOS activity", e)
            }

            try {
                wl.release()
            } catch (e: Exception) {
                Log.e(TAG, "Error releasing wake lock", e)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error launching SOS confirmation", e)
        }
    }
}