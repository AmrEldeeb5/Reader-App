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
import androidx.compose.ui.tooling.preview.Preview
import com.example.reader.navigation.ReaderNavigation
import com.example.reader.ui.theme.ReaderTheme

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
    // Get the system theme first
    val systemInDarkTheme = isSystemInDarkTheme()

    // Then use it in the remember block
    var isDarkTheme by remember { mutableStateOf(systemInDarkTheme) }

    ReaderTheme(darkTheme = isDarkTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            ReaderNavigation(
                isDarkTheme = isDarkTheme,
                onThemeToggle = { isDarkTheme = it }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReaderAppPreview() {
    ReaderApp()
}