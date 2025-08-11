package com.img.nirbhayx

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.img.nirbhayx.data.PreferencesManager
import com.img.nirbhayx.services.EmergencyBackgroundService
import com.img.nirbhayx.services.PowerButtonReceiver
import com.img.nirbhayx.ui.theme.NirbhayXTheme
import com.img.nirbhayx.utils.LocationUtils
import com.img.nirbhayx.viewmodels.ActivityViewModel
import com.img.nirbhayx.viewmodels.AuthViewModel
import com.img.nirbhayx.viewmodels.EmergencyContactsViewModel
import com.img.nirbhayx.viewmodels.EmergencySharingViewModel
import com.img.nirbhayx.viewmodels.MedicalInfoViewModel
import com.img.nirbhayx.viewmodels.SafetyTipsViewModel
import com.img.nirbhayx.viewmodels.SafetyTipsViewModelFactory
import com.img.nirbhayx.viewmodels.ThemeViewModel

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private var powerButtonReceiver: PowerButtonReceiver? = null
    private var isReceiverRegistered = false
    private lateinit var locationUtils: LocationUtils
    private lateinit var preferencesManager: PreferencesManager

    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferencesManager = PreferencesManager(this)
        locationUtils = LocationUtils(this)

        // Initialize database first
        com.img.nirbhayx.data.Graph.initDatabase(application)

        // Initialize repositories with current user or guest
        val initialUid = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
        com.img.nirbhayx.data.Graph.createRepositories(initialUid)

        setContent {
            val authViewModel: AuthViewModel by viewModels()
            val activityViewModel: ActivityViewModel by viewModels()
            val emergencyContactsViewModel: EmergencyContactsViewModel by viewModels()
            val emergencySharingViewModel: EmergencySharingViewModel by viewModels()
            val medicalInfoViewModel: MedicalInfoViewModel by viewModels()

            // Create SafetyTipsViewModel AFTER repositories are initialized
            val safetyTipsViewModel: SafetyTipsViewModel = remember {
                ViewModelProvider(
                    this@MainActivity,
                    SafetyTipsViewModelFactory(com.img.nirbhayx.data.Graph.safetyTipRepository)
                ).get(SafetyTipsViewModel::class.java)
            }

            val themeViewModel = ThemeViewModel(preferencesManager)
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            NirbhayXTheme(darkTheme = isDarkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerpadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerpadding),
                        authViewModel = authViewModel,
                        activityViewModel = activityViewModel,
                        emergencyContactsViewModel = emergencyContactsViewModel,
                        emergencySharingViewModel = emergencySharingViewModel,
                        safetyTipsViewModel = safetyTipsViewModel,
                        medicalInfoViewModel = medicalInfoViewModel,
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { newTheme ->
                            themeViewModel.setTheme(newTheme)
                        }
                    )
                }
            }
        }

        // Let AuthViewModel handle auth state changes
        // Remove the auth state listener to avoid conflicts

        setupEmergencySystem()
        requestRequiredPermissions()
    }

    // Rest of your MainActivity code remains the same...
    private fun setupLocationUpdates(uid: String) {
        if (locationUtils.hasLocationPermission(this)) {
            locationUtils.requestLocationUpdates { location ->
                Log.d(TAG, "User location updated for community features")
            }
        } else {
            Log.d(TAG, "Location permission not granted, requesting...")
            requestLocationPermission()
        }
    }

    private fun requestRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isNotificationPermissionGranted()) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                requestLocationPermission()
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (!locationUtils.hasLocationPermission(this)) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun setupEmergencySystem() {
        createNotificationChannels()
        checkSystemAlertPermission()
        startBackgroundService()
    }

    private fun startBackgroundService() {
        try {
            val serviceIntent = Intent(this, EmergencyBackgroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
            Log.d(TAG, "Background service started")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start background service", e)
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // SOS Confirmation Channel (your existing working channel)
            val sosChannel = NotificationChannel(
                "SOS_CHANNEL",
                "Emergency SOS Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical emergency alerts that can show over lock screen"
                enableVibration(true)
                enableLights(true)
                setBypassDnd(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            // Medical Info Channel (NEW - for displaying medical information)
            val medicalInfoChannel = NotificationChannel(
                "EMERGENCY_MEDICAL_INFO",
                "Emergency Medical Information",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Emergency medical information for first responders and bystanders"
                enableVibration(false) // No vibration to avoid interfering with SOS channel
                enableLights(true)
                setBypassDnd(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
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

            // Community Emergency Alerts Channel
            val emergencyAlertsChannel = NotificationChannel(
                "EMERGENCY_ALERTS_CHANNEL",
                "Emergency Community Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High-priority emergency notifications from nearby users"
                enableVibration(true)
                enableLights(true)
                setBypassDnd(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                setSound(null, null)
                setShowBadge(true)
            }

            // Community Safety Alerts Channel
            val communityChannel = NotificationChannel(
                "community_alerts",
                "Community Safety Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Community safety alerts from nearby users"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }

            // Create all channels
            notificationManager?.createNotificationChannel(sosChannel)
            notificationManager?.createNotificationChannel(medicalInfoChannel)
            notificationManager?.createNotificationChannel(serviceChannel)
            notificationManager?.createNotificationChannel(emergencyAlertsChannel)
            notificationManager?.createNotificationChannel(communityChannel)

            Log.d(TAG, "All notification channels created successfully")
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Notification permission granted")
            requestLocationPermission()
        } else {
            Log.w(TAG, "Notification permission denied")
            showPermissionDialog(
                "Notification Permission Required",
                "This app needs notification permission to show emergency alerts from nearby users."
            )
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            Log.d(TAG, "Location permission granted")
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                setupLocationUpdates(currentUser.uid)
            }
        } else {
            Log.w(TAG, "Location permission denied")
            showPermissionDialog(
                "Location Permission Required",
                "This app needs location permission to send and receive community safety alerts."
            )
        }
    }

    private val systemAlertPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                Log.d(TAG, "System alert window permission granted")
                registerPowerButtonReceiver()
            } else {
                Log.w(TAG, "System alert window permission denied")
                showPermissionDialog(
                    "System Alert Permission Required",
                    "This app needs permission to display over other apps for emergency situations."
                )
            }
        }
    }

    private fun showPermissionDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun checkSystemAlertPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                systemAlertPermissionLauncher.launch(intent)
            } else {
                Log.d(TAG, "System alert permission already granted")
                registerPowerButtonReceiver()
            }
        } else {
            registerPowerButtonReceiver()
        }
    }

    private fun registerPowerButtonReceiver() {
        if (!isReceiverRegistered) {
            try {
                powerButtonReceiver = PowerButtonReceiver()
                val filter = IntentFilter().apply {
                    addAction(Intent.ACTION_SCREEN_ON)
                    addAction(Intent.ACTION_SCREEN_OFF)
                    addAction(Intent.ACTION_USER_PRESENT)
                    priority = IntentFilter.SYSTEM_HIGH_PRIORITY
                }

                registerReceiver(powerButtonReceiver, filter)
                isReceiverRegistered = true
                Log.d(TAG, "✅ PowerButtonReceiver registered successfully")

                testEmergencySystem()
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to register PowerButtonReceiver", e)
            }
        }
    }

    private fun testEmergencySystem() {
        Log.d(TAG, "🧪 Testing emergency system...")
        Log.d(TAG, "- Receiver registered: $isReceiverRegistered")
        Log.d(TAG, "- Notification permission: ${isNotificationPermissionGranted()}")
        Log.d(TAG, "- Location permission: ${locationUtils.hasLocationPermission(this)}")
        Log.d(
            TAG,
            "- Overlay permission: ${
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.canDrawOverlays(this) else true
            }"
        )
    }

    override fun onResume() {
        super.onResume()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (!isReceiverRegistered) {
            registerPowerButtonReceiver()
        }
    }

    override fun onPause() {
        super.onPause()
        val currentUser = FirebaseAuth.getInstance().currentUser
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterPowerButtonReceiver()
        locationUtils.stopLocationUpdates()
    }

    private fun unregisterPowerButtonReceiver() {
        if (isReceiverRegistered && powerButtonReceiver != null) {
            try {
                unregisterReceiver(powerButtonReceiver)
                isReceiverRegistered = false
                Log.d(TAG, "PowerButtonReceiver unregistered successfully")
            } catch (e: IllegalArgumentException) {
                Log.w(TAG, "Receiver was not registered", e)
            }
        }
    }
}