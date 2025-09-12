package com.example.reader.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reader.navigation.ReaderScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingTopAppBar(navController : NavController) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "R",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Serif,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Reader",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                )
            }
        },
        actions = {
            TextButton(onClick = {
                navController.navigate(ReaderScreens.LoginScreen.name) {
                    popUpTo(ReaderScreens.OnBoardingScreen.name) { inclusive = true }
                }
            }) { Text("Skip", color = Color.White.copy(alpha = 0.9f)) }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        modifier = Modifier.background(Color.Transparent)
    )
}