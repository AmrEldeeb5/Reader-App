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

// Green Dark Color Scheme
private val GreenDarkColorScheme = darkColorScheme(
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

    background = DarkGreenBackground,
    onBackground = DarkGreenOnBackground,
    surface = DarkGreenSurface,
    onSurface = DarkGreenOnBackground,

    surfaceVariant = GreenCardBackground,
    onSurfaceVariant = TextColor,

    outline = SubtleTextColor,
    outlineVariant = Color(0xFF3A3A3A),

    error = BrandErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

// Green Light Color Scheme
private val GreenLightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenLightPrimaryContainer,
    onPrimaryContainer = GreenDark,

    secondary = GreenMid,
    onSecondary = Color.White,
    secondaryContainer = GreenLightSecondaryContainer,
    onSecondaryContainer = GreenDark,

    tertiary = GreenLight,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC8F2EC), // Light mint tertiary container
    onTertiaryContainer = GreenDark,

    background = GreenLightBackground,
    onBackground = GreenLightTextPrimary,
    surface = GreenLightSurface,
    onSurface = GreenLightTextPrimary,

    surfaceVariant = GreenLightCardBackground,
    onSurfaceVariant = GreenLightTextSecondary,

    outline = GreenLightTextTertiary,
    outlineVariant = GreenLightBorder,

    error = BrandErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFEDEA),
    onErrorContainer = Color(0xFF410002),

    surfaceContainer = GreenLightCardBackground,
    surfaceContainerHigh = Color(0xFFEBF7F4),
    surfaceContainerHighest = Color(0xFFD9F0EA)
)

// Brown Dark Color Scheme
private val BrownDarkColorScheme = darkColorScheme(
    primary = BrownPrimary,
    onPrimary = Color.White,
    primaryContainer = BrownDark,
    onPrimaryContainer = BrownExtraLight,

    secondary = BrownMid,
    onSecondary = Color.White,
    secondaryContainer = BrownDark,
    onSecondaryContainer = BrownLight,

    tertiary = BrownLight,
    onTertiary = Color.White,
    tertiaryContainer = BrownDark,
    onTertiaryContainer = BrownExtraLight,

    background = DarkBrownBackground,
    onBackground = DarkBrownOnBackground,
    surface = DarkBrownSurface,
    onSurface = DarkBrownOnBackground,

    surfaceVariant = BrownCardBackground,
    onSurfaceVariant = TextColor,

    outline = SubtleTextColor,
    outlineVariant = Color(0xFF3A3A3A),

    error = BrandErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

// Brown Light Color Scheme
private val BrownLightColorScheme = lightColorScheme(
    primary = BrownPrimary,
    onPrimary = Color.White,
    primaryContainer = BrownLightPrimaryContainer,
    onPrimaryContainer = BrownDark,

    secondary = BrownMid,
    onSecondary = Color.White,
    secondaryContainer = BrownLightSecondaryContainer,
    onSecondaryContainer = BrownDark,

    tertiary = BrownLight,
    onTertiary = Color.White,
    tertiaryContainer = BrownLightPrimaryContainer,
    onTertiaryContainer = BrownDark,

    background = BrownLightBackground,
    onBackground = BrownLightTextPrimary,
    surface = BrownLightSurface,
    onSurface = BrownLightTextPrimary,

    surfaceVariant = BrownLightCardBackground,
    onSurfaceVariant = BrownLightTextSecondary,

    outline = BrownLightTextTertiary,
    outlineVariant = BrownLightBorder,

    error = BrandErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFEDEA),
    onErrorContainer = Color(0xFF410002),

    surfaceContainer = BrownLightCardBackground,
    surfaceContainerHigh = BrownLightSecondaryContainer,
    surfaceContainerHighest = BrownLightPrimaryContainer
)

@Composable
fun ReaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    isGreenTheme: Boolean = true, // Green is default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isGreenTheme -> {
            if (darkTheme) GreenDarkColorScheme else GreenLightColorScheme
        }
        else -> {
            if (darkTheme) BrownDarkColorScheme else BrownLightColorScheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}