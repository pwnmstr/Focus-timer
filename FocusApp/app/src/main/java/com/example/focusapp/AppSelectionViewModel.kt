package com.example.focusapp

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.focusapp.data.AppDatabase
import com.example.focusapp.data.BlockedApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AppItem(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean,
    val isBlocked: Boolean
)

class AppSelectionViewModel(
    private val context: Context,
    private val db: AppDatabase
) : ViewModel() {

    private val _installedApps = MutableStateFlow<List<AppItem>>(emptyList())
    val installedApps: StateFlow<List<AppItem>> = _installedApps.asStateFlow()

    init {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch {
            val pm = context.packageManager
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            val blockedApps = db.blockedAppDao().getAllBlockedApps().first().map { it.packageName }.toSet()

            val apps = packages.mapNotNull { appInfo ->
                // Filter out some core system apps, but let the user see normal apps
                val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                val isUpdatedSystem = (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

                // We might still want to show system apps if the user *really* wants to block them (like YouTube, Chrome),
                // so we don't strictly filter them all out, or we can provide a toggle. For now, filter strictly non-system OR user-installed.
                if (!isSystem || isUpdatedSystem) {
                    val appName = pm.getApplicationLabel(appInfo).toString()
                    AppItem(
                        packageName = appInfo.packageName,
                        appName = appName,
                        isSystemApp = isSystem,
                        isBlocked = blockedApps.contains(appInfo.packageName)
                    )
                } else {
                    null
                }
            }.sortedBy { it.appName.lowercase() }

            _installedApps.value = apps
        }
    }

    fun toggleAppBlock(app: AppItem, isBlocked: Boolean) {
        viewModelScope.launch {
            val dao = db.blockedAppDao()
            if (isBlocked) {
                dao.insertBlockedApp(BlockedApp(packageName = app.packageName, appName = app.appName))
            } else {
                dao.deleteByPackage(app.packageName)
            }

            // Update UI state
            _installedApps.value = _installedApps.value.map {
                if (it.packageName == app.packageName) it.copy(isBlocked = isBlocked) else it
            }
        }
    }
}

class AppSelectionViewModelFactory(
    private val context: Context,
    private val db: AppDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppSelectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppSelectionViewModel(context, db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
