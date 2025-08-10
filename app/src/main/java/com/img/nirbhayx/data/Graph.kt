package com.img.nirbhayx.data

import android.app.Application
import androidx.room.Room
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

object Graph {
    lateinit var database: NXDatabase
        private set

    private var _emergencyContactRepository: EmergencyContactRepository? = null
    private var _activityRepository: ActivityRepository? = null
    private var _safetyTipRepository: SafetyTipsRepository? = null

    val emergencyContactRepository: EmergencyContactRepository
        get() = _emergencyContactRepository ?: throw IllegalStateException("Repository not initialized. Call createRepositories() first.")

    val activityRepository: ActivityRepository
        get() = _activityRepository ?: throw IllegalStateException("Repository not initialized. Call createRepositories() first.")

    val safetyTipRepository: SafetyTipsRepository
        get() = _safetyTipRepository ?: throw IllegalStateException("Repository not initialized. Call createRepositories() first.")

    fun initDatabase(app: Application) {
        if (!::database.isInitialized) {
            database = Room.databaseBuilder(
                app,
                NXDatabase::class.java,
                "nirbhayx.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    fun createRepositories(uid: String) {
        if (!::database.isInitialized) {
            throw IllegalStateException("Database not initialized.")
        }

        _emergencyContactRepository = EmergencyContactRepository(
            emergencyContactDao = database.emergencyContactDao(),
            firestore = Firebase.firestore,
            uid = uid
        )

        _activityRepository = ActivityRepository(
            activityDAO = database.activityLogDao()
        )

        _safetyTipRepository = SafetyTipsRepository(
            safetyTipDao = database.safetyTipDao()
        )
    }

    fun clearRepositories() {
        _emergencyContactRepository = null
        _activityRepository = null
        _safetyTipRepository = null
    }
}