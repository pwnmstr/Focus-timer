package com.example.focusapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule_blocks ORDER BY dayOfWeek, startHour, startMinute ASC")
    fun getAllBlocks(): Flow<List<ScheduleBlock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: ScheduleBlock)

    @Delete
    suspend fun deleteBlock(block: ScheduleBlock)
}

@Dao
interface BlockedAppDao {
    @Query("SELECT * FROM blocked_apps")
    fun getAllBlockedApps(): Flow<List<BlockedApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedApp(app: BlockedApp)

    @Delete
    suspend fun deleteBlockedApp(app: BlockedApp)

    @Query("DELETE FROM blocked_apps WHERE packageName = :packageName")
    suspend fun deleteByPackage(packageName: String)
}
