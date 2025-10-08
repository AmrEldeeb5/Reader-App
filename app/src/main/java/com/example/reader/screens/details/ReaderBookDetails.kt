package com.example.reader.screens.details

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reader.R
import com.example.reader.data.model.Book
import com.example.reader.screens.saved.FavoritesViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.core.graphics.ColorUtils
import androidx.compose.ui.graphics.toArgb

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    navController: NavController,
    bookId: Int?,
    favoritesViewModel: FavoritesViewModel = koinInject(),
    detailsViewModel: BookDetailsViewModel = koinViewModel()
) {
    val favoriteBooks by favoritesViewModel.favoriteBooks.collectAsState()
    val currentBook by favoritesViewModel.currentBook.collectAsState()

    val resolvedBook: Book? = remember(bookId, currentBook, favoriteBooks) {
        when {
            currentBook != null && currentBook?.id == bookId -> currentBook
            bookId == null -> null
            else -> favoriteBooks.firstOrNull { it.id == bookId }
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
    sheetPeekHeight: Dp = 300.dp,
    dynamicTheming: Boolean = true,
    translucentSheet: Boolean = true,
    detailsViewModel: BookDetailsViewModel? = null,
    autoExpand: Boolean = false // new flag; default false so sheet stays at peek height
) {
    val sheetState = rememberBottomSheetScaffoldState()
    val context = LocalContext.current

    // Trigger palette load once only if we have a view model (not in preview)
    LaunchedEffect(book.coverImageUrl, dynamicTheming, detailsViewModel) {
        if (dynamicTheming && detailsViewModel != null) detailsViewModel.loadPalette(context, book.id, book.coverImageUrl, translucentSheet)
    }
    val paletteColor by (detailsViewModel?.paletteColor?.collectAsState() ?: remember { mutableStateOf<Color?>(null) })

    val baseSheetColor = MaterialTheme.colorScheme.surface
    val sheetColorTarget = paletteColor ?: if (translucentSheet) baseSheetColor.copy(alpha = 0.92f) else baseSheetColor

    // Animated sheet color transition
    val sheetColor by animateColorAsState(
        targetValue = sheetColorTarget,
        animationSpec = tween(450),
        label = "sheet_color_anim"
    )

    // Hoist primary color (accessing MaterialTheme outside non-composable remember lambda)
    val primaryColor = MaterialTheme.colorScheme.primary

    // Derive a distinct but related button color from paletteColor (if present)
    val buttonColorTarget: Color = remember(paletteColor, sheetColorTarget, primaryColor) {
        val base = paletteColor ?: primaryColor
        if (paletteColor == null) return@remember base
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(base.toArgb(), hsl)
        val sheetLuminance = ColorUtils.calculateLuminance(sheetColorTarget.toArgb())
        hsl[2] = if (sheetLuminance > 0.5) {
            (hsl[2] - 0.28f).coerceAtLeast(0.12f)
        } else {
            (hsl[2] + 0.28f).coerceAtMost(0.90f)
        }
        hsl[1] = (hsl[1] * 1.15f).coerceIn(0f, 1f)
        var candidate = Color(ColorUtils.HSLToColor(hsl))
        val contrastLumaDiff = kotlin.math.abs(candidate.luminance() - sheetColorTarget.luminance())
        if (contrastLumaDiff < 0.18f) {
            ColorUtils.colorToHSL(base.toArgb(), hsl)
            val sheetLum = sheetLuminance
            hsl[2] = if (sheetLum > 0.5) {
                (hsl[2] - 0.40f).coerceAtLeast(0.08f)
            } else {
                (hsl[2] + 0.40f).coerceAtMost(0.95f)
            }
            candidate = Color(ColorUtils.HSLToColor(hsl))
        }
        candidate
    }
    val buttonColor by animateColorAsState(
        targetValue = buttonColorTarget,
        animationSpec = tween(450),
        label = "button_color_anim"
    )
    val onButtonColor = if (buttonColor.luminance() > 0.5f) Color.Black else Color.White

    Box(Modifier.fillMaxSize()) {
        BottomSheetScaffold(
            scaffoldState = sheetState,
            sheetPeekHeight = sheetPeekHeight,
            sheetContainerColor = sheetColor,
            sheetShadowElevation = 8.dp,
            sheetDragHandle = { BottomSheetDefaults.DragHandle() },
            topBar = {
                BookDetailsHeader(
                    book = book,
                    isFavorite = isFavorite,
                    onFavoriteToggle = onFavoriteToggle,
                    onNavigateBack = onNavigateBack
                )
            },
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(sheetColor)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = book.author,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (book.subtitle.isNotBlank()) {
                        Text(
                            text = book.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    HorizontalDivider()
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium
                    )
                    val description = book.description?.takeIf { it.isNotBlank() }
                    Text(
                        text = description ?: "Description not available.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(120.dp)) // space for fixed button
                }
            }
        ) { padding ->
            // Replaced Spacer with centered cover image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (!book.coverImageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = book.coverImageUrl,
                            contentDescription = book.title,
                            modifier = Modifier
                                .size(220.dp)
                                .clip(MaterialTheme.shapes.medium)
                        )
                    } else {
                        Text(
                            text = book.title.take(1),
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Button(
            onClick = onStartReading,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
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

    // Remove previous unconditional expand; only expand if explicitly enabled
    if (autoExpand) {
        LaunchedEffect(autoExpand) {
            sheetState.bottomSheetState.expand()
        }
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
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
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
        onNavigateBack = { /* no-op */ },
        dynamicTheming = false, // skip dynamic palette in preview (no VM)
        translucentSheet = false,
        detailsViewModel = null,
        autoExpand = false
    )
}
