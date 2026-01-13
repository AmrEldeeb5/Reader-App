package com.example.reader.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Animated success checkmark that appears with scale and fade animation.
 * Perfect for login, signup, or any successful action confirmation.
 */
@Composable
fun AnimatedSuccessCheckmark(
    visible: Boolean,
    modifier: Modifier = Modifier,
    onAnimationEnd: () -> Unit = {}
) {
    var animationFinished by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        if (visible) {
            delay(1500) // Show for 1.5 seconds
            animationFinished = true
            onAnimationEnd()
        }
    }

    AnimatedVisibility(
        visible = visible && !animationFinished,
        enter = scaleIn(
            initialScale = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Animated favorite heart with bounce effect.
 * Shows a pulsing heart animation when favoriting a book.
 */
@Composable
fun AnimatedFavoriteHeart(
    isFavorite: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "favorite_scale"
    )

    val color by animateColorAsState(
        targetValue = if (isFavorite) {
            Color(0xFFE91E63) // Pink/Red for favorite
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 300),
        label = "favorite_color"
    )

    Icon(
        imageVector = Icons.Default.Favorite,
        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
        modifier = modifier.scale(scale),
        tint = color
    )
}

/**
 * Success toast/snackbar with slide-in animation.
 * Shows a temporary success message that slides in from top.
 */
@Composable
fun AnimatedSuccessToast(
    visible: Boolean,
    message: String,
    icon: ImageVector = Icons.Default.Check,
    durationMillis: Long = 2000,
    onDismiss: () -> Unit = {}
) {
    LaunchedEffect(visible) {
        if (visible) {
            delay(durationMillis)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it }
        ) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * Loading to success transition animation.
 * Shows a smooth morphing animation from loading to success state.
 */
@Composable
fun LoadingToSuccessAnimation(
    isLoading: Boolean,
    isSuccess: Boolean,
    loadingText: String = "Loading...",
    successText: String = "Success!",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = when {
                isSuccess -> "success"
                isLoading -> "loading"
                else -> "idle"
            },
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            },
            label = "loading_success_transition"
        ) { state ->
            when (state) {
                "loading" -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = loadingText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                "success" -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AnimatedSuccessCheckmark(
                            visible = true,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = successText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                else -> {
                    // Idle state - empty
                    Spacer(modifier = Modifier.size(0.dp))
                }
            }
        }
    }
}

/**
 * Pulsing animation for important elements.
 * Creates a subtle pulsing effect to draw attention.
 */
@Composable
fun rememberPulsingAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    return infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    ).value
}

/**
 * Shimmer effect for loading states.
 * Creates a shimmering animation across the content.
 */
@Composable
fun rememberShimmerAnimation(): State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    return infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )
}

/**
 * Bouncing animation for interactive elements.
 * Creates a bouncing effect when pressed.
 */
@Composable
fun rememberBounceAnimation(isPressed: Boolean): Float {
    return animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounce_scale"
    ).value
}

