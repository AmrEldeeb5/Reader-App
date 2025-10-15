package com.example.reader.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reader.navigation.ReaderScreens
import com.example.reader.R
import com.example.reader.utils.cinzelFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingTopAppBar(navController : NavController) {
    // Define Cinzel font family
    val cinzelFontFamily = FontFamily(
        Font(R.font.cinzel_regular, FontWeight.Normal),
        Font(R.font.cinzel_medium, FontWeight.Medium),
        Font(R.font.cinzel_bold, FontWeight.Bold)
    )

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "R",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        fontFamily = cinzelFontFamily,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Reader",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp,
                        fontFamily = cinzelFontFamily,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                )
            }
        },
        actions = {
            TextButton(onClick = {
                navController.navigate(ReaderScreens.ReaderHomeScreen.name) {
                    popUpTo(ReaderScreens.OnBoardingScreen.name) { inclusive = true }
                }
            }) {
                Text(
                    "Skip",
                    color = Color.White,
                    fontFamily = cinzelFontFamily
                    ,fontSize = (16.sp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        modifier = Modifier.background(Color.Transparent)
    )
}

@Composable
fun SocialButton(
    modifier: Modifier = Modifier,
    iconRes: Int,
    label: String,
    onClick: () -> Unit,
    height: Dp = 56.dp
) {
    // Define Cinzel font family

    Surface(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontFamily = cinzelFontFamily
                )
            )
        }
    }
}