package com.example.focusapp

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusapp.data.AppDatabase

enum class Screen {
    SCHEDULE, APPS, POMODORO
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf(Screen.SCHEDULE) }

                    // Simple Accessibility Service permission check/prompt
                    var showAccessibilityPrompt by remember { mutableStateOf(true) }
                    val context = LocalContext.current

                    if (showAccessibilityPrompt) {
                        AlertDialog(
                            onDismissRequest = { showAccessibilityPrompt = false },
                            title = { Text("Permission Required") },
                            text = { Text("To block apps effectively, please enable the Accessibility Service for FocusApp in your system settings.") },
                            confirmButton = {
                                Button(onClick = {
                                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                                    showAccessibilityPrompt = false
                                }) {
                                    Text("Go to Settings")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showAccessibilityPrompt = false }) {
                                    Text("Later")
                                }
                            }
                        )
                    }

                    when (currentScreen) {
                        Screen.SCHEDULE -> {
                            val vm: ScheduleViewModel = viewModel(factory = ScheduleViewModelFactory(db))
                            ScheduleScreen(
                                viewModel = vm,
                                onNavigateToAppSelection = { currentScreen = Screen.APPS },
                                onNavigateToPomodoro = { currentScreen = Screen.POMODORO }
                            )
                        }
                        Screen.APPS -> {
                            val vm: AppSelectionViewModel = viewModel(factory = AppSelectionViewModelFactory(context, db))
                            AppSelectionScreen(
                                viewModel = vm,
                                onBack = { currentScreen = Screen.SCHEDULE }
                            )
                        }
                        Screen.POMODORO -> {
                            PomodoroScreen(
                                onBack = { currentScreen = Screen.SCHEDULE }
                            )
                        }
                    }
                }
            }
        }
    }
}
