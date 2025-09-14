package com.example.reader.screens.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reader.navigation.ReaderScreens

// Shared state holder for login-related simple flags.
object RememberMeBoxState {
    var rememberMe by mutableStateOf(false)

}

@Composable
fun CreateAccountPrompt(navController: NavController, isCompact: Boolean) {
    val tag = "CREATE_ACCOUNT_TAG"
    val annotated = buildAnnotatedString {
        append("New reader? ")
        pushStringAnnotation(tag = tag, annotation = "create_account")
        withStyle(
            SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) { append("Create account") }
        pop()
    }
    ClickableText(
        text = annotated,
        style = (if (isCompact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium)
            .copy(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)),
        onClick = { offset ->
            annotated.getStringAnnotations(tag, offset, offset).firstOrNull()?.let {
                navController.navigate(ReaderScreens.CreateAccountScreen.name) {
                    popUpTo(ReaderScreens.LoginScreen.name) { inclusive = true }
                }
            }
        }
    )
}

@Composable
fun RememberMeBox(
    checked: Boolean = RememberMeBoxState.rememberMe,
    onToggle: () -> Unit = { RememberMeBoxState.rememberMe = !RememberMeBoxState.rememberMe }
) {
    Surface(
        modifier = Modifier
            .size(22.dp)
            .clickable { onToggle() },
        shape = RoundedCornerShape(4.dp),
        color = if (checked) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        border = BorderStroke(
            1.dp,
            if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (checked) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Checked",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
