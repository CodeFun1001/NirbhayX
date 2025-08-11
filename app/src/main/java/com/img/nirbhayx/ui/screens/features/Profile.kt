// Updated Profile.kt with Medical Information Section - FIXED
package com.img.nirbhayx.ui.screens.features

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.img.nirbhayx.data.MedicalInfo
import com.img.nirbhayx.ui.components.NirbhayXMainScaffold
import com.img.nirbhayx.viewmodels.AuthViewModel
import com.img.nirbhayx.viewmodels.MedicalInfoViewModel

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    medicalInfoViewModel: MedicalInfoViewModel, // Add this
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var isEditing by remember { mutableStateOf(false) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    val profileState = remember {
        mutableStateMapOf(
            "name" to "",
            "email" to "",
            "address" to "",
            "phone" to ""
        )
    }

    val medicalInfo by medicalInfoViewModel.medicalInfo.collectAsState()

    // FIXED: Explicitly declare the type as MutableMap<String, Any>
    val medicalState: MutableMap<String, Any> = remember {
        mutableStateMapOf<String, Any>(
            "bloodType" to "",
            "height" to "",
            "weight" to "",
            "allergies" to "",
            "medications" to "",
            "medicalConditions" to "",
            "pregnancyStatus" to "",
            "emergencyNotes" to "",
            "organDonor" to false,
            "doctorName" to "",
            "doctorPhone" to "",
            "insurance" to ""
        )
    }

    // Load medical info when screen opens
    LaunchedEffect(userId) {
        userId?.let {
            medicalInfoViewModel.loadMedicalInfo(it)
        }
    }

    // Update medical state when medical info changes
    LaunchedEffect(medicalInfo) {
        medicalInfo?.let { info ->
            medicalState["bloodType"] = info.bloodType
            medicalState["height"] = info.height
            medicalState["weight"] = info.weight
            medicalState["allergies"] = info.allergies
            medicalState["medications"] = info.medications
            medicalState["medicalConditions"] = info.medicalConditions
            medicalState["pregnancyStatus"] = info.pregnancyStatus
            medicalState["emergencyNotes"] = info.emergencyNotes
            medicalState["organDonor"] = info.organDonor
            medicalState["doctorName"] = info.doctorName
            medicalState["doctorPhone"] = info.doctorPhone
            medicalState["insurance"] = info.insurance
        }
    }

    LaunchedEffect(Unit) {
        userId?.let {
            FirebaseFirestore.getInstance().collection("users").document(it)
                .get()
                .addOnSuccessListener { doc ->
                    doc?.let {
                        profileState["name"] = it.getString("name") ?: ""
                        profileState["email"] = it.getString("email") ?: ""
                        profileState["address"] = it.getString("address") ?: ""
                        profileState["phone"] = it.getString("phone") ?: ""
                        authViewModel.changeProfile(
                            profileState["name"]!!,
                            profileState["email"]!!,
                            profileState["address"]!!,
                            profileState["phone"]!!
                        )
                    }
                }
        }
    }

    NirbhayXMainScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Profile Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = profileState["name"]?.takeIf { it.isNotEmpty() } ?: "Your Name",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = profileState["email"]?.takeIf { it.isNotEmpty() }
                            ?: "your.email@example.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Profile") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Medical Info") }
                )
            }

            // Tab Content
            when (selectedTab) {
                0 -> ProfileTab(
                    profileState = profileState,
                    isEditing = isEditing,
                    onEditingChange = { isEditing = it },
                    authViewModel = authViewModel,
                    userId = userId
                )
                1 -> MedicalInfoTab(
                    medicalState = medicalState,
                    isEditing = isEditing,
                    onEditingChange = { isEditing = it },
                    medicalInfoViewModel = medicalInfoViewModel,
                    userId = userId
                )
            }
        }
    }
}

@Composable
fun ProfileTab(
    profileState: MutableMap<String, String>,
    isEditing: Boolean,
    onEditingChange: (Boolean) -> Unit,
    authViewModel: AuthViewModel,
    userId: String?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (isEditing) "Edit Profile" else "Profile Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            ModernTextField(
                value = profileState["name"] ?: "",
                onValueChange = { if (isEditing) profileState["name"] = it },
                label = "Full Name",
                icon = Icons.Default.Person,
                readOnly = !isEditing
            )
        }

        item {
            ModernTextField(
                value = profileState["email"] ?: "",
                onValueChange = { if (isEditing) profileState["email"] = it },
                label = "Email Address",
                icon = Icons.Default.Email,
                readOnly = !isEditing
            )
        }

        item {
            ModernTextField(
                value = profileState["phone"] ?: "",
                onValueChange = { if (isEditing) profileState["phone"] = it },
                label = "Phone Number",
                icon = Icons.Default.Phone,
                readOnly = !isEditing
            )
        }

        item {
            ModernTextField(
                value = profileState["address"] ?: "",
                onValueChange = { if (isEditing) profileState["address"] = it },
                label = "Address",
                icon = Icons.Default.Home,
                readOnly = !isEditing,
                singleLine = false
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isEditing) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onEditingChange(false) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            userId?.let {
                                FirebaseFirestore.getInstance().collection("users").document(it)
                                    .update(profileState as Map<String, Any>)
                                    .addOnSuccessListener {
                                        authViewModel.changeProfile(
                                            profileState["name"]!!,
                                            profileState["email"]!!,
                                            profileState["address"]!!,
                                            profileState["phone"]!!
                                        )
                                        onEditingChange(false)
                                    }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Changes")
                    }
                }
            }
        } else {
            item {
                Button(
                    onClick = { onEditingChange(true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Edit Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .height(56.dp)
                        .clickable {
                            authViewModel.logout()
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Logout",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .align(Alignment.CenterVertically),
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun MedicalInfoTab(
    medicalState: MutableMap<String, Any>,
    isEditing: Boolean,
    onEditingChange: (Boolean) -> Unit,
    medicalInfoViewModel: MedicalInfoViewModel,
    userId: String?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "🚨 Emergency Medical Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "This information will be displayed on lock screen during emergencies for first responders",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        item {
            Text(
                text = if (isEditing) "Edit Medical Information" else "Medical Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Basic Medical Info
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModernTextField(
                    value = medicalState["bloodType"] as? String ?: "",
                    onValueChange = { if (isEditing) medicalState["bloodType"] = it },
                    label = "Blood Type",
                    icon = Icons.Default.LocalHospital,
                    readOnly = !isEditing,
                    modifier = Modifier.weight(1f)
                )
                ModernTextField(
                    value = medicalState["height"] as? String ?: "",
                    onValueChange = { if (isEditing) medicalState["height"] = it },
                    label = "Height",
                    icon = Icons.Default.Person,
                    readOnly = !isEditing,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModernTextField(
                    value = medicalState["weight"] as? String ?: "",
                    onValueChange = { if (isEditing) medicalState["weight"] = it },
                    label = "Weight",
                    icon = Icons.Default.Person,
                    readOnly = !isEditing,
                    modifier = Modifier.weight(1f)
                )
                ModernTextField(
                    value = medicalState["pregnancyStatus"] as? String ?: "",
                    onValueChange = { if (isEditing) medicalState["pregnancyStatus"] = it },
                    label = "Pregnancy Status",
                    icon = Icons.Default.Person,
                    readOnly = !isEditing,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Critical Medical Info
        item {
            ModernTextField(
                value = medicalState["allergies"] as? String ?: "",
                onValueChange = { if (isEditing) medicalState["allergies"] = it },
                label = "⚠️ Allergies (Critical)",
                icon = Icons.Default.LocalHospital,
                readOnly = !isEditing,
                singleLine = false
            )
        }

        item {
            ModernTextField(
                value = medicalState["medications"] as? String ?: "",
                onValueChange = { if (isEditing) medicalState["medications"] = it },
                label = "💊 Current Medications",
                icon = Icons.Default.LocalHospital,
                readOnly = !isEditing,
                singleLine = false
            )
        }

        item {
            ModernTextField(
                value = medicalState["medicalConditions"] as? String ?: "",
                onValueChange = { if (isEditing) medicalState["medicalConditions"] = it },
                label = "🏥 Medical Conditions",
                icon = Icons.Default.LocalHospital,
                readOnly = !isEditing,
                singleLine = false
            )
        }

        item {
            ModernTextField(
                value = medicalState["emergencyNotes"] as? String ?: "",
                onValueChange = { if (isEditing) medicalState["emergencyNotes"] = it },
                label = "📝 Emergency Notes",
                icon = Icons.Default.LocalHospital,
                readOnly = !isEditing,
                singleLine = false
            )
        }

        // Doctor Information
        item {
            Text(
                text = "Doctor Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            ModernTextField(
                value = medicalState["doctorName"] as? String ?: "",
                onValueChange = { if (isEditing) medicalState["doctorName"] = it },
                label = "👨‍⚕️ Doctor Name",
                icon = Icons.Default.Person,
                readOnly = !isEditing
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModernTextField(
                    value = medicalState["doctorPhone"] as? String ?: "",
                    onValueChange = { if (isEditing) medicalState["doctorPhone"] = it },
                    label = "📞 Doctor Phone",
                    icon = Icons.Default.Phone,
                    readOnly = !isEditing,
                    modifier = Modifier.weight(1f)
                )
                ModernTextField(
                    value = medicalState["insurance"] as? String ?: "",
                    onValueChange = { if (isEditing) medicalState["insurance"] = it },
                    label = "🏥 Insurance",
                    icon = Icons.Default.LocalHospital,
                    readOnly = !isEditing,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Organ Donor Checkbox
        if (isEditing) {
            item {
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = medicalState["organDonor"] as? Boolean ?: false,
                            onCheckedChange = { medicalState["organDonor"] = it }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "💝 Organ Donor",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Check if you are a registered organ donor",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else if (medicalState["organDonor"] as? Boolean == true) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "💝 Registered Organ Donor",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Save/Edit Buttons
        if (isEditing) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onEditingChange(false) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            userId?.let { uid ->
                                val medicalInfo = MedicalInfo(
                                    userId = uid,
                                    bloodType = medicalState["bloodType"] as? String ?: "",
                                    height = medicalState["height"] as? String ?: "",
                                    weight = medicalState["weight"] as? String ?: "",
                                    allergies = medicalState["allergies"] as? String ?: "",
                                    medications = medicalState["medications"] as? String ?: "",
                                    medicalConditions = medicalState["medicalConditions"] as? String ?: "",
                                    pregnancyStatus = medicalState["pregnancyStatus"] as? String ?: "",
                                    emergencyNotes = medicalState["emergencyNotes"] as? String ?: "",
                                    organDonor = medicalState["organDonor"] as? Boolean ?: false,
                                    doctorName = medicalState["doctorName"] as? String ?: "",
                                    doctorPhone = medicalState["doctorPhone"] as? String ?: "",
                                    insurance = medicalState["insurance"] as? String ?: ""
                                )
                                medicalInfoViewModel.saveMedicalInfo(uid, medicalInfo)
                                onEditingChange(false)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Medical Info")
                    }
                }
            }
        } else {
            item {
                Button(
                    onClick = { onEditingChange(true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Edit Medical Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            if (readOnly) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (readOnly) MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (readOnly) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                if (readOnly) {
                    Text(
                        text = value.ifEmpty { "Not provided" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (value.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = singleLine,
                        modifier = Modifier.padding(top = 4.dp)
                    ) { innerTextField ->
                        if (value.isEmpty()) {
                            Text(
                                text = "Enter your ${label.replace("🚨", "").replace("⚠️", "").replace("💊", "").replace("🏥", "").replace("📝", "").replace("👨‍⚕️", "").replace("📞", "").replace("💝", "").trim()}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                }
            }
        }
    }
}