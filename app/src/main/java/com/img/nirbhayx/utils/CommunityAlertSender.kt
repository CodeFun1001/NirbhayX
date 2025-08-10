package com.img.nirbhayx.utils

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.img.nirbhayx.data.CommunityAlert
import com.img.nirbhayx.data.LocationData
import com.img.nirbhayx.data.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object CommunityAlertSender {

    private const val TAG = "CommunityAlertSender"
    private lateinit var locationUtils: LocationUtils

    fun sendAlert(
        context: Context,
        location: LocationData,
        address: String,
        username: String,
        contact: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val userId = FirebaseAuth.getInstance().uid ?: "unknown"

        val alert = CommunityAlert(
            userId = userId,
            username = username,
            contact = contact,
            latitude = location.latitude,
            longitude = location.longitude,
            locationText = address,
            message = "🚨 Someone nearby needs help!",
            timestamp = System.currentTimeMillis(),
            isResolved = false
        )

        FirebaseFirestore.getInstance()
            .collection("community_alerts")
            .add(alert)
            .addOnSuccessListener { docRef ->
                Log.d(TAG, "Alert saved to Firestore with ID: ${docRef.id}")

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save alert to Firestore", e)
                onFailure(e)
            }
    }

    fun sendCommunityAlert(context: Context) {
        try {
            if (!locationUtils.hasLocationPermission(context)) {
                Log.w(TAG, "Location permission not granted")
                return
            }

            locationUtils.requestLocationUpdates { locationData ->
                val address = locationUtils.reverseGeocodeLocation(locationData)

                val userId =
                    FirebaseAuth.getInstance().currentUser?.uid ?: return@requestLocationUpdates

                FirebaseFirestore.getInstance().collection("users").document(userId).get()
                    .addOnSuccessListener { userDoc ->
                        val username = userDoc.getString("name") ?: "Anonymous"
                        val contact = userDoc.getString("phone") ?: "N/A"


                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Failed to get user data", exception)

                    }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error in sendCommunityAlert", e)
        }
    }


    private fun sendToLocationTopic(
        alert: CommunityAlert,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val locationTopic = getLocationTopic(alert.latitude, alert.longitude)

        val data = mapOf(
            "type" to "community_alert",
            "username" to alert.username,
            "contact" to alert.contact,
            "message" to alert.message,
            "location" to alert.locationText,
            "latitude" to alert.latitude.toString(),
            "longitude" to alert.longitude.toString(),
            "timestamp" to alert.timestamp.toString(),
            "userId" to alert.userId
        )

        val notificationDoc = mapOf(
            "topic" to locationTopic,
            "title" to "🚨 Emergency Alert",
            "body" to "${alert.message}\n📍 ${alert.locationText}\n👤 ${alert.username}",
            "data" to data,
            "timestamp" to System.currentTimeMillis(),
            "processed" to false
        )

        FirebaseFirestore.getInstance()
            .collection("notification_triggers")
            .add(notificationDoc)
            .addOnSuccessListener {
                Log.d(TAG, "Notification trigger created for topic: $locationTopic")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to create notification trigger", e)
                onFailure(e)
            }
    }

    private fun getLocationTopic(latitude: Double, longitude: Double): String {
        val latRounded = (latitude * 100).toInt()
        val lonRounded = (longitude * 100).toInt()
        return "alerts_${latRounded}_${lonRounded}"
    }

    fun sendGlobalAlert(
        context: Context,
        location: LocationData,
        address: String,
        username: String,
        contact: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val userId = FirebaseAuth.getInstance().uid ?: "unknown"

        val alert = CommunityAlert(
            userId = userId,
            username = username,
            contact = contact,
            latitude = location.latitude,
            longitude = location.longitude,
            locationText = address,
            message = "🚨 Someone nearby needs help!",
            timestamp = System.currentTimeMillis(),
            isResolved = false
        )

        val alertWithGlobal = alert.copy()

        FirebaseFirestore.getInstance()
            .collection("community_alerts")
            .add(alertWithGlobal)
            .addOnSuccessListener { documentRef ->
                Log.d(TAG, "Global alert saved to Firestore with ID: ${documentRef.id}")

                val globalNotification = mapOf(
                    "alertId" to documentRef.id,
                    "title" to "🚨 Emergency Alert",
                    "body" to "${alert.message}\n📍 ${alert.locationText}\n👤 ${alert.username}",
                    "data" to mapOf(
                        "type" to "community_alert",
                        "username" to alert.username,
                        "contact" to alert.contact,
                        "message" to alert.message,
                        "location" to alert.locationText,
                        "latitude" to alert.latitude.toString(),
                        "longitude" to alert.longitude.toString(),
                        "timestamp" to alert.timestamp.toString()
                    ),
                    "timestamp" to System.currentTimeMillis(),
                    "processed" to false,
                    "isGlobal" to true
                )

                FirebaseFirestore.getInstance()
                    .collection("global_notifications")
                    .add(globalNotification)
                    .addOnSuccessListener {
                        Log.d(TAG, "Global notification trigger created")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to create global notification", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save global alert", e)
                onFailure(e)
            }
    }

    fun sendSosCommunityAlert(
        context: Context,
        location: LocationData,
        onSuccess: (Int) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser == null) {
                    Log.w(TAG, "User not authenticated, cannot send community alert")
                    onFailure(Exception("User not authenticated"))
                    return@launch
                }

                val userProfile = getUserProfile(currentUser.uid)
                if (userProfile == null) {
                    Log.w(TAG, "User profile not found, cannot send community alert")
                    onFailure(Exception("User profile not found"))
                    return@launch
                }

                val preferencesManager = PreferencesManager(context)
                val isOptedIn = preferencesManager.getCommunityAlertsEnabled()

                if (!isOptedIn) {
                    Log.d(TAG, "User has not opted into community alerts")
                    onSuccess(0)
                    return@launch
                }

                val emergencyMessage =
                    "🚨 SOS Alert: ${userProfile["name"] ?: "Someone"} needs immediate help!"

            } catch (e: Exception) {
                Log.e(TAG, "Error sending community alert", e)
                onFailure(e)
            }
        }
    }

    private suspend fun getUserProfile(uid: String): Map<String, Any>? {
        return try {
            val document = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .await()

            if (document.exists()) {
                document.data
            } else {
                val basicProfile = mapOf(
                    "name" to "User",
                    "phone" to "N/A",
                    "uid" to uid
                )

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .set(basicProfile)
                    .await()

                basicProfile
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user profile", e)
            null
        }
    }

    fun isEligibleForCommunityAlerts(context: Context): Boolean {
        return try {
            val preferencesManager = PreferencesManager(context)
            val isOptedIn = preferencesManager.getCommunityAlertsEnabled()
            val hasLocationPermission = LocationUtils(context).hasLocationPermission(context)

            isOptedIn && hasLocationPermission
        } catch (e: Exception) {
            Log.e(TAG, "Error checking eligibility", e)
            false
        }
    }

    fun getCommunityAlertStats(
        onSuccess: (Map<String, Any>) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val stats = mutableMapOf<String, Any>()

                val totalAlerts = FirebaseFirestore.getInstance()
                    .collection("community_alerts")
                    .count()
                    .get(com.google.firebase.firestore.AggregateSource.SERVER)
                    .await()
                stats["totalAlerts"] = totalAlerts.count

                val activeAlerts = FirebaseFirestore.getInstance()
                    .collection("community_alerts")
                    .whereEqualTo("isResolved", false)
                    .count()
                    .get(com.google.firebase.firestore.AggregateSource.SERVER)
                    .await()
                stats["activeAlerts"] = activeAlerts.count

                val today = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
                val todayAlerts = FirebaseFirestore.getInstance()
                    .collection("community_alerts")
                    .whereGreaterThan("timestamp", today)
                    .count()
                    .get(com.google.firebase.firestore.AggregateSource.SERVER)
                    .await()
                stats["todayAlerts"] = todayAlerts.count

                onSuccess(stats)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get community alert stats", e)
                onFailure(e)
            }
        }
    }
}