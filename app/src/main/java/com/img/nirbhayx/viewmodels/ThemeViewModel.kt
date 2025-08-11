package com.img.nirbhayx.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.img.nirbhayx.data.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ThemeViewModel(private val preferencesManager: PreferencesManager) : ViewModel() {

    private val _isDarkTheme = MutableStateFlow(
        runBlocking {
            try {
                preferencesManager.isDarkTheme.first()
            } catch (e: Exception) {
                false
            }
        }
    )
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                preferencesManager.isDarkTheme.collect { isDark ->
                    if (_isDarkTheme.value != isDark) {
                        _isDarkTheme.value = isDark
                    }
                }
            } catch (e: Exception) {
                _isDarkTheme.value = false
            }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            try {
                preferencesManager.setDarkTheme(!_isDarkTheme.value)
            } catch (e: Exception) {
            }
        }
    }

    fun setTheme(isDark: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setDarkTheme(isDark)
            } catch (e: Exception) {
            }
        }
    }
}