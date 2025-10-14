package com.example.reader.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reader.navigation.ReaderScreens
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import com.example.reader.R
import androidx.compose.ui.platform.LocalConfiguration
import coil.compose.AsyncImage
import com.example.reader.ui.theme.animatedScaffoldContainerColor
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingScreen(navController: NavController) {
    // Define Cinzel font family
    val cinzelFontFamily = FontFamily(
        Font(R.font.cinzel_regular, FontWeight.Normal),
        Font(R.font.cinzel_medium, FontWeight.Medium),
        Font(R.font.cinzel_bold, FontWeight.Bold)
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image - full screen, behind everything
        BackGround()

        Scaffold(
            topBar = {
                OnBoardingTopAppBar(navController)
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Main Heading
                Text(
                    text = "Get\nStarted!",
                    fontSize = 64.sp,
                    fontFamily = cinzelFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    lineHeight = 60.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .padding(bottom = 8.dp)
                )

                // Tagline
                Text(
                    text = "Join us now and start\nYour reading Journey.",
                    fontSize = 24.sp,
                    fontFamily = cinzelFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    lineHeight = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .padding(bottom = 48.dp)
                )

                // Create Account Button
                Button(
                    onClick = {
                        navController.navigate(ReaderScreens.CreateAccountScreen.name) {
                            popUpTo(ReaderScreens.OnBoardingScreen.name) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE8D7C3), // Cream/beige color from image
                        contentColor = Color(0xFF5D4037)
                    ),
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Create an account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5D4037) // Dark brown text
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sign In Button - Outlined
                OutlinedButton(
                    onClick = {
                        navController.navigate(ReaderScreens.LoginScreen.name) {
                            popUpTo(ReaderScreens.OnBoardingScreen.name) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    border = BorderStroke(2.dp, Color(0xFF8D6E63)), // Brown border
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Sign in to your account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun BackGround() {
    AsyncImage(
        model = R.drawable.beautiful_world,
        contentDescription = "Background Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}