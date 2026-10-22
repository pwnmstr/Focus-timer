package com.example.focusapp

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.focusapp.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class AppBlockingService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private var blockedAppsCache: Set<String> = emptySet()

    override fun onServiceConnected() {
        super.onServiceConnected()
        val db = AppDatabase.getDatabase(this)
        serviceScope.launch {
            db.blockedAppDao().getAllBlockedApps().collect { apps ->
                blockedAppsCache = apps.map { it.packageName }.toSet()
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return

            // Check if app is in blocked list
            if (blockedAppsCache.contains(packageName)) {

                // 1. Check if we are currently in an active override period
                val prefs = getSharedPreferences("focus_prefs", MODE_PRIVATE)
                val overrideUntil = prefs.getLong("override_until", 0)
                if (System.currentTimeMillis() < overrideUntil) {
                    return // Override active, allow app
                }

                // 2. Check if there is an active schedule
                serviceScope.launch {
                    val db = AppDatabase.getDatabase(this@AppBlockingService)
                    val blocks = db.scheduleDao().getAllBlocks().first()

                    val now = Calendar.getInstance()
                    // Map Sunday=1 -> 7 (in our logic DayOfWeek: 1=Mon, 7=Sun)
                    val currentDay = when(now.get(Calendar.DAY_OF_WEEK)) {
                        Calendar.MONDAY -> 1
                        Calendar.TUESDAY -> 2
                        Calendar.WEDNESDAY -> 3
                        Calendar.THURSDAY -> 4
                        Calendar.FRIDAY -> 5
                        Calendar.SATURDAY -> 6
                        Calendar.SUNDAY -> 7
                        else -> 1
                    }
                    val currentHour = now.get(Calendar.HOUR_OF_DAY)
                    val currentMinute = now.get(Calendar.MINUTE)
                    val currentTimeInMinutes = currentHour * 60 + currentMinute

                    val isCurrentlyBlocked = blocks.any { block ->
                        block.dayOfWeek == currentDay &&
                        (block.startHour * 60 + block.startMinute) <= currentTimeInMinutes &&
                        (block.endHour * 60 + block.endMinute) > currentTimeInMinutes
                    }

                    if (isCurrentlyBlocked) {
                        launchOverlayActivity()
                    }
                }
            }
        }
    }

    private fun launchOverlayActivity() {
        val intent = Intent(this, BlockOverlayActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onInterrupt() {
        // Do nothing
    }
}
