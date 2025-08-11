package com.img.nirbhayx.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.img.nirbhayx.data.Graph
import com.img.nirbhayx.data.MedicalInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicalInfoViewModel : ViewModel() {

    private val _medicalInfo = MutableStateFlow<MedicalInfo?>(null)
    val medicalInfo: StateFlow<MedicalInfo?> = _medicalInfo.asStateFlow()

    fun loadMedicalInfo(userId: String) {
        viewModelScope.launch {
            Graph.medicalInfoRepository.getMedicalInfo(userId).collect { info ->
                _medicalInfo.value = info
            }
        }
    }

    fun saveMedicalInfo(userId: String, medicalInfo: MedicalInfo) {
        viewModelScope.launch {
            val updatedInfo = medicalInfo.copy(
                userId = userId,
                lastUpdated = System.currentTimeMillis()
            )
            Graph.medicalInfoRepository.saveMedicalInfo(updatedInfo)
        }
    }

    fun updateField(field: String, value: Any) {
        val currentInfo = _medicalInfo.value ?: MedicalInfo(userId = "")
        val updatedInfo = when (field) {
            "bloodType" -> currentInfo.copy(bloodType = value as String)
            "height" -> currentInfo.copy(height = value as String)
            "weight" -> currentInfo.copy(weight = value as String)
            "allergies" -> currentInfo.copy(allergies = value as String)
            "medications" -> currentInfo.copy(medications = value as String)
            "medicalConditions" -> currentInfo.copy(medicalConditions = value as String)
            "pregnancyStatus" -> currentInfo.copy(pregnancyStatus = value as String)
            "emergencyNotes" -> currentInfo.copy(emergencyNotes = value as String)
            "organDonor" -> currentInfo.copy(organDonor = value as Boolean)
            "doctorName" -> currentInfo.copy(doctorName = value as String)
            "doctorPhone" -> currentInfo.copy(doctorPhone = value as String)
            "insurance" -> currentInfo.copy(insurance = value as String)
            else -> currentInfo
        }
        _medicalInfo.value = updatedInfo
    }
}