package com.img.nirbhayx.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalInfoDao {
    @Query("SELECT * FROM medical_info WHERE userId = :userId")
    fun getMedicalInfo(userId: String): Flow<MedicalInfo?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalInfo(medicalInfo: MedicalInfo)

    @Update
    suspend fun updateMedicalInfo(medicalInfo: MedicalInfo)

    @Query("DELETE FROM medical_info WHERE userId = :userId")
    suspend fun deleteMedicalInfo(userId: String)
}