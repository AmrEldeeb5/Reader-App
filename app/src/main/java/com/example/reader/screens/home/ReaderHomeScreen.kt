package com.example.reader.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.reader.R
import com.example.reader.data.LastSelectedCoverStore
import com.example.reader.data.model.Book
import com.example.reader.navigation.ReaderScreens
import com.example.reader.screens.saved.FavoritesViewModel
import com.example.reader.ui.theme.CardBackground
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun Home(
    navController: NavController,
    isDarkTheme: Boolean = false,
    onThemeToggle: (Boolean) -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
    favoritesViewModel: FavoritesViewModel = koinInject() // Inject singleton
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val booksState by viewModel.booksState.collectAsState()
    val favoriteBooks by favoritesViewModel.favoriteBooks.collectAsState()

    // Create a set of favorite IDs for quick lookup
    val favoriteIds = remember(favoriteBooks) {
        favoriteBooks.map { it.id }.toSet()
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            BookDiscoveryScreen(isDarkTheme = isDarkTheme)

            CategoryTabs(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    viewModel.selectCategory(category)
                },
                modifier = Modifier.padding(top = 1.dp),
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(16.dp))

            BookGridSection(
                isDarkTheme = isDarkTheme,
                booksState = booksState,
                favoriteIds = favoriteIds,
                onFavoriteToggle = { book ->
                    favoritesViewModel.toggleFavorite(book)
                },
                onRatingChange = { bookId, rating ->
                    viewModel.updateUserRating(bookId, rating)
                    favoritesViewModel.updateUserRating(bookId, rating)
                },
                navController = navController,
                onBookOpen = { book ->
                    favoritesViewModel.setCurrentBook(book)
                    // Update last-selected cover and text snippet for Discovery
                    val snippet = book.description?.takeIf { it.isNotBlank() }
                        ?: book.subtitle.takeIf { it.isNotBlank() }
                        ?: book.title
                    LastSelectedCoverStore.set(book.coverImageUrl, snippet)
                    navController.navigate(ReaderScreens.DetailScreen.name + "/${book.id}")
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    userName: String? = null,
    onNotificationsClick: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val initialName = remember(userName, auth.currentUser) {
        userName ?: auth.currentUser?.displayName?.takeIf { it.isNotBlank() }
        ?: "Andy"
    }
    var resolvedName by remember { mutableStateOf(initialName) }

    LaunchedEffect(initialName) {
        val currentUser = auth.currentUser
        if (currentUser == null) return@LaunchedEffect
        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.uid)
                .get()
                .await()
            if (snapshot.exists()) {
                val fromDoc = snapshot.getString("displayName")
                    ?: snapshot.getString("username")
                    ?: snapshot.getString("name")
                if (!fromDoc.isNullOrBlank() && fromDoc != resolvedName) {
                    resolvedName = fromDoc
                }
            }
        } catch (_: Exception) {
            // Ignore failures and keep the immediate fallback
        }
    }

    TopAppBar(
        title = {
            Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                GreetingSection(resolvedName)
            }
        },
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.streamline_kameleon_color__eyeglasses),
                contentDescription = "User avatar",
                modifier = Modifier
                    .clickable(onClick = {
                        navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                    })
                    .size(48.dp)
                    .clip(CircleShape)
            )
        },
        actions = {
            IconButton(onClick = onNotificationsClick) {
                Image(
                    painter = painterResource(id = R.drawable.solar__bell_bing_bold),
                    contentDescription = "Notifications",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.size(28.dp)
                )
            }
            FunThemeToggleCompact(
                isDark = isDarkTheme,
                onToggle = onThemeToggle
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
    )
}

@Composable
fun BookGridSection(
    isDarkTheme: Boolean,
    booksState: CategoryBooksState,
    favoriteIds: Set<Int>,
    onFavoriteToggle: (Book) -> Unit,
    onRatingChange: (Int, Double) -> Unit,
    navController: NavController,
    onBookOpen: (Book) -> Unit
) {
    when {
        booksState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        booksState.error != null -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = booksState.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        booksState.books.isEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No books found in this category",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        else -> {
            val midPoint = booksState.books.size / 2
            val firstRowBooks = booksState.books.take(midPoint)
            val secondRowBooks = booksState.books.drop(midPoint)

            // First row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(firstRowBooks) { book ->
                    BookCard(
                        book = book.copy(isFavorite = favoriteIds.contains(book.id)),
                        isDarkTheme = isDarkTheme,
                        onFavoriteToggle = { onFavoriteToggle(book) },
                        onRatingChange = { rating -> onRatingChange(book.id, rating) },
                        onBookClick = { onBookOpen(book) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clip(RoundedCornerShape(12.dp))
            ){
                Text(
                    text = "More books",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Second row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(secondRowBooks) { book ->
                    BookCard(
                        book = book.copy(isFavorite = favoriteIds.contains(book.id)),
                        isDarkTheme = isDarkTheme,
                        onFavoriteToggle = { onFavoriteToggle(book) },
                        onRatingChange = { rating -> onRatingChange(book.id, rating) },
                        onBookClick = { onBookOpen(book) }
                    )
                }
            }
        }
    }
}
@Composable
fun BookCard(
    book: Book,
    onFavoriteToggle: () -> Unit,
    onRatingChange: (Double) -> Unit = {},
    isDarkTheme: Boolean,
    onBookClick: () -> Unit = {}
) {
    var showRatingDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .width(150.dp)
            .height(290.dp)
            .clickable { onBookClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) {
                CardBackground
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkTheme) 4.dp else 1.dp
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(185.dp)
            ) {
                AsyncImage(
                    model = book.coverImageUrl,
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Top
                ) {
                    IconButton(onClick = onFavoriteToggle) {
                        val tint by animateColorAsState(
                            targetValue = if (book.isFavorite) Color.Red else Color.White,
                            animationSpec = tween(300),
                            label = "favorite_color"
                        )
                        Icon(
                            painter = painterResource(R.drawable.solar__heart_angle_bold),
                            contentDescription = "Favorite",
                            tint = tint,
                            modifier = Modifier.size(28.dp)
                                .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape).padding(4.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = book.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = book.author,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showRatingDialog = true },
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = if (book.userRating != null) Color(0xFFFFB300) else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = book.userRating?.toString() ?: "Rate",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // Rating Dialog
    if (showRatingDialog) {
        RatingDialog(
            currentRating = book.userRating ?: 0.0,
            onDismiss = { showRatingDialog = false },
            onRatingSelected = { rating ->
                onRatingChange(rating)
                showRatingDialog = false
            }
        )
    }
}

@Composable
fun RatingDialog(
    currentRating: Double,
    onDismiss: () -> Unit,
    onRatingSelected: (Double) -> Unit
) {
    var selectedRating by remember { mutableStateOf(currentRating) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Rate this book",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedRating.toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Star rating selector
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star $star",
                            tint = if (star <= selectedRating) Color(0xFFFFB300) else Color.Gray,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { selectedRating = star.toDouble() }
                                .padding(4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onRatingSelected(selectedRating) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
