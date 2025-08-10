package com.img.nirbhayx.ui.screens.features

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.img.nirbhayx.Screen
import com.img.nirbhayx.data.EmergencySharingPrefsKeys
import com.img.nirbhayx.ui.components.NirbhayXMainScaffold
import com.img.nirbhayx.viewmodels.EmergencyContactsViewModel
import com.img.nirbhayx.viewmodels.EmergencySharingViewModel

@Composable
fun EmergencySharing(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: EmergencySharingViewModel,
    contactsViewModel: EmergencyContactsViewModel,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val contactList by contactsViewModel.filteredContacts.collectAsState()
    val emergencyContactCount = contactList.size

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result[Manifest.permission.ACCESS_FINE_LOCATION] == true)
            viewModel.updateSetting(EmergencySharingPrefsKeys.LOCATION, true)
        if (result[Manifest.permission.RECORD_AUDIO] == true)
            viewModel.updateSetting(EmergencySharingPrefsKeys.AUDIO, true)
        if (result[Manifest.permission.CAMERA] == true)
            viewModel.updateSetting(EmergencySharingPrefsKeys.VIDEO, true)
        if (result[Manifest.permission.CALL_PHONE] == true)
            viewModel.updateSetting(EmergencySharingPrefsKeys.AUTO_CALL, true)
        if (result[Manifest.permission.SEND_SMS] == true)
            viewModel.updateSetting(EmergencySharingPrefsKeys.SMS, true)
    }

    fun requestPermissionIfNotGranted(permission: String, onGranted: () -> Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            onGranted()
        } else {
            permissionLauncher.launch(arrayOf(permission))
        }
    }

    NirbhayXMainScaffold(currentRoute, onNavigate) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Card(
                    onClick = { onNavigate(Screen.DrawerScreen.EmergencyContacts.dRoute) },
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Emergency Contacts",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "$emergencyContactCount contacts configured",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Go",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Emergency Features",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                ModernToggleCard(
                    title = "Share Live Location",
                    description = "Send your precise GPS location to emergency contacts",
                    emoji = "📍",
                    checked = uiState.isLocationSharingEnabled
                ) {
                    requestPermissionIfNotGranted(Manifest.permission.ACCESS_FINE_LOCATION) {
                        viewModel.updateSetting(
                            EmergencySharingPrefsKeys.LOCATION,
                            !uiState.isLocationSharingEnabled
                        )
                    }
                }
            }

            item {
                ModernToggleCard(
                    title = "Auto Call Emergency Contact",
                    description = "Automatically call your primary emergency contact",
                    emoji = "📞",
                    checked = uiState.isAutoCallEnabled
                ) {
                    requestPermissionIfNotGranted(Manifest.permission.CALL_PHONE) {
                        viewModel.updateSetting(
                            EmergencySharingPrefsKeys.AUTO_CALL,
                            !uiState.isAutoCallEnabled
                        )
                    }
                }
            }

            item {
                ModernToggleCard(
                    title = "Send Emergency SMS",
                    description = "Send detailed emergency alerts via text message",
                    emoji = "📩",
                    checked = uiState.isSmsEnabled
                ) {
                    requestPermissionIfNotGranted(Manifest.permission.SEND_SMS) {
                        viewModel.updateSetting(
                            EmergencySharingPrefsKeys.SMS,
                            !uiState.isSmsEnabled
                        )
                    }
                }
            }

            item {
                ModernToggleCard(
                    title = "Background Audio Recording",
                    description = "Record audio evidence during emergency situations",
                    emoji = "🎙️",
                    checked = uiState.isAudioRecordingEnabled
                ) {
                    requestPermissionIfNotGranted(Manifest.permission.RECORD_AUDIO) {
                        viewModel.updateSetting(
                            EmergencySharingPrefsKeys.AUDIO,
                            !uiState.isAudioRecordingEnabled
                        )
                    }
                }
            }

            item {
                ModernToggleCard(
                    title = "Background Video Recording",
                    description = "Record video evidence for enhanced security",
                    emoji = "🎥",
                    checked = uiState.isVideoRecordingEnabled
                ) {
                    requestPermissionIfNotGranted(Manifest.permission.CAMERA) {
                        viewModel.updateSetting(
                            EmergencySharingPrefsKeys.VIDEO,
                            !uiState.isVideoRecordingEnabled
                        )
                    }
                }
            }

            item {
                Text(
                    text = "System Permissions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            item {
                PermissionCard(
                    title = "Notification Access",
                    description = "Allow NirbhayX to send emergency notifications",
                    icon = Icons.Default.NotificationsActive
                ) {
                    context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    })
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                item {
                    PermissionCard(
                        title = "Overlay Permission",
                        description = "Required for emergency alerts to show over other apps",
                        icon = Icons.Default.Security,
                        isRequired = true
                    ) {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${context.packageName}")
                        )
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}

@Composable
fun ModernToggleCard(
    title: String,
    description: String,
    emoji: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (checked) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@Composable
fun PermissionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isRequired: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            if (isRequired) MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isRequired) MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isRequired) MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onSurface
                    )
                    if (isRequired) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "REQUIRED",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isRequired) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Open Settings",
                tint = if (isRequired) MaterialTheme.colorScheme.onErrorContainer
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}