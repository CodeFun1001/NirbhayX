package com.img.nirbhayx.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class EmergencyContactRepository (private val emergencyContactDao: EmergencyContactDao,
                                  private val firestore: FirebaseFirestore,
                                  private val uid: String
) {
    private val userDoc = firestore.collection("users").document(uid)
    private val contactsCollection = userDoc.collection("emergency_contacts")

    suspend fun insert(contact: EmergencyContact) {
        emergencyContactDao.insert(contact)
        contactsCollection.document(contact.id).set(contact)
    }

    fun getAllContacts(): Flow<List<EmergencyContact>> {
        return emergencyContactDao.getAllContacts()
    }

    suspend fun update(contact: EmergencyContact) {
        emergencyContactDao.update(contact)
    }

    suspend fun delete(contact: EmergencyContact) {
        emergencyContactDao.delete(contact)
        contactsCollection.document(contact.id).delete()
    }
}