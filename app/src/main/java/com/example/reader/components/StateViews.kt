package com.example.reader.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.reader.ui.theme.Spacing

/**
 * Standard error view for displaying error states.
 *
 * @param error Error message to display
 * @param onRetry Optional retry callback. If provided, shows a retry button
 * @param modifier Modifier for customization
 */
@Composable
fun ErrorView(
    error: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(Spacing.lg))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text("Retry")
            }
        }
    }
}

/**
 * Standard empty view for displaying empty states.
 *
 * @param message Message to display
 * @param actionText Optional action button text
 * @param onAction Optional action callback
 * @param icon Optional icon to display
 * @param modifier Modifier for customization
 */
@Composable
fun EmptyView(
    message: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            icon()
        } else {
            // Default empty box icon
            Icon(
                imageVector = Icons.Default.Error, // Replace with empty box icon
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
        Spacer(modifier = Modifier.height(Spacing.lg))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(Spacing.lg))
            TextButton(onClick = onAction) {
                Text(actionText)
            }
        }
    }
}

/**
 * Standard loading view with circular progress indicator.
 *
 * @param message Optional loading message
 * @param modifier Modifier for customization
 */
@Composable
fun LoadingView(
    message: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
        if (message != null) {
            Spacer(modifier = Modifier.height(Spacing.md))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * UI state handler that displays different views based on state.
 *
 * @param T Data type
 * @param state Current UI state
 * @param onLoading Composable to show when loading
 * @param onError Composable to show on error
 * @param onEmpty Composable to show when data is empty
 * @param onSuccess Composable to show with data
 */
@Composable
fun <T> UiStateHandler(
    state: UiState<T>,
    onLoading: @Composable () -> Unit = { LoadingView() },
    onError: @Composable (String) -> Unit = { ErrorView(it) },
    onEmpty: @Composable () -> Unit = { EmptyView("No data available") },
    onSuccess: @Composable (T) -> Unit
) {
    when (state) {
        is UiState.Loading -> onLoading()
        is UiState.Error -> onError(state.message)
        is UiState.Empty -> onEmpty()
        is UiState.Success -> onSuccess(state.data)
    }
}

/**
 * Sealed class representing different UI states.
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}

