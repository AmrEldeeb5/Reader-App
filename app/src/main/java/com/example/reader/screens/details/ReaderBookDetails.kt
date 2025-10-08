package com.example.reader.screens.details

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reader.R
import com.example.reader.data.model.Book
import com.example.reader.screens.saved.FavoritesViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    navController: NavController,
    bookId: Int?,
    favoritesViewModel: FavoritesViewModel = koinInject(),
) {
    val favoriteBooks by favoritesViewModel.favoriteBooks.collectAsState()
    val currentBook by favoritesViewModel.currentBook.collectAsState()

    // Resolve the book to display dynamically (removed hardcoded placeholder)
    val resolvedBook: Book? = remember(bookId, currentBook, favoriteBooks) {
        when {
            currentBook != null && currentBook?.id == bookId -> currentBook
            bookId == null -> null
            else -> (favoriteBooks.firstOrNull { it.id == bookId })
        }
    }

    if (resolvedBook == null) {
        MissingBookContent(onBack = { navController.popBackStack() })
        return
    }

    val isFavorite = favoritesViewModel.isFavorite(resolvedBook.id)

    BookDetailsBottomSheet(
        book = resolvedBook,
        isFavorite = isFavorite,
        onFavoriteToggle = { favoritesViewModel.toggleFavorite(resolvedBook) },
        onNavigateBack = { navController.popBackStack() }
    )
}

@Composable
private fun MissingBookContent(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Book not found",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = onBack) { Text("Go Back") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsBottomSheet(
    book: Book,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val sheetState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetPeekHeight = 250.dp,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (book.subtitle.isNotBlank()) {
                    Text(
                        text = book.subtitle,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                val description = book.description?.takeIf { it.isNotBlank() }
                Text(
                    text = description ?: "Description not available.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(
                    onClick = { /* TODO: Implement read/start action */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6392))
                ) { Text("Start Reading") }
            }
        },
        topBar = {
            BookDetailsHeader(
                book = book,
                isFavorite = isFavorite,
                onFavoriteToggle = onFavoriteToggle,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = book.title, style = MaterialTheme.typography.titleLarge)
            Text(text = book.author, style = MaterialTheme.typography.titleMedium)
            if (book.subtitle.isNotBlank()) {
                Text(text = book.subtitle, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    // Automatically expand sheet when screen loads
    LaunchedEffect(Unit) {
        coroutineScope.launch { sheetState.bottomSheetState.expand() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsHeader(
    book: Book,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = { Text(book.title, maxLines = 1) },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.solar__alt_arrow_left_line_duotone),
                contentDescription = "Back",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { onNavigateBack() }
            )
        },
        actions = {
            val tint by animateColorAsState(
                targetValue = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface,
                animationSpec = tween(300),
                label = "favorite_color"
            )
            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = tint
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenPreview() {
    var isFavorite by remember { mutableStateOf(false) }
    val sample = Book(
        id = 99,
        title = "Preview Book",
        author = "Preview Author",
        subtitle = "Preview Subtitle",
        rating = 3.8,
        coverImageUrl = null,
        description = "This is a sample description used only for preview purposes."
    )
    BookDetailsBottomSheet(
        book = sample,
        isFavorite = isFavorite,
        onFavoriteToggle = { isFavorite = !isFavorite },
        onNavigateBack = { /* no-op */ }
    )
}
