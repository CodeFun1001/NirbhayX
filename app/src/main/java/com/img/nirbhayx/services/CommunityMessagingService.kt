package com.img.nirbhayx.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.img.nirbhayx.MainActivity
import com.img.nirbhayx.R
import com.img.nirbhayx.utils.LocationUtils

class CommunityMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "CommunityMessaging"
        private const val CHANNEL_ID = "community_alerts"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received from: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            val alertType = remoteMessage.data["type"]
            if (alertType == "community_alert" || alertType == "emergency_alert") {
                handleCommunityAlert(remoteMessage.data)
            }
        }

        remoteMessage.notification?.let {
            showNotification(it.title ?: "Community Alert", it.body ?: "Someone nearby needs help!")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed FCM token: $token")
        sendTokenToServer(token)
    }

    private fun handleCommunityAlert(data: Map<String, String>) {
        val title = "🚨 Emergency Alert"
        val body = "${data["message"] ?: "Help needed!"}\n📍 ${data["location"] ?: "Unknown"}\n👤 ${data["username"] ?: "Unknown"}"
        showNotification(title, body, data)
    }

    private fun showNotification(title: String, body: String, data: Map<String, String>? = null) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "community")
            data?.forEach { (k, v) -> putExtra(k, v) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_sos)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Community Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun sendTokenToServer(token: String) {
        getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("fcm_token", token)
            .apply()
    }
}
