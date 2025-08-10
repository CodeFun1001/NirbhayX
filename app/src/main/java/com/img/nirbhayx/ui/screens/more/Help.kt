package com.img.nirbhayx.ui.screens.more

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Help(navController: NavHostController, currentRoute: String, onNavigate: (String) -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Quick Start", "FAQ", "Features Guide")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Help & Support",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                elevation = 8.dp,
                modifier = Modifier.height(90.dp)
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> QuickStartGuide()
                1 -> FAQSection()
                2 -> FeaturesGuide()
            }
        }
    }
}

@Composable
fun QuickStartGuide() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Quick Start Guide",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Text(
                "Follow these simple steps to set up NirbhayX and be prepared for emergencies:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(quickStartSteps) { step ->
            QuickStartStep(step = step)
        }

        item {
            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            "Important",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Text(
                        "Test your SOS feature regularly to ensure it works when you need it most. Make sure your emergency contacts are aware of the app and know what to do when they receive your alert.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FAQSection() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "Frequently Asked Questions",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(faqItems) { faq ->
            FAQItem(faq = faq)
        }
    }
}

@Composable
fun FeaturesGuide() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Features Guide",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(featureGuides) { feature ->
            FeatureGuideCard(feature = feature)
        }
    }
}

@Composable
fun QuickStartStep(step: QuickStartStepData) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(2.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    step.stepNumber.toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    step.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    step.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (step.tip.isNotEmpty()) {
                    Text(
                        "💡 Tip: ${step.tip}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FAQItem(faq: FAQData) {
    var expanded by remember { mutableStateOf(false) }

    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(2.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    faq.question,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    faq.answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

@Composable
fun FeatureGuideCard(feature: FeatureGuideData) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(2.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    feature.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    feature.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Text(
                feature.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (feature.steps.isNotEmpty()) {
                Text(
                    "How to use:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 12.dp)
                )

                feature.steps.forEachIndexed { index, step ->
                    Text(
                        "${index + 1}. $step",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}

data class QuickStartStepData(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val tip: String = ""
)

data class FAQData(
    val question: String,
    val answer: String
)

data class FeatureGuideData(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val steps: List<String> = emptyList()
)

val quickStartSteps = listOf(
    QuickStartStepData(
        1,
        "Sign Up & Profile Setup",
        "Create your account using email or phone number. Complete your profile with personal details for identification.",
        "Use a strong password and verify your email/phone for account security."
    ),
    QuickStartStepData(
        2,
        "Add Emergency Contacts",
        "Add at least 3 trusted contacts who will receive your SOS alerts. Include family members, friends, and local authorities.",
        "Make sure your contacts know about this app and what to do when they receive an alert."
    ),
    QuickStartStepData(
        3,
        "Grant Permissions",
        "Allow location, SMS, phone, camera, and microphone access for all features to work properly.",
        "These permissions are essential for emergency functionality."
    ),
    QuickStartStepData(
        4,
        "Configure SOS Settings",
        "Set up your SOS preferences including countdown time, auto-recording, and alert methods.",
        "Test these settings to ensure they work as expected."
    ),
    QuickStartStepData(
        5,
        "Practice SOS Trigger",
        "Learn the triple power button press. Practice in a safe environment to get familiar with the process.",
        "The SOS trigger works even when the screen is locked."
    ),
    QuickStartStepData(
        6,
        "Test Fake Call Feature",
        "Set up and test the fake call feature for situations where you need to escape discreetly.",
        "Customize caller name and delay time for realistic simulation."
    )
)

val faqItems = listOf(
    FAQData(
        "How does the SOS feature work?",
        "Press the power button 3 times quickly to trigger SOS. You'll see a confirmation screen with a countdown. Swipe to confirm or wait for auto-activation. Your location and emergency message will be sent to all your emergency contacts via SMS."
    ),
    FAQData(
        "What happens after I trigger SOS?",
        "Multiple actions happen simultaneously: 1) SMS with your location is sent to emergency contacts, 2) Audio/video recording starts (if enabled), 3) Your main contact receives a call (if enabled), 4) Community alert is sent to nearby NirbhayX users, 5) The incident is logged in your activity history."
    ),
    FAQData(
        "Will SOS work without internet?",
        "Yes! SOS works with just GPS and cellular network. Location sharing and SMS don't require internet. However, community alerts need internet connection."
    ),
    FAQData(
        "How accurate is the location sharing?",
        "We use GPS for highest accuracy (usually within 3-5 meters). If GPS is unavailable, we fall back to network location which may be less accurate but still provides your general area."
    ),
    FAQData(
        "What is the Fake Call feature?",
        "Fake Call simulates an incoming call to help you escape uncomfortable situations. You can customize the caller name and set a delay. The call looks and sounds completely real to anyone nearby."
    ),
    FAQData(
        "How does Community Alert work?",
        "When you trigger SOS, nearby NirbhayX users (within 2km) receive a notification about your emergency. They can see your approximate location and offer help before official authorities arrive."
    ),
    FAQData(
        "Are my recordings saved securely?",
        "Yes, all recordings are encrypted and stored locally on your device in a secure folder. You control when to share or delete them. We recommend backing up important recordings."
    ),
    FAQData(
        "Where are all the recordings stored?",
        "Path : Internal Storage < Android < data < com.img.nirbhayx < files  < Movies : Video Recordings & Music : AudioRecordings"
    ),
    FAQData(
        "Can I cancel an SOS after triggering?",
        "Yes, you have a countdown period (5-10 seconds) to cancel. After that, cancellation requires additional steps to prevent accidental cancellation during real emergencies."
    ),
    FAQData(
        "What if my phone is stolen or lost?",
        "Log into your account from another device to remotely disable SOS features and protect your privacy. Your emergency contacts will be notified of the device change."
    ),
    FAQData(
        "How do I add someone from my contacts?",
        "Go to Emergency Contacts section, tap 'Add Contact', then 'Select from Phone Book'. Choose the contact and confirm. Make sure the phone number is correct and active."
    ),
    FAQData(
        "Why do I need so many permissions?",
        "Each permission serves a critical safety function: Location (for sharing your position), SMS (for emergency messages), Phone (for emergency calls), Camera/Microphone (for evidence recording), Contacts (for easy emergency contact selection)."
    )
)

val featureGuides = listOf(
    FeatureGuideData(
        Icons.Default.Emergency,
        "SOS Emergency Trigger",
        "Your lifeline in critical situations. Triple-press power button to instantly alert your emergency contacts with your exact location.",
        listOf(
            "Press power button 3 times quickly",
            "Swipe to confirm on the countdown screen",
            "Your contacts receive SMS with live location",
            "Audio/video recording starts automatically",
            "Community alert sent to nearby users"
        )
    ),
    FeatureGuideData(
        Icons.Default.Phone,
        "Fake Call Feature",
        "Escape uncomfortable situations discretely by simulating a realistic incoming call.",
        listOf(
            "Go to Fake Call section in the app",
            "Set caller name and photo",
            "Choose delay time (immediate or timed)",
            "Trigger when needed from quick actions",
            "Answer the 'call' and create your excuse to leave"
        )
    ),
    FeatureGuideData(
        Icons.Default.Group,
        "Community Alert System",
        "Connect with nearby NirbhayX users who can provide immediate help before authorities arrive.",
        listOf(
            "Enable community alerts in settings",
            "When you trigger SOS, nearby users get notified",
            "They can see your approximate location",
            "Community members can offer assistance",
            "Creates a local safety network"
        )
    ),
    FeatureGuideData(
        Icons.Default.VideoCall,
        "Background Recording",
        "Automatically record audio/video evidence during emergencies for your safety and legal protection.",
        listOf(
            "Enable auto-recording in emergency settings",
            "Recording starts when SOS is confirmed",
            "Saves to secure, encrypted folder",
            "Access recordings from Activity Log",
            "Share with authorities when needed"
        )
    ),
    FeatureGuideData(
        Icons.Default.LocationOn,
        "Real-time Location Sharing",
        "Share your exact location with emergency contacts using GPS and network data.",
        listOf(
            "Location shared automatically with SOS",
            "Uses GPS for highest accuracy",
            "Works even without internet connection",
            "Updates sent every few minutes during emergency",
            "Stops when you mark yourself as safe"
        )
    ),
    FeatureGuideData(
        Icons.Default.Contacts,
        "Emergency Contacts Management",
        "Maintain a list of trusted people who will be notified during emergencies.",
        listOf(
            "Add contacts from phone book or manually",
            "Set primary contact for direct calls",
            "Verify contact numbers regularly",
            "Inform contacts about the app",
            "Keep at least 3-5 emergency contacts"
        )
    )
)