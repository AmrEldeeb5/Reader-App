package com.example.reader.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GreenLight,
    onPrimary = Color(0xFF003731),
    primaryContainer = GreenDark,
    onPrimaryContainer = GreenExtraLight,

    secondary = GreenMid,
    onSecondary = Color(0xFF003731),
    secondaryContainer = GreenDark,
    onSecondaryContainer = GreenExtraLight,

    tertiary = GreenPrimary,
    onTertiary = Color.White,
    tertiaryContainer = GreenMid,
    onTertiaryContainer = Color.White,

    background = Color(0xFF09201D),
    onBackground = Color(0xFFDBF2EE),
    surface = Color(0xFF0E2724),
    onSurface = Color(0xFFCFE7E3),

    error = BrandErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenLight,
    onPrimaryContainer = Color(0xFF003731),

    secondary = GreenMid,
    onSecondary = Color.White,
    secondaryContainer = GreenLight,
    onSecondaryContainer = Color(0xFF003731),

    tertiary = GreenDark,
    onTertiary = Color.White,
    tertiaryContainer = GreenExtraLight,
    onTertiaryContainer = Color(0xFF00201B),

    background = Color(0xFFD3E1DF), // Slightly darker than previous to separate from white
    onBackground = Color(0xFF0D1F1D),
    surface = Color(0xFFBFD1CF), // Slightly darker than previous to separate from white
    onSurface = Color(0xFF102725),

    error = BrandErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

@Composable
fun ReaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // disable dynamic so brand colors are consistent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}