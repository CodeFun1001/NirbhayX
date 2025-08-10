package com.img.nirbhayx.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.img.nirbhayx.MainActivity
import com.img.nirbhayx.R

class CommunityNotificationListener(private val context: Context) {

    private val TAG = "CommunityNotificationListener"
    private val firestore = FirebaseFirestore.getInstance()
    private var globalNotificationListener: ListenerRegistration? = null
    private var locationNotificationListener: ListenerRegistration? = null

    companion object {
        private const val CHANNEL_ID = "community_alerts"
        private const val NOTIFICATION_ID_BASE = 2000
        private var notificationCounter = 0
    }

    init {
        createNotificationChannel()
    }

    fun startListening() {
        val currentUserId = FirebaseAuth.getInstance().uid ?: return

        listenForGlobalNotifications(currentUserId)

        Log.d(TAG, "Started listening for community notifications")
    }

    fun stopListening() {
        globalNotificationListener?.remove()
        locationNotificationListener?.remove()

        globalNotificationListener = null
        locationNotificationListener = null

        Log.d(TAG, "Stopped listening for community notifications")
    }

    private fun listenForGlobalNotifications(currentUserId: String) {
        globalNotificationListener = firestore
            .collection("global_notifications")
            .whereEqualTo("processed", false)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed for global notifications", e)
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach { change ->
                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        val doc = change.document
                        val data = doc.data

                        val alertData = data["data"] as? Map<String, Any>
                        val senderUserId = alertData?.get("userId") as? String

                        if (senderUserId != currentUserId) {
                            val title = data["title"] as? String ?: "Community Alert"
                            val body = data["body"] as? String ?: "Someone needs help"

                            showNotification(title, body, alertData ?: emptyMap())

                            doc.reference.update("processed", true)
                        }
                    }
                }
            }
    }

    fun listenForLocationNotifications(latitude: Double, longitude: Double, currentUserId: String) {
        val locationTopic = getLocationTopic(latitude, longitude)

        locationNotificationListener = firestore
            .collection("notification_triggers")
            .whereEqualTo("topic", locationTopic)
            .whereEqualTo("processed", false)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed for location notifications", e)
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach { change ->
                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        val doc = change.document
                        val data = doc.data

                        val alertData = data["data"] as? Map<String, Any>
                        val senderUserId = alertData?.get("userId") as? String

                        if (senderUserId != currentUserId) {
                            val title = data["title"] as? String ?: "Community Alert"
                            val body = data["body"] as? String ?: "Someone nearby needs help"

                            showNotification(title, body, alertData ?: emptyMap())

                            doc.reference.update("processed", true)
                        }
                    }
                }
            }
    }

    private fun getLocationTopic(latitude: Double, longitude: Double): String {
        val latRounded = (latitude * 100).toInt()
        val lonRounded = (longitude * 100).toInt()
        return "alerts_${latRounded}_${lonRounded}"
    }

    private fun showNotification(title: String, body: String, data: Map<String, Any>) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "community")
            data.forEach { (key, value) ->
                putExtra(key, value.toString())
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationCounter++,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alert)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            NOTIFICATION_ID_BASE + notificationCounter,
            notificationBuilder.build()
        )

        Log.d(TAG, "Notification shown: $title")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Community Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for community emergency alerts"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
