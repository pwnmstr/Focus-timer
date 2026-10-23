package com.example.focusapp

import android.os.CountDownTimer
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(isDarkTheme: Boolean, onToggleTheme: () -> Unit, onBack: () -> Unit) {
    var isRunning by remember { mutableStateOf(false) }
    var isBreak by remember { mutableStateOf(false) }

    // Configurable minutes
    var workMinutes by remember { mutableStateOf(25) }
    var breakMinutes by remember { mutableStateOf(5) }

    var timeRemaining by remember { mutableStateOf(workMinutes * 60L) }
    var timer: CountDownTimer? by remember { mutableStateOf(null) }

    fun startTimer() {
        isRunning = true
        timer = object : CountDownTimer(timeRemaining * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished / 1000
            }

            override fun onFinish() {
                isRunning = false
                isBreak = !isBreak
                timeRemaining = if (isBreak) breakMinutes * 60L else workMinutes * 60L
            }
        }.start()
    }

    fun stopTimer() {
        timer?.cancel()
        isRunning = false
    }

    fun resetTimer() {
        stopTimer()
        isBreak = false
        timeRemaining = workMinutes * 60L
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pomodoro Timer") },
                navigationIcon = {
                    Button(onClick = {
                        stopTimer()
                        onBack()
                    }, modifier = Modifier.padding(8.dp)) {
                        Text("Back")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Text(if (isDarkTheme) "☀️" else "🌙")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = if (isBreak) "Break Time!" else "Focus Time",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isBreak) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            val minutes = timeRemaining / 60
            val seconds = timeRemaining % 60
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(48.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (isRunning) {
                    Button(onClick = { stopTimer() }) {
                        Text("Pause")
                    }
                } else {
                    Button(onClick = { startTimer() }) {
                        Text("Start")
                    }
                }

                Button(onClick = { resetTimer() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Reset")
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Settings", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Work (min): ")
                        Slider(
                            value = workMinutes.toFloat(),
                            onValueChange = {
                                workMinutes = it.toInt()
                                if (!isRunning && !isBreak) timeRemaining = workMinutes * 60L
                            },
                            valueRange = 1f..60f,
                            modifier = Modifier.weight(1f)
                        )
                        Text("$workMinutes")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Break (min): ")
                        Slider(
                            value = breakMinutes.toFloat(),
                            onValueChange = {
                                breakMinutes = it.toInt()
                                if (!isRunning && isBreak) timeRemaining = breakMinutes * 60L
                            },
                            valueRange = 1f..30f,
                            modifier = Modifier.weight(1f)
                        )
                        Text("$breakMinutes")
                    }
                }
            }
        }
    }
}
