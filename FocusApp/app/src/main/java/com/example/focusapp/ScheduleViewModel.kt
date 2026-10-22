package com.example.focusapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.focusapp.data.AppDatabase
import com.example.focusapp.data.ScheduleBlock
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScheduleViewModel(private val db: AppDatabase) : ViewModel() {

    val scheduleBlocks: StateFlow<List<ScheduleBlock>> = db.scheduleDao().getAllBlocks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addBlock(
        name: String,
        dayOfWeek: Int,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
        isPomodoroEnabled: Boolean
    ) {
        viewModelScope.launch {
            val block = ScheduleBlock(
                name = name,
                dayOfWeek = dayOfWeek,
                startHour = startHour,
                startMinute = startMinute,
                endHour = endHour,
                endMinute = endMinute,
                isPomodoroEnabled = isPomodoroEnabled
            )
            db.scheduleDao().insertBlock(block)
        }
    }

    fun deleteBlock(block: ScheduleBlock) {
        viewModelScope.launch {
            db.scheduleDao().deleteBlock(block)
        }
    }
}

class ScheduleViewModelFactory(
    private val db: AppDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduleViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
