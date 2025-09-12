package com.example.reader.screens

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
import androidx.compose.ui.text.style.TextAlign
import com.example.reader.R
import androidx.compose.ui.platform.LocalConfiguration
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingScreen(navController: NavController) {
    // Gradient background (vertical)
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F4F47), // dark variant
            Color(0xFF24786D), // base splash color
            Color(0xFF2F8F81), // mid accent
            Color(0xFF3FAF9E)  // lighter accent
        )
    )

    val configuration = LocalConfiguration.current
    val sw = configuration.screenWidthDp
    val sh = configuration.screenHeightDp
    val isTablet = sw >= 600
    val isNarrow = sw < 340
    val isShort = sh < 600

    val horizontalPadding = when {
        isTablet -> 64.dp
        isNarrow -> 16.dp
        else -> 24.dp
    }

    val headingSize = when {
        isTablet -> 44.sp
        isNarrow -> 26.sp
        else -> 32.sp
    }
    val headingLineHeight = (headingSize.value * 1.18f).sp
    val subTextSize = when {
        isTablet -> 18.sp
        isNarrow -> 12.sp
        else -> 14.sp
    }

    Scaffold(
        topBar = {
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
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(innerPadding)
                .padding(horizontal = horizontalPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(if (isShort) 32.dp else 48.dp))

                Text(
                    text = if (isTablet) "Read smarter,\non any device" else "Read smarter,\nanytime & anywhere",
                    color = Color.White,
                    fontSize = headingSize,
                    fontWeight = FontWeight.Bold,
                    lineHeight = headingLineHeight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(if (isShort) 12.dp else 16.dp))

                // Subtext
                Text(
                    text = "Your digital bookshelf with hand-picked stories, novels, and more — always within reach.",
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = subTextSize,
                    lineHeight = (subTextSize.value * 1.3f).sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = if (isNarrow) 4.dp else 8.dp)
                )

                Spacer(modifier = Modifier.height(if (isShort) 24.dp else 32.dp))

                // Responsive social auth layout
                if (isNarrow) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        SocialButton(
                            modifier = Modifier.fillMaxWidth(),
                            iconRes = R.drawable.facebook,
                            label = "Facebook",
                            onClick = { }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SocialButton(
                            modifier = Modifier.fillMaxWidth(),
                            iconRes = R.drawable.search,
                            label = "Google",
                            onClick = { }
                        )
                    }
                } else {
                    val maxButtonWidth = min((sw - (horizontalPadding.value * 2) - 40), 300f) // leave spacing
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SocialButton(
                            modifier = Modifier.width(maxButtonWidth.dp / 2f),
                            iconRes = R.drawable.facebook,
                            label = "Facebook",
                            onClick = { }
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        SocialButton(
                            modifier = Modifier.width(maxButtonWidth.dp / 2f),
                            iconRes = R.drawable.search,
                            label = "Google",
                            onClick = { }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(if (isShort) 20.dp else 24.dp))
                Text("OR", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(if (isShort) 12.dp else 16.dp))

                Button(
                    onClick = { navController.navigate(ReaderScreens.LoginScreen.name) {
                        popUpTo(ReaderScreens.OnBoardingScreen.name) { inclusive = true } } },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isNarrow) 46.dp else 50.dp),
                    shape = RoundedCornerShape(if (isNarrow) 20.dp else 24.dp)
                ) {
                    Text(
                        "Continue with email",
                        style = if (isNarrow) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(if (isShort) 24.dp else 32.dp))

                val signInTag = "SIGN_IN_TAG"
                val annotated = buildAnnotatedString {
                    append("Already a reader? ")
                    pushStringAnnotation(tag = signInTag, annotation = "signin")
                    withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) {
                        append("Sign in")
                    }
                    pop()
                }
                ClickableText(
                    text = annotated,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray),
                    onClick = { offset ->
                        annotated.getStringAnnotations(signInTag, offset, offset).firstOrNull()?.let {
                            navController.navigate(ReaderScreens.CreateAccountScreen.name) {
                                popUpTo(ReaderScreens.OnBoardingScreen.name) { inclusive = true }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun SocialButton(
    modifier: Modifier = Modifier,
    iconRes: Int,
    label: String,
    onClick: () -> Unit,
    height: Dp = 56.dp
) {
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
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
