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
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenDark,
    onPrimaryContainer = GreenExtraLight,

    secondary = GreenMid,
    onSecondary = Color.White,
    secondaryContainer = GreenDark,
    onSecondaryContainer = GreenLight,

    tertiary = GreenLight,
    onTertiary = Color.White,
    tertiaryContainer = GreenDark,
    onTertiaryContainer = GreenExtraLight,

    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnBackground,

    surfaceVariant = CardBackground,
    onSurfaceVariant = TextColor,

    outline = SubtleTextColor,
    outlineVariant = Color(0xFF3A3A3A),

    error = BrandErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = GreenDark,

    secondary = GreenMid,
    onSecondary = Color.White,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = GreenDark,

    tertiary = GreenLight,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC8F2EC), // Light mint tertiary container
    onTertiaryContainer = GreenDark,

    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,

    surfaceVariant = LightCardBackground,
    onSurfaceVariant = LightTextSecondary,

    outline = LightTextTertiary,
    outlineVariant = LightBorder,

    error = BrandErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFEDEA), // Light red container
    onErrorContainer = Color(0xFF410002),

    // Additional surface colors for better hierarchy
    surfaceContainer = LightCardBackground,
    surfaceContainerHigh = Color(0xFFEBF7F4), // Light green tint
    surfaceContainerHighest = Color(0xFFD9F0EA) // Slightly more green
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