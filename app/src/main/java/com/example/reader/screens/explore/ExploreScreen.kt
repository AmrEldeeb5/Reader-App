package com.example.reader.screens.explore

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import com.example.reader.screens.home.BookCard
import com.example.reader.screens.saved.FavoritesViewModel
import com.example.reader.navigation.ReaderScreens
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navController: NavController,
    isDarkTheme: Boolean = false,
    onThemeToggle: (Boolean) -> Unit = {},
    viewModel: ExploreViewModel = koinViewModel(),
    favoritesViewModel: FavoritesViewModel = koinInject()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Live search with debounce
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            kotlinx.coroutines.delay(500) // 500ms debounce
            if (searchQuery.isNotEmpty()) { // Check again after delay
                viewModel.searchBooks(searchQuery)
            }
        }
    }

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

    Box(modifier = Modifier.fillMaxSize()
        .systemBarsPadding()) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = if (isTopBarVisible) 80.dp else 0.dp) // Reduced from 250.dp to 80.dp
        ) {
            // Content Area
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    // Loading state
                    searchState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Searching for books...",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    // Error or No Results state
                    searchState.errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Text(
                                    text = "📚",
                                    fontSize = 64.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = searchState.errorMessage!!,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
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
                                top = 16.dp,
                                bottom = 16.dp
                            )
                        ) {
                            items(searchState.books) { book ->
                                BookCard(
                                    book = book,
                                    isDarkTheme = isDarkTheme,
                                    onFavoriteToggle = {
                                        viewModel.toggleFavorite(book.id)
                                    },
                                    onRatingChange = { rating ->
                                        viewModel.updateUserRating(book.id, rating)
                                    },
                                    onBookClick = {
                                        favoritesViewModel.setCurrentBook(book)
                                        navController.navigate(ReaderScreens.DetailScreen.name + "/${book.id}")
                                    }
                                )
                            }
                        }
                    }

                    // Initial state (no search performed yet)
                    !searchState.hasSearched -> {
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
                        val isEnabled = searchQuery.isNotEmpty() && !searchState.isLoading
                        IconButton(
                            onClick = {
                                viewModel.searchBooks(searchQuery)
                                keyboardController?.hide()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                            )
                        }
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


            }
        }
    }
}