package com.img.nirbhayx.ui.screens.features

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.img.nirbhayx.data.CommunityAlert
import com.img.nirbhayx.ui.components.NirbhayXMainScaffold
import com.img.nirbhayx.ui.theme.ActionGreen
import com.img.nirbhayx.ui.theme.BrightOrange
import com.img.nirbhayx.ui.theme.ElectricRed
import com.img.nirbhayx.ui.theme.PowerGradient
import com.img.nirbhayx.ui.theme.PureWhite
import com.img.nirbhayx.utils.CommunityNotificationListener
import com.img.nirbhayx.utils.LocationUtils

@Composable
fun Community(onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val prefs: SharedPreferences =
        context.getSharedPreferences("community_prefs", Context.MODE_PRIVATE)

    var isOptedIn by remember { mutableStateOf(prefs.getBoolean("opted_in", false)) }
    var alerts by remember { mutableStateOf(listOf<CommunityAlert>()) }

    val notificationListener = remember { CommunityNotificationListener(context) }

    LaunchedEffect(isOptedIn) {
        if (isOptedIn) {
            notificationListener.startListening()

            val locationUtils = LocationUtils(context)
            if (locationUtils.hasLocationPermission(context)) {
                locationUtils.requestLocationUpdates { location ->
                    val userId = com.google.firebase.auth.FirebaseAuth.getInstance().uid
                        ?: return@requestLocationUpdates
                    notificationListener.listenForLocationNotifications(
                        location.latitude,
                        location.longitude,
                        userId
                    )
                }
            }

            loadCommunityAlerts { loadedAlerts ->
                alerts = loadedAlerts
            }
        } else {
            notificationListener.stopListening()
            alerts = emptyList()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            notificationListener.stopListening()
        }
    }

    NirbhayXMainScaffold(currentRoute = "community", onNavigate = onNavigate) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(24.dp)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = PowerGradient,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(
                                        PureWhite.copy(alpha = 0.2f),
                                        RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = "Community",
                                    tint = PureWhite,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "🛡️ Join the NirbhayX Safety Network",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PureWhite,
                                    fontSize = 20.sp
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Be part of a community that cares. Get instant alerts when someone nearby needs help and make a difference.",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = PureWhite.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isOptedIn) "🟢 Active Member" else "Join Network",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite
                                    )
                                )

                                Switch(
                                    checked = isOptedIn,
                                    onCheckedChange = {
                                        isOptedIn = it
                                        prefs.edit().putBoolean("opted_in", it).apply()
                                        Toast.makeText(
                                            context,
                                            if (it) "Welcome to the safety network! 🛡️" else "You've left the network.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = ActionGreen,
                                        checkedTrackColor = PureWhite.copy(alpha = 0.3f),
                                        uncheckedThumbColor = PureWhite.copy(alpha = 0.7f),
                                        uncheckedTrackColor = PureWhite.copy(alpha = 0.2f)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            if (isOptedIn) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "🚨 Community Alerts",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        if (alerts.isNotEmpty()) {
                            Surface(
                                color = BrightOrange.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "${alerts.size}",
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BrightOrange
                                    )
                                )
                            }
                        }
                    }
                }

                if (alerts.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Safe",
                                    tint = ActionGreen,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "🌟 All Clear in Your Area!",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "No recent alerts nearby. You'll be notified instantly when someone needs help.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(alerts) { alert ->
                        EnhancedAlertCard(alert = alert)
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "🤝",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Join the Safety Community",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Enable community alerts to view and receive emergency notifications from people around you.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedAlertCard(alert: CommunityAlert) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (alert.isResolved)
                MaterialTheme.colorScheme.surfaceContainer
            else
                ElectricRed.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = if (alert.isResolved) ActionGreen else ElectricRed,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (alert.isResolved) "✅ RESOLVED" else "🚨 ACTIVE",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PureWhite,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = alert.message,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(icon = "📍", text = alert.locationText)
            InfoRow(icon = "👤", text = "${alert.username} | 📞 ${alert.contact}")
            InfoRow(icon = "🕐", text = formatTimestamp(alert.timestamp))
        }
    }
}

@Composable
fun InfoRow(icon: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 14.sp,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

private fun loadCommunityAlerts(onAlertsLoaded: (List<CommunityAlert>) -> Unit) {
    FirebaseFirestore.getInstance()
        .collection("community_alerts")
        .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
        .limit(20)
        .get()
        .addOnSuccessListener { documents ->
            val alertsList = documents.mapNotNull { doc ->
                try {
                    doc.toObject(CommunityAlert::class.java)
                } catch (e: Exception) {
                    Log.e("Community", "Error parsing alert", e)
                    null
                }
            }
            onAlertsLoaded(alertsList)
        }
        .addOnFailureListener { e ->
            Log.e("Community", "Error loading alerts", e)
            onAlertsLoaded(emptyList())
        }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000} minutes ago"
        diff < 86400000 -> "${diff / 3600000} hours ago"
        else -> "${diff / 86400000} days ago"
    }
}