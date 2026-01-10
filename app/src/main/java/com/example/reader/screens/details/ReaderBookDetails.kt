package com.example.reader.screens.details

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reader.R
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reader.screens.savedScreen.FavoritesViewModel
import com.example.reader.screens.details.BookDetailsViewModel
import com.example.reader.domain.model.Book
import com.example.reader.components.BookDetailsSkeleton
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    navController: NavController,
    bookId: String?,
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    detailsViewModel: BookDetailsViewModel = hiltViewModel()
) {
    val favoriteBooks by favoritesViewModel.favoriteBooks.collectAsStateWithLifecycle()
    val currentBook by favoritesViewModel.currentBook.collectAsStateWithLifecycle()
    val fetchedBook by detailsViewModel.bookDetails.collectAsStateWithLifecycle()
    val isLoading by detailsViewModel.isLoading.collectAsStateWithLifecycle()
    val error by detailsViewModel.error.collectAsStateWithLifecycle()

    // Try to resolve the book from multiple sources
    val resolvedBook: Book? = remember(bookId, currentBook, favoriteBooks, fetchedBook) {
        when {
            currentBook != null && currentBook?.id == bookId -> currentBook
            bookId == null -> null
            fetchedBook != null && fetchedBook?.id == bookId -> fetchedBook
            else -> favoriteBooks.firstOrNull { it.book.id == bookId }?.book
        }
    }

    // Fetch book from repository if not found in memory
    LaunchedEffect(bookId) {
        if (bookId != null && resolvedBook == null && !isLoading) {
            detailsViewModel.fetchBookById(bookId)
        }
    }

    // Show loading state with skeleton
    if (isLoading && resolvedBook == null) {
        BookDetailsSkeleton(modifier = Modifier.fillMaxSize())
        return
    }

    // Show error or missing book
    if (resolvedBook == null) {
        MissingBookContent(
            onBack = { navController.popBackStack() },
            errorMessage = error
        )
        return
    }

    val coroutineScope = rememberCoroutineScope()
    var isFavorite by remember { mutableStateOf(false) }

    // Check favorite status
    LaunchedEffect(resolvedBook.id) {
        isFavorite = favoritesViewModel.isFavorite(resolvedBook.id)
    }

    BookDetailsBottomSheet(
        book = resolvedBook,
        isFavorite = isFavorite,
        onFavoriteToggle = { favoritesViewModel.toggleFavorite(resolvedBook) },
        onNavigateBack = { navController.popBackStack() },
        detailsViewModel = detailsViewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsBottomSheet(
    book: Book,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onNavigateBack: () -> Unit,
    onStartReading: () -> Unit = {},
    sheetPeekHeight: Dp = 350.dp,
    dynamicTheming: Boolean = true,
    translucentSheet: Boolean = true,
    detailsViewModel: BookDetailsViewModel? = null,
    autoExpand: Boolean = false,
    sheetHorizontalMargin: Dp = 24.dp,
    // NEW: control visibility of drag handle; hiding it removes the small pill/outline artifact
    showDragHandle: Boolean = false
) {
    val sheetState = rememberBottomSheetScaffoldState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Simplified: Use Material3 color scheme directly
    val sheetBgColor = MaterialTheme.colorScheme.surface
    val buttonColor = MaterialTheme.colorScheme.primary
    val onButtonColor = MaterialTheme.colorScheme.onPrimary
    val onSheetColor = MaterialTheme.colorScheme.onSurface

    val isExpanded = sheetState.bottomSheetState.currentValue == SheetValue.Expanded
    val coverScale by animateFloatAsState(
        targetValue = if (isExpanded) 0.85f else 1f,
        animationSpec = tween(400),
        label = "cover_scale_anim"
    )

    // Favorite animation
    val favoriteScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = tween(200),
        label = "favorite_scale"
    )

    Box(Modifier.fillMaxSize()) {
        // Blurred background
        if (!book.coverImageUrl.isNullOrBlank()) {
            AsyncImage(
                model = book.coverImageUrl,
                contentDescription = "Blurred book cover background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(22.dp)
                    .graphicsLayer { alpha = 0.55f }
            )
        } else {
            // Fallback background color when no cover
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
        // Gradient overlay for depth & legibility
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.40f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.55f)
                        )
                    )
                )
        )

        // Foreground scaffold & content
        BottomSheetScaffold(
            scaffoldState = sheetState,
            sheetPeekHeight = sheetPeekHeight,
            containerColor = Color.Transparent,
            sheetContainerColor = Color.Transparent,
            sheetTonalElevation = 0.dp,
            sheetShadowElevation = 0.dp,
            sheetShape = RectangleShape,
            sheetDragHandle = if (showDragHandle) { { BottomSheetDefaults.DragHandle() } } else { {} },
            topBar = {
                BookDetailsHeader(
                    isFavorite = isFavorite,
                    onFavoriteToggle = onFavoriteToggle,
                    onNavigateBack = onNavigateBack,
                    book = book
                )
            },
            sheetContent = {
                // Constrain width with horizontal margin and use a Surface to host the content (rounded, elevated, translucent)
                Box(
                    modifier = Modifier
                        .padding(horizontal = sheetHorizontalMargin, vertical = 8.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = sheetBgColor,
                        tonalElevation = 0.dp,
                        shadowElevation = 8.dp,
                        shape = MaterialTheme.shapes.extraLarge,
                        contentColor = onSheetColor
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Text(
                                text = "By ${book.author}",
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = onSheetColor
                            )
                            if (book.subtitle.isNotBlank()) {
                                Text(
                                    text = book.subtitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = onSheetColor
                                )
                            }
                            HorizontalDivider()
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleMedium,
                                color = onSheetColor
                            )
                            val description = book.description?.takeIf { it.isNotBlank() }
                            Text(
                                text = description ?: "Description not available.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = onSheetColor
                            )
                            Spacer(Modifier.height(120.dp)) // space for fixed button overlay
                        }
                    }
                }
            }
        ) { padding ->
            // CONTENT area (no blurred background here anymore)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    if (!book.coverImageUrl.isNullOrBlank()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(260.dp)
                        ) {
                            AsyncImage(
                                model = book.coverImageUrl,
                                contentDescription = book.title,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(MaterialTheme.shapes.medium)
                            )
                        }
                    } else {
                        Text(
                            text = book.title.take(1),
                            style = MaterialTheme.typography.displayLarge,
                            color = buttonColor,
                            modifier = Modifier
                                .graphicsLayer { scaleX = coverScale; scaleY = coverScale }
                                .clickable {
                                    coroutineScope.launch {
                                        if (sheetState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                            sheetState.bottomSheetState.partialExpand()
                                        } else {
                                            sheetState.bottomSheetState.expand()
                                        }
                                    }
                                }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = book.title,
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Button width matches floating sheet width via same horizontal margin
        Button(
            onClick = onStartReading,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = sheetHorizontalMargin, end = sheetHorizontalMargin, bottom = 24.dp)
                .clip(MaterialTheme.shapes.medium)
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = onButtonColor
            )
        ) {
            Text(
                "Start Reading",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
        }
    }

    if (autoExpand) {
        LaunchedEffect(autoExpand) {
            sheetState.bottomSheetState.expand()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsHeader(
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onNavigateBack: () -> Unit,
    book: Book? = null
) {
    val context = LocalContext.current

    TopAppBar(
        title = {},
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
            // Share button
            if (book != null) {
                IconButton(onClick = {
                    val shareText = "Check out this book: ${book.title} by ${book.author}"
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Share book")
                    context.startActivity(shareIntent)
                }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share book",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Favorite button with improved animation
            val tint by animateColorAsState(
                targetValue = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface,
                animationSpec = tween(300),
                label = "favorite_color"
            )
            val scale by animateFloatAsState(
                targetValue = if (isFavorite) 1.15f else 1f,
                animationSpec = tween(200),
                label = "favorite_scale"
            )
            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    painter = painterResource(R.drawable.solar__heart_angle_bold),
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = tint,
                    modifier = Modifier.graphicsLayer(
                        scaleX = scale,
                        scaleY = scale
                    )
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
private fun MissingBookContent(onBack: () -> Unit, errorMessage: String? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMessage ?: "Book not found",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = onBack) { Text("Go Back") }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenPreview() {
    var isFavorite by remember { mutableStateOf(false) }
    val sample = Book(
        id = "preview-99",
        title = "Preview Book",
        author = "Preview Author",
        subtitle = "Preview Subtitle",
        rating = 3.8,
        coverImageUrl = null,
        description = "This is a sample description used only for preview purposes.",
        publishedDate = null
    )
    BookDetailsBottomSheet(
        book = sample,
        isFavorite = isFavorite,
        onFavoriteToggle = { isFavorite = !isFavorite },
        onNavigateBack = { /* no-op */ },
        dynamicTheming = false,
        translucentSheet = true,
        detailsViewModel = null,
        autoExpand = false,
        showDragHandle = false
    )
}