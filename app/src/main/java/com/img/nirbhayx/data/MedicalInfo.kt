package com.img.nirbhayx.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medical_info")
data class MedicalInfo(
    @PrimaryKey
    val userId: String,
    val bloodType: String = "",
    val height: String = "",
    val weight: String = "",
    val allergies: String = "",
    val medications: String = "",
    val medicalConditions: String = "",
    val pregnancyStatus: String = "",
    val emergencyNotes: String = "",
    val organDonor: Boolean = false,
    val doctorName: String = "",
    val doctorPhone: String = "",
    val insurance: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)
