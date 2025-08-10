package com.img.nirbhayx.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class EmergencyContactDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(contact: EmergencyContact)

    @Query("SELECT * FROM emergency_contacts WHERE id = :id")
    abstract fun getContactById(id: String): Flow<EmergencyContact?>

    @Query("SELECT * FROM emergency_contacts")
    abstract fun getAllContacts(): Flow<List<EmergencyContact>>

    @Update
    abstract suspend fun update(contact: EmergencyContact)

    @Delete
    abstract suspend fun delete(contact: EmergencyContact)
}