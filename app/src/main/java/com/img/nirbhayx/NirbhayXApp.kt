package com.img.nirbhayx

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.img.nirbhayx.data.Graph

class NirbhayXApp : Application() {

    companion object {
        private const val TAG = "NirbhayApplication"
        private const val ONESIGNAL_APP_ID = "8fd9e2bc-90ce-4abc-a39c-af5d0a436044"
    }

    override fun onCreate() {
        super.onCreate()

        Graph.initDatabase(this)

        createNotificationChannels()
        createCommunityChannel()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // SOS Confirmation Channel
            val sosChannel = NotificationChannel(
                "SOS_CHANNEL",
                "SOS Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Emergency SOS notifications"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }

            // Medical Info Channel (NEW)
            val medicalInfoChannel = NotificationChannel(
                "EMERGENCY_MEDICAL_INFO",
                "Emergency Medical Information",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Emergency medical information for first responders and bystanders"
                enableVibration(false) // No vibration to avoid interfering with SOS
                enableLights(true)
                setShowBadge(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }

            // Emergency Alerts Channel
            val emergencyChannel = NotificationChannel(
                "EMERGENCY_ALERTS_CHANNEL",
                "Emergency Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High-priority emergency notifications from nearby users"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }

            // Community Channel
            val communityChannel = NotificationChannel(
                "community_alerts",
                "Community Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Community safety alerts"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }

            // Emergency Background Service Channel
            val serviceChannel = NotificationChannel(
                "EMERGENCY_SERVICE_CHANNEL",
                "Emergency Background Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps emergency features running in background"
                enableVibration(false)
                enableLights(false)
                setShowBadge(false)
            }

            // Create all channels
            notificationManager.createNotificationChannel(sosChannel)
            notificationManager.createNotificationChannel(medicalInfoChannel)
            notificationManager.createNotificationChannel(emergencyChannel)
            notificationManager.createNotificationChannel(communityChannel)
            notificationManager.createNotificationChannel(serviceChannel)

            Log.d(TAG, "All notification channels created including medical info channel")
        }
    }

    private fun createCommunityChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "community_siren_channel"
            val name = "Community Alerts (Siren)"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = "Emergency alerts with siren sound"
                enableLights(true)
                enableVibration(true)

                val soundUri = Uri.parse("android.resource://${packageName}/raw/siren")
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                setSound(soundUri, audioAttributes)
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }
}