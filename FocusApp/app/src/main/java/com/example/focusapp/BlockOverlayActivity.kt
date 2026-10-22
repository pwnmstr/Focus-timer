package com.example.focusapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class BlockOverlayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var tapCount by remember { mutableStateOf(0) }
            val coroutineScope = rememberCoroutineScope()

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "FOCUS MODE ACTIVE",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Get back to work!",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        Button(
                            onClick = {
                                tapCount++
                                if (tapCount >= 5) {
                                    // Set override globally or in SharedPreferences
                                    val prefs = getSharedPreferences("focus_prefs", MODE_PRIVATE)
                                    prefs.edit().putLong("override_until", System.currentTimeMillis() + 15 * 60 * 1000).apply()
                                    finish() // Close overlay
                                }
                            }
                        ) {
                            Text("I really need to use this app")
                        }

                        if (tapCount > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Taps: $tapCount/5 for 15-min bypass",
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Prevent going back to the blocked app
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        super.onBackPressed()
    }
}
