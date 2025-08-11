package com.img.nirbhayx.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ActivityLog::class, EmergencyContact::class, SafetyTip::class, BookmarkedTip::class, MedicalInfo::class] , version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NXDatabase : RoomDatabase() {
    abstract fun activityLogDao(): ActivityDAO
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun safetyTipDao(): SafetyTipDao
    abstract fun medicalInfoDao(): MedicalInfoDao
}
