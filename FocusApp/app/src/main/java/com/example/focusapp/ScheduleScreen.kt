package com.example.focusapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    onNavigateToAppSelection: () -> Unit,
    onNavigateToPomodoro: () -> Unit
) {
    val blocks by viewModel.scheduleBlocks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Focus Schedule") },
                actions = {
                    Button(onClick = onNavigateToPomodoro, modifier = Modifier.padding(end = 8.dp)) {
                        Text("Timer")
                    }
                    Button(onClick = onNavigateToAppSelection) {
                        Text("Apps")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Block")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

            for ((index, dayName) in days.withIndex()) {
                val dayOfWeekNum = index + 1
                val dayBlocks = blocks.filter { it.dayOfWeek == dayOfWeekNum }

                if (dayBlocks.isNotEmpty()) {
                    item {
                        Text(
                            text = dayName,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
                        )
                    }

                    items(dayBlocks, key = { it.id }) { block ->
                        BlockItem(
                            block = block,
                            onDelete = { viewModel.deleteBlock(block) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBlockDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, day, sh, sm, eh, em, pomo ->
                viewModel.addBlock(name, day, sh, sm, eh, em, pomo)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun BlockItem(block: com.example.focusapp.data.ScheduleBlock, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = block.name, style = MaterialTheme.typography.titleMedium)
                val startTime = String.format("%02d:%02d", block.startHour, block.startMinute)
                val endTime = String.format("%02d:%02d", block.endHour, block.endMinute)
                Text(text = "$startTime - $endTime", style = MaterialTheme.typography.bodyMedium)
                if (block.isPomodoroEnabled) {
                    Text(text = "Pomodoro Enabled", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBlockDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int, Int, Int, Int, Int, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf(1) } // 1=Mon .. 7=Sun
    var startHour by remember { mutableStateOf(9) }
    var startMinute by remember { mutableStateOf(0) }
    var endHour by remember { mutableStateOf(10) }
    var endMinute by remember { mutableStateOf(0) }
    var isPomodoroEnabled by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Focus Block") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Task Name") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Simple day selector
                Text("Day of week (1=Mon, 7=Sun): $selectedDay")
                Slider(
                    value = selectedDay.toFloat(),
                    onValueChange = { selectedDay = it.toInt() },
                    valueRange = 1f..7f,
                    steps = 5
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Start: ")
                    // Simplistic time inputs for brevity
                    OutlinedTextField(
                        value = startHour.toString(),
                        onValueChange = { startHour = it.toIntOrNull() ?: 0 },
                        modifier = Modifier.width(60.dp)
                    )
                    Text(":")
                    OutlinedTextField(
                        value = startMinute.toString(),
                        onValueChange = { startMinute = it.toIntOrNull() ?: 0 },
                        modifier = Modifier.width(60.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top=8.dp)) {
                    Text("End:   ")
                    OutlinedTextField(
                        value = endHour.toString(),
                        onValueChange = { endHour = it.toIntOrNull() ?: 0 },
                        modifier = Modifier.width(60.dp)
                    )
                    Text(":")
                    OutlinedTextField(
                        value = endMinute.toString(),
                        onValueChange = { endMinute = it.toIntOrNull() ?: 0 },
                        modifier = Modifier.width(60.dp)
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isPomodoroEnabled, onCheckedChange = { isPomodoroEnabled = it })
                    Text("Enable Pomodoro for this block")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, selectedDay, startHour, startMinute, endHour, endMinute, isPomodoroEnabled) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
