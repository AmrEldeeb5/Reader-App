package com.example.reader

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.reader.navigation.ReaderNavigation
import com.example.reader.ui.theme.ReaderTheme
import com.example.reader.utils.UserPreferences
import com.example.reader.ui.theme.rememberAppThemeState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ReaderApp()
        }
    }
}

@Composable
fun ReaderApp() {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }

    // Centralized, stable theme state with instant persistence
    val themeState = rememberAppThemeState(prefs)

    ReaderTheme(
        darkTheme = themeState.isDarkTheme,
        isGreenTheme = themeState.isGreenTheme
    ) {
        // Apply system bars only when theme changes
        val activity = context as? ComponentActivity
        DisposableEffect(themeState.isDarkTheme) {
            activity?.enableEdgeToEdge(
                statusBarStyle = if (themeState.isDarkTheme) {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.BLACK)
                },
                navigationBarStyle = if (themeState.isDarkTheme) {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                }
            )
            onDispose { }
        }

        Surface(modifier = Modifier.fillMaxSize()) {
            ReaderNavigation(
                isDarkTheme = themeState.isDarkTheme,
                onThemeToggle = { themeState.setDark(it) },
                isGreenTheme = themeState.isGreenTheme,
                onColorSchemeToggle = { themeState.setGreen(it) }
            )
        }
    }
}

// @Preview(showBackground = true)
// @Composable
// fun ReaderAppPreview() {
//     ReaderApp()
// }
