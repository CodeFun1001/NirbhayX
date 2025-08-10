package com.img.nirbhayx.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.img.nirbhayx.data.ActivityLog
import com.img.nirbhayx.data.EmergencyContact
import com.img.nirbhayx.data.EmergencyContactRepository
import com.img.nirbhayx.data.Graph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EmergencyContactsViewModel : ViewModel() {

    private fun getEmergencyRepo(): EmergencyContactRepository? {
        return try {
            Graph.emergencyContactRepository
        } catch (e: IllegalStateException) {
            null
        }
    }

    private fun getActivityRepo() = try {
        Graph.activityRepository
    } catch (e: IllegalStateException) {
        null
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    val currentTime = System.currentTimeMillis()

    private val _contacts = MutableStateFlow<List<EmergencyContact>>(emptyList())

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            try {
                getEmergencyRepo()?.getAllContacts()?.collect { contacts ->
                    _contacts.value = contacts
                }
            } catch (e: Exception) {
                _contacts.value = emptyList()
            }
        }
    }

    val filteredContacts: StateFlow<List<EmergencyContact>> =
        combine(_contacts, _searchQuery) { contacts, query ->
            if (query.isBlank()) contacts
            else contacts.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.phoneNumber.contains(query)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addContact(contact: EmergencyContact) = viewModelScope.launch {
        try {
            getEmergencyRepo()?.insert(contact)
            getActivityRepo()?.insert(
                ActivityLog(
                    timestamp = currentTime,
                    description = "Emergency Contact ${contact.name} added"
                )
            )
        } catch (e: Exception) {
            Log.d("Insertion Error : ", "$e")
        }
    }

    fun deleteContact(contact: EmergencyContact) = viewModelScope.launch {
        try {
            getEmergencyRepo()?.delete(contact)
            getActivityRepo()?.insert(
                ActivityLog(
                    timestamp = currentTime,
                    description = "Emergency Contact ${contact.name} deleted"
                )
            )
        } catch (e: Exception) {
            Log.d("Deletion Error : ", "$e")
        }
    }

    fun addDefaultContactsIfNeeded() = viewModelScope.launch {
        try {
            val repo = getEmergencyRepo()
            val current = repo?.getAllContacts()?.first() ?: emptyList()
            if (current.isEmpty()) {
                val defaultContacts = listOf(
                    EmergencyContact(name = "Police", phoneNumber = "100"),
                    EmergencyContact(name = "Fire", phoneNumber = "101"),
                    EmergencyContact(name = "Ambulance", phoneNumber = "102")
                )
                defaultContacts.forEach { repo?.insert(it) }
            }
        } catch (e: Exception) {
        }
    }
}