package com.example.reader.screens.SignUp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reader.navigation.ReaderScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpTopAppBar(navController : NavController) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = {
                navController.navigate(ReaderScreens.OnBoardingScreen.name) {
                    popUpTo(ReaderScreens.CreateAccountScreen.name) { inclusive = true }
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        title = { Text("Sign up", style = MaterialTheme.typography.titleLarge) },
    )
}

@Composable
fun LogInPrompt(navController: NavController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isVeryNarrow = screenWidth < 360

    val logInTag = "LOG_IN_TAG"
    val baseTextStyle = if (isVeryNarrow) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium
    val clickableString = buildAnnotatedString {
        withStyle(baseTextStyle.toSpanStyle().copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
            append("Already a reader? ")
        }
        pushStringAnnotation(tag = logInTag, annotation = "login")
        withStyle(baseTextStyle.toSpanStyle().copy(color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) {
            append("Login")
        }
        pop()
    }
    ClickableText(
        text = clickableString,
        style = baseTextStyle.copy(color = Color.Unspecified),
        onClick = { offset ->
            clickableString.getStringAnnotations(logInTag, offset, offset).firstOrNull()?.let {
                navController.navigate(ReaderScreens.LoginScreen.name) {
                    popUpTo(ReaderScreens.CreateAccountScreen.name) { inclusive = true }
                }
            }
        }
    )
}


