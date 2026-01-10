package com.example.reader.screens.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reader.navigation.ReaderScreens

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

        // Clickable "Create account" text using new LinkAnnotation API
        withLink(
            LinkAnnotation.Clickable(
                tag = LoginComponentConstants.CREATE_ACCOUNT_TAG,
                linkInteractionListener = {
                    navController.navigate(ReaderScreens.CreateAccountScreen.name) {
                        popUpTo(ReaderScreens.LoginScreen.name) { inclusive = true }
                    }
                }
            )
        ) {
            withStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Create account")
            }
        }
    }

    Text(
        text = annotatedString,
        style = (if (isCompact)
            MaterialTheme.typography.bodySmall
        else
            MaterialTheme.typography.bodyMedium
                ).copy(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
            )
    )
}

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
                navController.navigate(ReaderScreens.ChangePasswordScreen.name)
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


@Composable
fun RememberMeBox(
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(LoginComponentConstants.CHECKBOX_SIZE.dp)
            .clickable { onToggle() },
        shape = RoundedCornerShape(LoginComponentConstants.CHECKBOX_CORNER_RADIUS.dp),
        color = if (isChecked)
            MaterialTheme.colorScheme.primaryContainer
        else
            Color.Transparent,
        border = BorderStroke(
            width = LoginComponentConstants.CHECKBOX_BORDER_WIDTH.dp,
            color = if (isChecked)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (isChecked) {
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