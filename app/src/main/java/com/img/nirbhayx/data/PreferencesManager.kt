package com.img.nirbhayx.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }
        .catch { exception ->
            emit(false)
        }
        .onStart {
            emit(false)
        }

    suspend fun setDarkTheme(isDark: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[DARK_THEME_KEY] = isDark
            }
        } catch (e: Exception) {
        }
    }

    fun getCommunityAlertsEnabled(): Boolean {
        return try {
            val sharedPrefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            sharedPrefs.getBoolean("community_alerts", true)
        } catch (e: Exception) {
            true
        }
    }
}