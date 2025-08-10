package com.img.nirbhayx.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SafetyTipDao {
    @Query("SELECT * FROM safety_tips")
    fun getAllTips(): Flow<List<SafetyTip>>

    @Query("SELECT * FROM safety_tips")
    fun getAllTipsSync(): List<SafetyTip>

    @Query("SELECT * FROM safety_tips WHERE category = :category ORDER BY priority ASC")
    fun getTipsByCategory(category: SafetyCategory): Flow<List<SafetyTip>>

    @Query("SELECT * FROM safety_tips WHERE titleKey LIKE '%' || :query || '%' OR contentKey LIKE '%' || :query || '%'")
    fun searchTips(query: String): Flow<List<SafetyTip>>

    @Query("SELECT st.* FROM safety_tips st INNER JOIN bookmarked_tips bt ON st.id = bt.tipId ORDER BY bt.bookmarkedAt DESC")
    fun getBookmarkedTips(): Flow<List<SafetyTip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTip(tip: SafetyTip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTips(tips: List<SafetyTip>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun bookmarkTip(bookmark: BookmarkedTip)

    @Query("DELETE FROM bookmarked_tips WHERE tipId = :tipId")
    suspend fun removeBookmark(tipId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_tips WHERE tipId = :tipId)")
    fun isBookmarked(tipId: String): Flow<Boolean>
}