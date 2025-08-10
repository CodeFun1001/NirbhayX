package com.img.nirbhayx.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.img.nirbhayx.R
import com.img.nirbhayx.data.ActivityLog
import com.img.nirbhayx.data.EmergencySharingDataStore
import com.img.nirbhayx.data.EmergencySharingSettings
import com.img.nirbhayx.data.Graph
import com.img.nirbhayx.data.LocationData
import com.img.nirbhayx.utils.AudioRecorder
import com.img.nirbhayx.utils.CommunityAlertSender
import com.img.nirbhayx.utils.LocationUtils
import com.img.nirbhayx.utils.SmsUtils
import com.img.nirbhayx.utils.VideoRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SosService() : Service() {

    private lateinit var locationUtils: LocationUtils
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var audioRecorder: AudioRecorder? = null
    private var videoRecorder: VideoRecorder? = null
    private var sosJob: Job? = null
    private var recordingJob: Job? = null
    private var currentSettings: EmergencySharingSettings? = null

    override fun onCreate() {
        super.onCreate()

        locationUtils = LocationUtils(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.action == "STOP_SOS") {
            Log.d("SOS", "Stop Emergency triggered from notification")
            stopAllOperations()
            stopSelf()
            return START_NOT_STICKY
        }

        startForegroundWithNotification()
        insertToRoom("🚨 SOS triggered - Emergency Sharing started")

        val dataStore = EmergencySharingDataStore(this)

        sosJob?.cancel()

        sosJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                dataStore.preferencesFlow.collect { settings ->
                    currentSettings = settings
                    handleEmergencySettings(settings)
                    CommunityAlertSender.sendCommunityAlert(context = this@SosService)
                }
            } catch (e: Exception) {
                Log.e("SOS", "Error in SOS service", e)
                insertToRoom("❌ SOS service error: ${e.localizedMessage}")
            }
        }

        return START_STICKY
    }

    private fun handleEmergencySettings(settings: EmergencySharingSettings) {
        try {
            if (settings.isLocationSharingEnabled) {
                if (locationUtils.hasLocationPermission(this@SosService)) {
                    getCurrentLocation()
                } else {
                    insertToRoom("⚠️ Location permission not granted")
                }
            }

            if (settings.isSmsEnabled) {
                startContinuousLocationUpdates()
            }

            if (settings.isAutoCallEnabled) {
                insertToRoom("📞 Auto call feature triggered")
                startFakeCall()
            }

            handleRecordingSettings(settings)

        } catch (e: Exception) {
            Log.e("SOS", "Error handling emergency settings", e)
            insertToRoom("❌ Error in emergency settings: ${e.localizedMessage}")
        }
    }

    private fun startFakeCall() {
        try {
            val intent = Intent(this, FakeCallActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            insertToRoom("📞 Fake call started")
        } catch (e: Exception) {
            Log.e("SOS", "Error starting fake call", e)
            insertToRoom("❌ Fake call error: ${e.message}")
        }
    }

    private fun handleRecordingSettings(settings: EmergencySharingSettings) {
        stopAllRecordings()

        recordingJob?.cancel()

        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                when {
                    settings.isVideoRecordingEnabled -> {
                        startContinuousVideoRecording()
                    }

                    settings.isAudioRecordingEnabled -> {
                        startContinuousAudioRecording()
                    }

                    else -> {
                        insertToRoom("📹 No recording option enabled")
                    }
                }
            } catch (e: Exception) {
                Log.e("SOS", "Recording error", e)
                insertToRoom("❌ Recording error: ${e.message}")
            }
        }
    }


    private suspend fun startContinuousVideoRecording() {
        while (recordingJob?.isActive == true) {
            try {
                insertToRoom("📹 Starting video recording session...")

                videoRecorder = VideoRecorder(this@SosService)
                val videoPath = videoRecorder?.startRecording()
                delay(1000)

                if (videoPath != null) {
                    insertToRoom("📹 Video recording started")

                    delay(300000)

                    videoRecorder?.stopRecording()
                    insertToRoom("📹 Video recording session completed")

                    delay(10000)
                } else {
                    insertToRoom("❌ Video recording failed, switching to audio")
                    startContinuousAudioRecording()
                    break
                }

            } catch (e: Exception) {
                Log.e("SOS", "Video recording error", e)
                insertToRoom("❌ Video error: ${e.message}, switching to audio")
                startContinuousAudioRecording()
                break
            }
        }
    }

    private suspend fun startContinuousAudioRecording() {
        while (recordingJob?.isActive == true) {
            try {
                insertToRoom("🎙️ Starting audio recording session...")

                audioRecorder = AudioRecorder(this@SosService)
                val audioPath = audioRecorder?.startRecording()

                if (audioPath != null) {
                    insertToRoom("🎙️ Audio recording started")

                    delay(300000)

                    audioRecorder?.stopRecording()
                    insertToRoom("🎙️ Audio recording session completed")

                    delay(10000)
                } else {
                    insertToRoom("❌ Audio recording failed")
                    delay(30000)
                }

            } catch (e: Exception) {
                Log.e("SOS", "Audio recording error", e)
                insertToRoom("❌ Audio error: ${e.message}")
                delay(30000)
            }
        }
    }

    private fun stopAllRecordings() {
        try {
            recordingJob?.cancel()

            videoRecorder?.let {
                if (it.isCurrentlyRecording()) {
                    it.stopRecording()
                    insertToRoom("📹 Video recording stopped")
                }
            }
            videoRecorder = null

            audioRecorder?.let {
                if (it.isCurrentlyRecording()) {
                    it.stopRecording()
                    insertToRoom("🎙️ Audio recording stopped")
                }
            }
            audioRecorder = null

        } catch (e: Exception) {
            Log.e("SOS", "Error stopping recordings", e)
        }
    }

    private fun sendEmergencySms(locationData: LocationData, address: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this@SosService,
                    android.Manifest.permission.SEND_SMS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                insertToRoom("⚠️ SMS permission NOT granted")
                return@launch
            }

            val contactsFlow = Graph.emergencyContactRepository.getAllContacts()

            contactsFlow.collect { contactList ->
                if (contactList.isNotEmpty()) {
                    val message = """
                🚨 I’m in danger! Please help!
                📍 Location: https://maps.google.com/?q=${locationData.latitude},${locationData.longitude}
                📌 Address: $address
                """.trimIndent()

                    SmsUtils.sendSmsToContacts(this@SosService, message, contactList)
                    insertToRoom("📤 SMS sent to ${contactList.size} contacts")
                } else {
                    insertToRoom("⚠️ No emergency contacts found")
                }
            }
        }
    }


    private fun getCurrentLocation() {
        try {
            val request = CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMaxUpdateAgeMillis(0)
                .build()

            fusedLocationClient.getCurrentLocation(request, null)
                .addOnSuccessListener { location: Location? ->
                    Log.d("SOS", "Location tracked successfully: $location")
                    if (location != null) {
                        val loc = LocationData(location.latitude, location.longitude)
                        val address = locationUtils.reverseGeocodeLocation(loc)

                        insertToRoom("📍 Location: ${loc.latitude}, ${loc.longitude} \n 📌 Address: $address")

                    } else {
                        insertToRoom("⚠️ Location not available")
                    }
                }
                .addOnFailureListener {
                    insertToRoom("⚠️ Location fetch failed: ${it.localizedMessage}")
                }

        } catch (e: SecurityException) {
            insertToRoom("⚠️ SecurityException in location: ${e.message}")
        }
    }

    private fun stopAllOperations() {
        try {
            sosJob?.cancel()
            recordingJob?.cancel()
            stopAllRecordings()
            locationUtils.stopLocationUpdates()
            insertToRoom("🛑 All emergency operations stopped")
        } catch (e: Exception) {
            Log.e("SOS", "Error stopping operations", e)
        }
    }

    private fun startContinuousLocationUpdates() {
        locationUtils.requestLocationUpdates { locationData ->
            val address = locationUtils.reverseGeocodeLocation(locationData)
            sendEmergencySms(locationData, address)
        }
    }

    private fun insertToRoom(message: String) {
        val log = ActivityLog(
            timestamp = System.currentTimeMillis(),
            description = message
        )
        CoroutineScope(Dispatchers.IO).launch {
            Graph.activityRepository.insert(log)
        }
    }

    private fun startForegroundWithNotification() {
        val stopIntent = Intent(this, StopSosReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, "SOS_CHANNEL")
            .setContentTitle("🚨 Emergency Active")
            .setContentText("Emergency mode is running. Tap to stop.")
            .setSmallIcon(R.drawable.ic_sos)
            .addAction(R.drawable.ic_stop, "Stop Emergency", stopPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAllOperations()
        insertToRoom("🛑 Emergency service destroyed")
        Log.d("SOS", "SOS Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
