package com.example.reader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.reader.navigation.ReaderNavigation
import com.example.reader.ui.theme.ReaderTheme
import com.example.reader.utils.UserPreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReaderApp()
        }
    }
}

@Composable
fun ReaderApp() {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }

    // Get the system theme first
    val systemInDarkTheme = isSystemInDarkTheme()

    // Load saved theme preferences directly, not in remember block
    val savedDarkTheme = userPreferences.getDarkTheme()
    val savedGreenTheme = userPreferences.getGreenTheme()

    // Theme states - initialize with saved values or defaults
    var isDarkTheme by remember { mutableStateOf(savedDarkTheme ?: systemInDarkTheme) }
    var isGreenTheme by remember { mutableStateOf(savedGreenTheme) }

    ReaderTheme(
        darkTheme = isDarkTheme,
        isGreenTheme = isGreenTheme
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            ReaderNavigation(
                isDarkTheme = isDarkTheme,
                onThemeToggle = { newDarkTheme ->
                    isDarkTheme = newDarkTheme
                    userPreferences.setDarkTheme(newDarkTheme) // Save preference
                },
                isGreenTheme = isGreenTheme,
                onColorSchemeToggle = { newGreenTheme ->
                    isGreenTheme = newGreenTheme
                    userPreferences.setGreenTheme(newGreenTheme) // Save preference
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReaderAppPreview() {
    ReaderApp()
}