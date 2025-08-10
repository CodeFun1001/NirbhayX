package com.img.nirbhayx.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.img.nirbhayx.data.SafetyTipsRepository

class SafetyTipsViewModelFactory(
    private val repository: SafetyTipsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SafetyTipsViewModel::class.java)) {
            return SafetyTipsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

