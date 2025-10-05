package com.example.reader.screens.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reader.navigation.ReaderScreens
import com.example.reader.utils.UserPreferences
import com.example.reader.utils.rememberResponsiveLayout
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val layout = rememberResponsiveLayout()

    LaunchedEffect(Unit) {
        delay(2000L)

        val userPrefs = UserPreferences(context)

        // Simple navigation logic: Only check "Remember Me" preference
        if (userPrefs.getRememberMe()) {
            // User checked "Remember Me" -> Go to Login with pre-filled email
            navController.navigate(ReaderScreens.LoginScreen.name) {
                popUpTo(ReaderScreens.SplashScreen.name) { inclusive = true }
            }
        } else {
            // User didn't check "Remember Me" -> Always go to OnBoarding
            navController.navigate(ReaderScreens.OnBoardingScreen.name) {
                popUpTo(ReaderScreens.SplashScreen.name) { inclusive = true }
            }
        }
    } // LaunchedEffect properly closed here

    // Responsive font sizes based on WindowSizeClass
    val rFontSize = when {
        layout.isExpanded -> 180.sp      // Tablets/Desktop
        layout.isMedium -> 120.sp        // Phone landscape, small tablets
        layout.isCompactHeight -> 100.sp // Very short screens
        else -> 140.sp                   // Phone portrait (default)
    }

    val readerFontSize = when {
        layout.isExpanded -> 54.sp       // Tablets/Desktop
        layout.isMedium -> 38.sp         // Phone landscape, small tablets
        layout.isCompactHeight -> 32.sp  // Very short screens
        else -> 42.sp                    // Phone portrait (default)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF24786D) // green background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = layout.horizontalPadding,
                    vertical = layout.verticalSpacing
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "R",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = rFontSize,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White,
                    letterSpacing = 2.sp,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.4f),
                        offset = androidx.compose.ui.geometry.Offset(6f, 10f),
                        blurRadius = 15f
                    )
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(layout.verticalSpacing))

            Text(
                text = "Reader",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = readerFontSize,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White.copy(alpha = 0.95f),
                    letterSpacing = 3.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}