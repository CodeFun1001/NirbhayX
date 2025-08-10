package com.img.nirbhayx.data

import kotlinx.coroutines.flow.Flow

class ActivityRepository(private val activityDAO: ActivityDAO) {

    suspend fun insert(log: ActivityLog) {
        activityDAO.insert(log)
    }

    fun getAllActivities(): Flow<List<ActivityLog>> {
        return activityDAO.getAll()
    }

}