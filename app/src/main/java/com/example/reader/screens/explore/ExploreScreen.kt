package com.example.reader.screens.explore

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.reader.screens.home.RatingDialog
import com.example.reader.screens.home.BookCard

import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reader.screens.explore.ExploreViewModel
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import com.example.reader.screens.home.BookCard
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.reader.components.ErrorView
import com.example.reader.components.EmptyView
import com.example.reader.components.BookCardSkeleton
import com.example.reader.components.OfflineBanner
import com.example.reader.ui.theme.Spacing
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navController: NavController,
    isDarkTheme: Boolean = false,
    onThemeToggle: (Boolean) -> Unit = {},
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val networkManager = remember { com.example.reader.utils.NetworkConnectivityManager(context) }

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Network status
    val isOffline = !networkManager.isNetworkAvailable()

    // State for tracking scroll
    val listState = rememberLazyGridState()
    var isTopBarVisible by remember { mutableStateOf(true) }
    var lastScrollIndex by remember { mutableStateOf(0) }

    // Detect scroll direction
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        val currentIndex = listState.firstVisibleItemIndex
        val currentOffset = listState.firstVisibleItemScrollOffset

        // Show top bar when at the very top
        if (currentIndex == 0 && currentOffset == 0) {
            isTopBarVisible = true
        }
        // Hide when scrolling down
        else if (currentIndex > lastScrollIndex) {
            isTopBarVisible = false
        }
        // Show when scrolling up
        else if (currentIndex < lastScrollIndex) {
            isTopBarVisible = true
        }

        lastScrollIndex = currentIndex
    }

    // Animate the top bar offset
    val topBarOffset by animateDpAsState(
        targetValue = if (isTopBarVisible) 0.dp else (-250).dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "topbar_offset"
    )

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets(0))) {
        // Main content
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top spacing
            Spacer(modifier = Modifier.height(if (isTopBarVisible) 80.dp else 0.dp))

            // Offline indicator
            OfflineBanner(isOffline = isOffline)

            // Content Area
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    // Loading state
                    searchState.isLoading -> {
                        // Show skeleton loading for better perceived performance
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 55.dp,
                                bottom = 16.dp
                            )
                        ) {
                            items(6) {
                                BookCardSkeleton()
                            }
                        }
                    }

                    // Error or No Results state
                    searchState.errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorView(
                                error = searchState.errorMessage!!,
                                onRetry = if (searchQuery.isNotBlank()) {
                                    { viewModel.searchBooks(searchQuery) }
                                } else null,
                                modifier = Modifier.padding(Spacing.xl)
                            )
                        }
                    }

                    // Books found state
                    searchState.books.isNotEmpty() -> {
                        LazyVerticalGrid(
                            state = listState,
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 55.dp,
                                bottom = 16.dp
                            )
                        ) {
                            items(searchState.books, key = { it.id }) { book ->
                                val isFavorite by viewModel.isFavoriteFlow(book.id).collectAsState(initial = false)
                                BookCard(
                                    book = book,
                                    isFavorite = isFavorite,
                                    isDarkTheme = isDarkTheme,
                                    onFavoriteToggle = {
                                        viewModel.toggleFavorite(book)
                                    },
                                    onRatingChange = { rating ->
                                        viewModel.updateUserRating(book.id, rating)
                                    }
                                )
                            }
                        }
                    }

                    // Initial state (no search performed yet) - Show search history
                    !searchState.hasSearched -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                                .padding(top = 70.dp)
                        ) {
                            if (searchHistory.isNotEmpty()) {
                                // Search History Section
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Recent Searches",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    TextButton(onClick = { viewModel.clearSearchHistory() }) {
                                        Text("Clear All", style = MaterialTheme.typography.bodySmall)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Search History List
                                searchHistory.forEach { query ->
                                    ListItem(
                                        headlineContent = { Text(query) },
                                        leadingContent = {
                                            Icon(
                                                imageVector = Icons.Default.History,
                                                contentDescription = "History",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        trailingContent = {
                                            IconButton(onClick = { viewModel.removeFromSearchHistory(query) }) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Remove",
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        },
                                        modifier = Modifier.clickable {
                                            viewModel.searchFromHistory(query)
                                        }
                                    )
                                }
                            } else {
                                // Empty state when no history
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(32.dp)
                                    ) {
                                        Text(
                                            text = "🔍",
                                            fontSize = 64.sp
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "Search for your favorite books",
                                            color = MaterialTheme.colorScheme.onBackground,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Try searching by book title, author, or genre",
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Collapsible Top Section (SearchBar + Button)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = topBarOffset)
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 16.dp)
        ) {

            // Search Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp), // Only bottom padding
                    placeholder = {
                        Text(
                            "Search for books, authors...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearSearch() }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.searchBooks(searchQuery)
                            keyboardController?.hide()
                        }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Search Button
                Button(
                    onClick = {
                        viewModel.searchBooks(searchQuery)
                        keyboardController?.hide()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = searchQuery.isNotEmpty() && !searchState.isLoading
                ) {
                    if (searchState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Search", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}