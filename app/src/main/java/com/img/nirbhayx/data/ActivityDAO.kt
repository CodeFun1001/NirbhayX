package com.img.nirbhayx.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ActivityDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(log: ActivityLog)

    @Query("SELECT * FROM activity_log ORDER BY time DESC")
    abstract fun getAll(): Flow<List<ActivityLog>>
}