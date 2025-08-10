package com.img.nirbhayx.viewmodels

import android.app.Application
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.img.nirbhayx.data.EmergencySharingDataStore
import com.img.nirbhayx.data.EmergencySharingSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EmergencySharingViewModel(app: Application) : AndroidViewModel(app) {

    private val dataStore = EmergencySharingDataStore(app)

    val uiState = dataStore.preferencesFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        EmergencySharingSettings(
            isLocationSharingEnabled = false,
            isAutoCallEnabled = false,
            isSmsEnabled = false,
            isAudioRecordingEnabled = false,
            isVideoRecordingEnabled = false,
            selectedContactId = ""
        )
    )


    fun updateSetting(key: Preferences.Key<Boolean>, value: Boolean) {
        viewModelScope.launch {
            dataStore.updateSetting(key, value)
        }
    }
}


