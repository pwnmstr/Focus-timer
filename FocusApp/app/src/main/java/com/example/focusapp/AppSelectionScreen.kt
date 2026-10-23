package com.example.focusapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(isDarkTheme: Boolean, onToggleTheme: () -> Unit,
    viewModel: AppSelectionViewModel,
    onBack: () -> Unit
) {
    val apps by viewModel.installedApps.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Apps to Block") },
                navigationIcon = {
                    Button(onClick = onBack, modifier = Modifier.padding(8.dp)) {
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
        if (apps.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(apps, key = { it.packageName }) { app ->
                    AppListItem(
                        app = app,
                        onToggle = { isBlocked ->
                            viewModel.toggleAppBlock(app, isBlocked)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AppListItem(
    app: AppItem,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = app.appName, style = MaterialTheme.typography.bodyLarge)
            Text(text = app.packageName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = app.isBlocked,
            onCheckedChange = onToggle
        )
    }
}
