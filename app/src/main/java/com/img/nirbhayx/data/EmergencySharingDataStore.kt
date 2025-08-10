package com.img.nirbhayx.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "emergency_prefs")

object EmergencySharingPrefsKeys {
    val LOCATION = booleanPreferencesKey("share_location")
    val AUTO_CALL = booleanPreferencesKey("auto_call")
    val AUDIO = booleanPreferencesKey("audio_record")
    val VIDEO = booleanPreferencesKey("video_record")
    val SMS = booleanPreferencesKey("sms")
    val SELECTED_CONTACT_ID = stringPreferencesKey("selected_contact_id")
}

class EmergencySharingDataStore(private val context: Context) {
    val preferencesFlow: Flow<EmergencySharingSettings> = context.dataStore.data.map { prefs ->
        EmergencySharingSettings(
            isLocationSharingEnabled = prefs[EmergencySharingPrefsKeys.LOCATION] ?: false,
            isAutoCallEnabled = prefs[EmergencySharingPrefsKeys.AUTO_CALL] ?: false,
            isAudioRecordingEnabled = prefs[EmergencySharingPrefsKeys.AUDIO] ?: false,
            isVideoRecordingEnabled = (prefs[EmergencySharingPrefsKeys.VIDEO] ?: false),
            isSmsEnabled = (prefs[EmergencySharingPrefsKeys.SMS] ?: false),
            selectedContactId = prefs[EmergencySharingPrefsKeys.SELECTED_CONTACT_ID] ?: ""
        )
    }

    suspend fun updateSetting(key: Preferences.Key<Boolean>, value: Boolean) {
        context.dataStore.edit { it[key] = value }
    }
}

data class EmergencySharingSettings(
    var isLocationSharingEnabled: Boolean,
    var isAutoCallEnabled: Boolean,
    var isAudioRecordingEnabled: Boolean,
    var isVideoRecordingEnabled: Boolean,
    var isSmsEnabled: Boolean,
    val selectedContactId: String
)
