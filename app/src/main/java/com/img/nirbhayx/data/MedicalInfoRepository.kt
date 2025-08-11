package com.img.nirbhayx.data

import kotlinx.coroutines.flow.Flow

class MedicalInfoRepository(
    private val medicalInfoDao: MedicalInfoDao
) {
    fun getMedicalInfo(userId: String): Flow<MedicalInfo?> =
        medicalInfoDao.getMedicalInfo(userId)

    suspend fun saveMedicalInfo(medicalInfo: MedicalInfo) {
        medicalInfoDao.insertMedicalInfo(medicalInfo)
    }

    suspend fun updateMedicalInfo(medicalInfo: MedicalInfo) {
        medicalInfoDao.updateMedicalInfo(medicalInfo)
    }

    suspend fun deleteMedicalInfo(userId: String) {
        medicalInfoDao.deleteMedicalInfo(userId)
    }
}