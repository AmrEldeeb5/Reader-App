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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reader.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    LaunchedEffect(Unit) {
        delay(2000L)
      //  navController.navigate(ReaderScreens.OnBoardingScreen.name) {
          //  popUpTo(ReaderScreens.SplashScreen.name) { inclusive = true }
       // }
        if (FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()) {
            navController.navigate(ReaderScreens.OnBoardingScreen.name) {
                popUpTo(ReaderScreens.SplashScreen.name) { inclusive = true }
            }
        } else {
            navController.navigate(ReaderScreens.ReaderHomeScreen.name) {
                popUpTo(ReaderScreens.SplashScreen.name) { inclusive = true }
            }
        }
    } // LaunchedEffect properly closed here

    // Responsive design - get screen dimensions
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // Calculate responsive sizes based on screen dimensions
    val isTablet = screenWidth > 600.dp
    val isLandscape = screenWidth > screenHeight

    // Responsive font sizes
    val rFontSize = when {
        isTablet -> 180.sp
        isLandscape -> (screenHeight.value * 0.25f).sp
        else -> (screenWidth.value * 0.3f).sp
    }

    val readerFontSize = when {
        isTablet -> 54.sp
        isLandscape -> (screenHeight.value * 0.08f).sp
        else -> (screenWidth.value * 0.1f).sp
    }

    // Responsive spacing
    val spacingBetween = when {
        isTablet -> 24.dp
        isLandscape -> 12.dp
        else -> 16.dp
    }

    val sidePadding = when {
        isTablet -> 48.dp
        isLandscape -> 32.dp
        else -> 24.dp
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF24786D) // green background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = sidePadding, vertical = 24.dp),
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

            Spacer(modifier = Modifier.height(spacingBetween))

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