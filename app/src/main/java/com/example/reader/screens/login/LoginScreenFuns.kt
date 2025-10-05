package com.example.reader.screens.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reader.navigation.ReaderScreens
import com.example.reader.utils.UserPreferences

/**
 * Global state holder for the "Remember Me" checkbox functionality.
 * This persists across login and signup screens and saves to SharedPreferences.
 */
object RememberMeBoxState {
    var rememberMe by mutableStateOf(false)

    /**
     * Initialize state from saved preferences
     */
    fun loadFromPreferences(userPrefs: UserPreferences) {
        rememberMe = userPrefs.getRememberMe()
    }

    /**
     * Save current state to preferences
     */
    fun saveToPreferences(userPrefs: UserPreferences) {
        userPrefs.setRememberMe(rememberMe)
    }
}

/**
 * Constants for the login screen components
 */
private object LoginComponentConstants {
    const val CREATE_ACCOUNT_TAG = "CREATE_ACCOUNT_TAG"
    const val CREATE_ACCOUNT_ANNOTATION = "create_account"
    const val CHECKBOX_SIZE = 22
    const val CHECKBOX_ICON_SIZE = 16
    const val CHECKBOX_CORNER_RADIUS = 4
    const val CHECKBOX_BORDER_WIDTH = 1
}

/**
 * Clickable text that prompts users to create a new account
 *
 * @param navController Navigation controller for screen transitions
 * @param isCompact Whether the screen is in compact mode (affects text size)
 */
@Composable
fun CreateAccountPrompt(
    navController: NavController,
    isCompact: Boolean
) {
    val annotatedString = buildAnnotatedString {
        // Regular text
        append("New reader? ")

        // Clickable "Create account" text
        pushStringAnnotation(
            tag = LoginComponentConstants.CREATE_ACCOUNT_TAG,
            annotation = LoginComponentConstants.CREATE_ACCOUNT_ANNOTATION
        )
        withStyle(
            SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Create account")
        }
        pop()
    }

    ClickableText(
        text = annotatedString,
        style = (if (isCompact)
            MaterialTheme.typography.bodySmall
        else
            MaterialTheme.typography.bodyMedium
                ).copy(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
            ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(
                LoginComponentConstants.CREATE_ACCOUNT_TAG,
                offset,
                offset
            ).firstOrNull()?.let {
                navController.navigate(ReaderScreens.CreateAccountScreen.name) {
                    popUpTo(ReaderScreens.LoginScreen.name) { inclusive = true }
                }
            }
        }
    )
}

/**
 * Forgot password link that navigates to password recovery
 *
 * @param navController Navigation controller for screen transitions
 * @param isCompact Whether the screen is in compact height mode
 * @param isVeryNarrow Whether the screen is very narrow
 * @param inRow Whether this component is being used inside a Row layout
 */
@Composable
fun ForgotPasswordLink(
    navController: NavController,
    isCompact: Boolean,
    isVeryNarrow: Boolean,
    inRow: Boolean = false
) {
    val linkContent: @Composable () -> Unit = {
        TextButton(
            onClick = {
                // Navigate to create account screen as placeholder
                // TODO: Replace with actual password recovery screen when available
                navController.navigate(ReaderScreens.CreateAccountScreen.name)
            }
        ) {
            Text(
                text = "Forgot Password?",
                style = when {
                    isVeryNarrow -> MaterialTheme.typography.bodySmall
                    isCompact -> MaterialTheme.typography.bodySmall
                    else -> MaterialTheme.typography.bodyMedium
                },
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (inRow) {
        linkContent()
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            linkContent()
        }
    }
}

/**
 * Footer section containing forgot password and create account links
 * Arranged horizontally with space between them
 *
 * @param navController Navigation controller for screen transitions
 * @param isCompact Whether the screen is in compact height mode
 * @param isVeryNarrow Whether the screen is very narrow
 */
@Composable
fun AuthFooterLinks(
    navController: NavController,
    isCompact: Boolean,
    isVeryNarrow: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ForgotPasswordLink(
            navController = navController,
            isCompact = isCompact,
            isVeryNarrow = isVeryNarrow,
            inRow = true
        )

        CreateAccountPrompt(
            navController = navController,
            isCompact = isCompact
        )
    }
}

/**
 * Custom checkbox component for "Remember Me" functionality
 * Uses Material 3 design system colors and shows a check icon when selected
 * Now with persistent storage support!
 */
@Composable
fun RememberMeBox() {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    // Load saved state on first composition
    LaunchedEffect(Unit) {
        RememberMeBoxState.loadFromPreferences(userPrefs)
    }

    Surface(
        modifier = Modifier
            .size(LoginComponentConstants.CHECKBOX_SIZE.dp)
            .clickable {
                RememberMeBoxState.rememberMe = !RememberMeBoxState.rememberMe
                RememberMeBoxState.saveToPreferences(userPrefs)
            },
        shape = RoundedCornerShape(LoginComponentConstants.CHECKBOX_CORNER_RADIUS.dp),
        color = if (RememberMeBoxState.rememberMe)
            MaterialTheme.colorScheme.primaryContainer
        else
            Color.Transparent,
        border = BorderStroke(
            width = LoginComponentConstants.CHECKBOX_BORDER_WIDTH.dp,
            color = if (RememberMeBoxState.rememberMe)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (RememberMeBoxState.rememberMe) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Checked",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(LoginComponentConstants.CHECKBOX_ICON_SIZE.dp)
                )
            }
        }
    }
}