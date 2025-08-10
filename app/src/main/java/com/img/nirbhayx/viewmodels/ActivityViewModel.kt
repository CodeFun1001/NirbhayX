package com.img.nirbhayx.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.img.nirbhayx.data.ActivityLog
import com.img.nirbhayx.data.ActivityRepository
import com.img.nirbhayx.data.Graph
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class ActivityViewModel : ViewModel() {

    private fun getRepo(): ActivityRepository? {
        return try {
            Graph.activityRepository
        } catch (e: IllegalStateException) {
            null
        }
    }

    val getAllActivities: Flow<List<ActivityLog>>
        get() = getRepo()?.getAllActivities() ?: flowOf(emptyList())

    fun insert(log: ActivityLog) {
        viewModelScope.launch {
            try {
                getRepo()?.insert(log)
            } catch (e: Exception) {
                Log.d("Activity Repo Error", "Exception in insert: $e")
            }
        }
    }
}
