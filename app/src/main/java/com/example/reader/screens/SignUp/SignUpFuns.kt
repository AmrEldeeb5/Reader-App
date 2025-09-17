package com.example.reader.screens.SignUp

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.navigation.NavController
import com.example.reader.navigation.ReaderScreens

// Constants for login prompt
private object LoginPromptConstants {
    const val NARROW_WIDTH_THRESHOLD = 360
    const val LOG_IN_TAG = "LOG_IN_TAG"
    const val LOGIN_ANNOTATION = "login"
}

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
    val isVeryNarrow = screenWidth < LoginPromptConstants.NARROW_WIDTH_THRESHOLD

    val baseTextStyle = if (isVeryNarrow)
        MaterialTheme.typography.bodySmall
    else
        MaterialTheme.typography.bodyMedium

    val clickableString = buildAnnotatedString {
        // "Already a reader? " text
        withStyle(
            baseTextStyle.toSpanStyle().copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        ) {
            append("Already a reader? ")
        }

        // "Login" clickable text
        pushStringAnnotation(
            tag = LoginPromptConstants.LOG_IN_TAG,
            annotation = LoginPromptConstants.LOGIN_ANNOTATION
        )
        withStyle(
            baseTextStyle.toSpanStyle().copy(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Login")
        }
        pop()
    }

    ClickableText(
        text = clickableString,
        style = baseTextStyle.copy(color = Color.Unspecified),
        onClick = { offset ->
            clickableString.getStringAnnotations(
                LoginPromptConstants.LOG_IN_TAG,
                offset,
                offset
            ).firstOrNull()?.let {
                navController.navigate(ReaderScreens.LoginScreen.name) {
                    popUpTo(ReaderScreens.CreateAccountScreen.name) { inclusive = true }
                }
            }
        }
    )
}


