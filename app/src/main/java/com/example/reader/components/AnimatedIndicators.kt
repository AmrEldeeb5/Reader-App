package com.example.reader.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay

/**
 * Animated success dialog for successful operations.
 * Shows a checkmark animation with a message.
 *
 * @param visible Whether the dialog is visible
 * @param message Success message to display
 * @param onDismiss Callback when dialog is dismissed
 * @param autoDismissDelay Auto-dismiss after this many milliseconds
 */
@Composable
fun SuccessDialog(
    visible: Boolean,
    message: String,
    onDismiss: () -> Unit,
    autoDismissDelay: Long = 2000
) {
    if (visible) {
        LaunchedEffect(Unit) {
            delay(autoDismissDelay)
            onDismiss()
        }

        Dialog(onDismissRequest = onDismiss) {
            AnimatedResultCard(
                icon = Icons.Default.Check,
                message = message,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                iconTint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Animated error dialog for failed operations.
 * Shows an error icon with a message.
 *
 * @param visible Whether the dialog is visible
 * @param message Error message to display
 * @param onDismiss Callback when dialog is dismissed
 * @param autoDismissDelay Auto-dismiss after this many milliseconds (0 = no auto-dismiss)
 */
@Composable
fun ErrorDialog(
    visible: Boolean,
    message: String,
    onDismiss: () -> Unit,
    autoDismissDelay: Long = 0
) {
    if (visible) {
        if (autoDismissDelay > 0) {
            LaunchedEffect(Unit) {
                delay(autoDismissDelay)
                onDismiss()
            }
        }

        Dialog(onDismissRequest = onDismiss) {
            AnimatedResultCard(
                icon = Icons.Default.Error,
                message = message,
                backgroundColor = MaterialTheme.colorScheme.errorContainer,
                iconTint = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * Internal composable for animated result card.
 */
@Composable
private fun AnimatedResultCard(
    icon: ImageVector,
    message: String,
    backgroundColor: Color,
    iconTint: Color
) {
    // Scale animation for the card
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    // Icon rotation animation
    var iconVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        iconVisible = true
    }

    val iconScale by animateFloatAsState(
        targetValue = if (iconVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_scale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large
            )
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Animated icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(iconScale)
                    .background(
                        color = backgroundColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(48.dp)
                )
            }

            // Message text with fade-in animation
            AnimatedVisibility(
                visible = iconVisible,
                enter = fadeIn(animationSpec = tween(400)) +
                        expandVertically(animationSpec = tween(400))
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * Inline success indicator with animated checkmark.
 * Can be used directly in forms or screens without a dialog.
 *
 * @param visible Whether to show the indicator
 * @param message Success message
 * @param modifier Modifier for customization
 */
@Composable
fun InlineSuccessIndicator(
    visible: Boolean,
    message: String,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Animated favorite heart icon.
 * Shows a scale and color animation when toggled.
 *
 * @param isFavorite Whether the item is favorited
 * @param modifier Modifier for customization
 */
@Composable
fun AnimatedFavoriteIcon(
    isFavorite: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "favorite_scale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isFavorite)
            Color.Red
        else
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        animationSpec = tween(200),
        label = "favorite_color"
    )

    Icon(
        imageVector = Icons.Default.Favorite,
        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
        tint = iconColor,
        modifier = modifier.scale(scale)
    )
}

