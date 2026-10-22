package com.example.focusapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_blocks")
data class ScheduleBlock(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dayOfWeek: Int, // 1=Mon, 7=Sun
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val isPomodoroEnabled: Boolean
)

@Entity(tableName = "blocked_apps")
data class BlockedApp(
    @PrimaryKey val packageName: String,
    val appName: String
)
