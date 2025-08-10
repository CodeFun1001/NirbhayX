package com.img.nirbhayx.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name")
    val name: String = "",
    @ColumnInfo(name = "phoneNumber")
    val phoneNumber: String = ""
)