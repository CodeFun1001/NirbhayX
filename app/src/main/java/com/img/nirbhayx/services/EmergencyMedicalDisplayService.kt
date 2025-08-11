package com.img.nirbhayx.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.img.nirbhayx.R
import com.img.nirbhayx.data.Graph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EmergencyMedicalDisplayService : Service() {

    companion object {
        private const val TAG = "EmergencyMedicalDisplay"
        private const val NOTIFICATION_ID = 8888
        private const val CHANNEL_ID = "EMERGENCY_MEDICAL_INFO"
        private const val CHANNEL_NAME = "Emergency Medical Information"

        const val ACTION_SHOW_MEDICAL_INFO = "SHOW_MEDICAL_INFO"
        const val ACTION_DISMISS = "DISMISS_EMERGENCY"
    }

    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "EmergencyMedicalDisplayService created")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called with action: ${intent?.action}")

        when (intent?.action) {
            ACTION_SHOW_MEDICAL_INFO -> {
                showMedicalInfoNotification()
            }
            ACTION_DISMISS -> {
                Log.d(TAG, "Dismissing medical info service")
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        Log.d(TAG, "Creating notification channel")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                notificationManager.deleteNotificationChannel(CHANNEL_ID)
            } catch (e: Exception) {
                Log.w(TAG, "Could not delete existing channel", e)
            }

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Emergency medical information for first responders and bystanders"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created successfully")
        }
    }

    private fun showMedicalInfoNotification() {
        Log.d(TAG, "Starting medical info notification process")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationManagerCompat = NotificationManagerCompat.from(this)
            if (!notificationManagerCompat.areNotificationsEnabled()) {
                Log.e(TAG, "Notifications are disabled - cannot show medical info")
                showFallbackNotification()
                return
            }
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
        Log.d(TAG, "Using userId: $userId")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Fetching user profile...")
                val userProfile = getUserProfile(userId)
                Log.d(TAG, "User profile: $userProfile")

                val medicalInfo = try {
                    Log.d(TAG, "Fetching medical info...")
                    Graph.medicalInfoRepository.getMedicalInfo(userId).first()
                } catch (e: Exception) {
                    Log.e(TAG, "Error accessing medical info repository", e)
                    null
                }

                val emergencyContacts = try {
                    Log.d(TAG, "Fetching emergency contacts...")
                    Graph.emergencyContactRepository.getAllContacts().first()
                } catch (e: Exception) {
                    Log.e(TAG, "Error accessing emergency contacts repository", e)
                    emptyList()
                }

                val medicalText = buildMedicalInfoText(userProfile, medicalInfo, emergencyContacts)
                Log.d(TAG, "Medical info text built, length: ${medicalText.length}")

                val dismissIntent = Intent(this@EmergencyMedicalDisplayService, EmergencyMedicalDisplayService::class.java).apply {
                    action = ACTION_DISMISS
                }
                val dismissPendingIntent = PendingIntent.getService(
                    this@EmergencyMedicalDisplayService, 2, dismissIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                val notification = NotificationCompat.Builder(this@EmergencyMedicalDisplayService, CHANNEL_ID)
                    .setContentTitle("🏥 EMERGENCY MEDICAL INFO")
                    .setContentText("Critical medical information available for first responders")
                    .setStyle(NotificationCompat.BigTextStyle().bigText(medicalText))
                    .setSmallIcon(R.drawable.ic_sos)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .addAction(
                        R.drawable.ic_stop,
                        "Dismiss Info",
                        dismissPendingIntent
                    )
                    .setColor(getColor(R.color.red))
                    .setLights(getColor(R.color.red), 1000, 1000)
                    .setVibrate(longArrayOf(0, 1000, 500, 1000))
                    .build()

                Log.d(TAG, "Starting foreground service with medical info notification")
                startForeground(NOTIFICATION_ID, notification)
                Log.d(TAG, "Medical info notification displayed successfully")

            } catch (e: Exception) {
                Log.e(TAG, "Error showing medical info notification", e)
                showFallbackNotification()
            }
        }
    }

    private suspend fun getUserProfile(userId: String): Map<String, String> {
        return try {
            if (userId == "guest") {
                Log.d(TAG, "Using guest profile")
                mapOf(
                    "name" to "Unknown User",
                    "email" to "Not available",
                    "address" to "Not available",
                    "phone" to "Not available"
                )
            } else {
                Log.d(TAG, "Fetching Firebase profile for user: $userId")
                val document = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .get()
                    .await()

                val profile = mapOf(
                    "name" to (document.getString("name") ?: "Unknown User"),
                    "email" to (document.getString("email") ?: "Not available"),
                    "address" to (document.getString("address") ?: "Not available"),
                    "phone" to (document.getString("phone") ?: "Not available")
                )
                Log.d(TAG, "Firebase profile fetched: $profile")
                profile
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user profile", e)
            mapOf(
                "name" to "Profile Error",
                "email" to "Unable to fetch",
                "address" to "Unable to fetch",
                "phone" to "Unable to fetch"
            )
        }
    }

    private fun buildMedicalInfoText(
        userProfile: Map<String, String>,
        medicalInfo: com.img.nirbhayx.data.MedicalInfo?,
        emergencyContacts: List<com.img.nirbhayx.data.EmergencyContact>
    ): String {
        val sb = StringBuilder()

        sb.append("Address: ${userProfile["address"] ?: "Not available"}\n\n")

        sb.append("Emergency Contacts:\n")
        if (emergencyContacts.isNotEmpty()) {
            emergencyContacts.take(3).forEachIndexed { index, contact ->
                sb.append("${index + 1}. ${contact.name}: ${contact.phoneNumber}\n")
            }
        } else {
            sb.append("No emergency contacts available\n")
        }
        sb.append("\n")

        medicalInfo?.let { info ->
            if (info.bloodType.isNotEmpty()) sb.append("Blood Type: ${info.bloodType}\n")
            if (info.allergies.isNotEmpty()) sb.append("Allergies: ${info.allergies}\n")
            if (info.medications.isNotEmpty()) sb.append("Medications: ${info.medications}\n")
            if (info.medicalConditions.isNotEmpty()) sb.append("Conditions: ${info.medicalConditions}\n")
            if (info.emergencyNotes.isNotEmpty()) sb.append("Notes: ${info.emergencyNotes}\n")
        }

        return sb.toString().trim()
    }


    private fun showFallbackNotification() {
        Log.d(TAG, "Showing fallback notification")

        val fallbackText = """
            🚨 EMERGENCY MEDICAL INFO
            
            ⚠️ Unable to load complete medical information
            📞 Check device for emergency contacts
            🏥 Please seek immediate medical attention
            
            This notification was triggered by emergency power button press (3x)
        """.trimIndent()

        val dismissIntent = Intent(this, EmergencyMedicalDisplayService::class.java).apply {
            action = ACTION_DISMISS
        }
        val dismissPendingIntent = PendingIntent.getService(
            this, 3, dismissIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🚨 EMERGENCY - Medical Info Error")
            .setContentText("Emergency detected but unable to load medical data")
            .setStyle(NotificationCompat.BigTextStyle().bigText(fallbackText))
            .setSmallIcon(R.drawable.ic_sos)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setOngoing(true)
            .addAction(R.drawable.ic_stop, "Dismiss", dismissPendingIntent)
            .setColor(getColor(R.color.red))
            .setVibrate(longArrayOf(0, 1000, 500, 1000))
            .build()

        try {
            startForeground(NOTIFICATION_ID, notification)
            Log.d(TAG, "Fallback notification displayed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show fallback notification", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "EmergencyMedicalDisplayService destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}