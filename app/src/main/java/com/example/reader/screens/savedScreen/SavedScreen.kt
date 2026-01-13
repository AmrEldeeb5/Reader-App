package com.example.reader.screens.savedScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reader.navigation.ReaderScreens
import com.example.reader.screens.home.BookCard
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reader.screens.savedScreen.FavoritesViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.reader.components.EmptyFavoritesView
import com.example.reader.components.PullToRefreshContainer
import com.example.reader.domain.model.ReadingStatus

enum class SortOption {
    RECENTLY_ADDED,
    TITLE_ASC,
    TITLE_DESC,
    AUTHOR_ASC,
    RATING_DESC
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    navController: NavController,
    isDarkTheme: Boolean = false,
    onThemeToggle: (Boolean) -> Unit = {},
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
) {
    val favoriteBooks by favoritesViewModel.favoriteBooks.collectAsStateWithLifecycle()
    val isRefreshing by favoritesViewModel.isRefreshing.collectAsStateWithLifecycle()
    val haptic = com.example.reader.utils.rememberHapticFeedback()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    var sortOption by remember { mutableStateOf(SortOption.RECENTLY_ADDED) }
    var showSortMenu by remember { mutableStateOf(false) }

    // Filter by tab and search, then sort
    val currentTabStatus = when (selectedTab) {
        0 -> null // All
        1 -> ReadingStatus.READING
        2 -> ReadingStatus.FINISHED
        else -> null
    }

    val filteredBooks = remember(favoriteBooks, searchQuery, selectedTab, sortOption) {
        var books = favoriteBooks

        // Filter by tab
        if (currentTabStatus != null) {
            books = books.filter { it.readingStatus == currentTabStatus }
        }

        // Filter by search
        if (searchQuery.isNotBlank()) {
            books = books.filter { favorite ->
                favorite.book.title.contains(searchQuery, ignoreCase = true) ||
                favorite.book.author.contains(searchQuery, ignoreCase = true)
            }
        }

        // Sort
        when (sortOption) {
            SortOption.RECENTLY_ADDED -> books.sortedByDescending { it.addedTimestamp }
            SortOption.TITLE_ASC -> books.sortedBy { it.book.title.lowercase() }
            SortOption.TITLE_DESC -> books.sortedByDescending { it.book.title.lowercase() }
            SortOption.AUTHOR_ASC -> books.sortedBy { it.book.author.lowercase() }
            SortOption.RATING_DESC -> books.sortedByDescending { it.userRating ?: it.book.rating }
        }
    }

    PullToRefreshContainer(
        isRefreshing = isRefreshing,
        onRefresh = { favoritesViewModel.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Title with Sort button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Saved Books",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Sort button
            if (favoriteBooks.isNotEmpty()) {
                Box {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Sort options"
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Recently Added") },
                            onClick = {
                                sortOption = SortOption.RECENTLY_ADDED
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (sortOption == SortOption.RECENTLY_ADDED) {
                                    Text("✓", fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Title (A-Z)") },
                            onClick = {
                                sortOption = SortOption.TITLE_ASC
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (sortOption == SortOption.TITLE_ASC) {
                                    Text("✓", fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Title (Z-A)") },
                            onClick = {
                                sortOption = SortOption.TITLE_DESC
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (sortOption == SortOption.TITLE_DESC) {
                                    Text("✓", fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Author (A-Z)") },
                            onClick = {
                                sortOption = SortOption.AUTHOR_ASC
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (sortOption == SortOption.AUTHOR_ASC) {
                                    Text("✓", fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Highest Rated") },
                            onClick = {
                                sortOption = SortOption.RATING_DESC
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (sortOption == SortOption.RATING_DESC) {
                                    Text("✓", fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Tabs
        if (favoriteBooks.isNotEmpty()) {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "All (${favoriteBooks.size})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Reading (${favoriteBooks.count { it.readingStatus == ReadingStatus.READING }})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            "Finished (${favoriteBooks.count { it.readingStatus == ReadingStatus.FINISHED }})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }
        }

        // Search bar (only show if there are books)
        if (favoriteBooks.isNotEmpty()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = {
                    Text(
                        "Search your favorites...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
        }

        // Content
        when {
            favoriteBooks.isEmpty() -> {
                // No books at all
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyFavoritesView(
                        onExploreClick = {
                            navController.navigate(ReaderScreens.ExploreScreen.name)
                        }
                    )
                }
            }
            filteredBooks.isEmpty() && searchQuery.isNotEmpty() -> {
                // No search results
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
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No favorites found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No books match \"$searchQuery\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            filteredBooks.isEmpty() && currentTabStatus != null -> {
                // Empty tab-specific state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = when (currentTabStatus) {
                                ReadingStatus.READING -> "📖"
                                ReadingStatus.FINISHED -> "✅"
                                else -> "📚"
                            },
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = when (currentTabStatus) {
                                ReadingStatus.READING -> "No books in Reading"
                                ReadingStatus.FINISHED -> "No finished books yet"
                                else -> "No books"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when (currentTabStatus) {
                                ReadingStatus.READING -> "Start reading a book from your favorites"
                                ReadingStatus.FINISHED -> "Mark books as finished when you complete them"
                                else -> "Add some books to your favorites"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredBooks, key = { it.bookId }) { favorite ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                    haptic.warning() // Haptic feedback for delete
                                    favoritesViewModel.removeFavorite(favorite.bookId)
                                    true
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color by animateColorAsState(
                                    when (dismissState.targetValue) {
                                        SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.8f)
                                        else -> Color.Transparent
                                    },
                                    label = "background_color"
                                )
                                val scale by animateFloatAsState(
                                    if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.3f else 0.8f,
                                    label = "icon_scale"
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        modifier = Modifier.scale(scale),
                                        tint = Color.White
                                    )
                                }
                            },
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = true
                        ) {
                            BookCard(
                                book = favorite.book,
                                isFavorite = true,
                                isDarkTheme = isDarkTheme,
                                onFavoriteToggle = {
                                    favoritesViewModel.removeFavorite(favorite.bookId)
                                },
                                onRatingChange = { rating ->
                                    favoritesViewModel.updateUserRating(favorite.bookId, rating)
                                },
                                onBookClick = {
                                    favoritesViewModel.setCurrentBook(favorite.book)
                                    navController.navigate(ReaderScreens.DetailScreen.name + "/${favorite.book.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    } // End PullToRefreshContainer
}