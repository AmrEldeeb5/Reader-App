package com.example.reader.ui.theme

import androidx.compose.ui.graphics.Color

// Brand greens (shared with Theme.kt). Public so UI components can reference them directly.
val GreenDark = Color(0xFF0F4F47)
val GreenPrimary = Color(0xFF24786D)
val GreenMid = Color(0xFF2F8F81)
val GreenLight = Color(0xFF3FAF9E)
val GreenExtraLight = Color(0xFFBEEDE5)
val BrandErrorRed = Color(0xFFBA1A1A)

// Legacy palette (kept to avoid breaking references; mark deprecated)
@Deprecated("Use brand green palette instead (GreenPrimary, etc.)")
val Purple80 = Color(0xFFD0BCFF)
@Deprecated("Use brand green palette instead (GreenPrimary, etc.)")
val PurpleGrey80 = Color(0xFFCCC2DC)
@Deprecated("Use brand green palette instead (GreenPrimary, etc.)")
val Pink80 = Color(0xFFEFB8C8)

@Deprecated("Use brand green palette instead (GreenPrimary, etc.)")
val Purple40 = Color(0xFF6650a4)
@Deprecated("Use brand green palette instead (GreenPrimary, etc.)")
val PurpleGrey40 = Color(0xFF625b71)
@Deprecated("Use brand green palette instead (GreenPrimary, etc.)")
val Pink40 = Color(0xFF7D5260)