package com.example.reader.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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

/**
 * Enhanced empty state for favorites/saved books screen.
 * Shows illustration, helpful message, and call-to-action button.
 */
@Composable
fun EmptyFavoritesView(
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Illustration/Icon
        Surface(
            modifier = Modifier.size(120.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(id = com.example.reader.R.drawable.streamline_stickies_color__book_library),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = androidx.compose.ui.graphics.Color.Unspecified
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        // Title
        Text(
            text = "Your library is empty",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Description
        Text(
            text = "Start exploring and save your favorite books to read them anytime",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.md)
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))

        // Call-to-action button
        Button(
            onClick = onExploreClick,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh, // Use a search/explore icon if available
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = "Explore Books",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

/**
 * Enhanced empty state for search results.
 */
@Composable
fun EmptySearchView(
    searchQuery: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error, // Use search icon if available
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            text = "No results found",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        Text(
            text = "We couldn't find any books matching \"$searchQuery\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.md)
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        Text(
            text = "Try different keywords or browse categories",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        OutlinedButton(
            onClick = onClearSearch,
            modifier = Modifier.height(40.dp)
        ) {
            Text("Clear Search")
        }
    }
}

/**
 * Enhanced empty state for general no content scenarios.
 */
@Composable
fun EmptyContentView(
    title: String,
    description: String,
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
        // Icon/Illustration
        if (icon != null) {
            icon()
        } else {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Description
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.md)
        )

        // Action button
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(Spacing.xxl))
            Button(
                onClick = onAction,
                modifier = Modifier.height(48.dp)
            ) {
                Text(actionText)
            }
        }
    }
}
