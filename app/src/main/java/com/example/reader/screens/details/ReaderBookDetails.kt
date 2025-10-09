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
import com.example.reader.data.LastSelectedCoverStore
import com.example.reader.data.model.Book
import com.example.reader.screens.saved.FavoritesViewModel
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.core.graphics.ColorUtils
import androidx.compose.ui.graphics.toArgb
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

// Helper functions for contrast-aware color adjustment
private fun contrastRatio(a: Color, b: Color): Double {
    val l1 = a.luminance()
    val l2 = b.luminance()
    val lighter = max(l1, l2)
    val darker = min(l1, l2)
    return (lighter + 0.05) / (darker + 0.05)
}

private fun adjustColorForContrast(base: Color, makeDarker: Boolean): Color {
    // Work in HSL for intuitive lightness changes
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(base.toArgb(), hsl)
    val step = if (makeDarker) -0.10f else 0.10f
    hsl[2] = (hsl[2] + step).coerceIn(0.05f, 0.95f)
    // Slightly boost saturation for better differentiation
    hsl[1] = (hsl[1] * 1.08f).coerceIn(0f, 1f)
    return Color(ColorUtils.HSLToColor(hsl))
}

// Utility to directly set a new lightness delta relative to original
private fun shiftLightness(color: Color, delta: Float): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(color.toArgb(), hsl)
    hsl[2] = (hsl[2] + delta).coerceIn(0.02f, 0.98f)
    return Color(ColorUtils.HSLToColor(hsl))
}

private fun deriveContrastingButtonColor(paletteColor: Color?, primary: Color, sheetBg: Color): Color {
    val base = paletteColor ?: primary

    // Start from a modified variant similar to previous logic
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(base.toArgb(), hsl)
    hsl[1] = (hsl[1] * 1.10f).coerceIn(0f, 1f)
    hsl[2] = (hsl[2] * 0.85f).coerceIn(0f, 1f)
    var candidate = Color(ColorUtils.HSLToColor(hsl))
    var ratio = contrastRatio(candidate, sheetBg)

    // Raise the desired contrast to make the button stand out more vs sheet
    val targetRatio = 3.6 // previously 3.0; nudged up for clearer separation
    val hardMinimum = 3.0 // absolute minimum we accept before aggressive fallback
    val preferredHigh = 4.5 // try to approach AA text contrast if feasible

    val sheetIsLight = sheetBg.luminance() > 0.5f

    var attempts = 0
    while (ratio < targetRatio && attempts < 6) {
        candidate = adjustColorForContrast(candidate, makeDarker = sheetIsLight)
        ratio = contrastRatio(candidate, sheetBg)
        attempts++
    }

    // If still not great, explore a wider search space of lightness shifts (both directions)
    if (ratio < targetRatio) {
        val searchDeltas = listOf(-0.40f, -0.30f, -0.22f, -0.15f, 0.15f, 0.22f, 0.30f, 0.40f)
        val variants = mutableListOf<Pair<Color, Double>>()
        for (d in searchDeltas) {
            val v = shiftLightness(base, if (sheetIsLight) -kotlin.math.abs(d) else kotlin.math.abs(d))
            variants += v to contrastRatio(v, sheetBg)
        }
        // Also include pure primary darkened/lightened extremes
        val darkExtreme = shiftLightness(base, if (sheetIsLight) -0.55f else 0.0f)
        val lightExtreme = shiftLightness(base, if (sheetIsLight) 0.0f else 0.55f)
        variants += darkExtreme to contrastRatio(darkExtreme, sheetBg)
        variants += lightExtreme to contrastRatio(lightExtreme, sheetBg)

        val best = variants.maxByOrNull { it.second }
        if (best != null && best.second > ratio) {
            candidate = best.first
            ratio = best.second
        }
    }

    // Fallbacks if still weak contrast
    if (ratio < hardMinimum) {
        // Use black/white whichever maximizes contrast
        val blackRatio = contrastRatio(Color.Black, sheetBg)
        val whiteRatio = contrastRatio(Color.White, sheetBg)
        val extreme = if (blackRatio > whiteRatio) Color.Black else Color.White
        // Blend a little with base for identity but keep strong contrast
        candidate = Color(
            ColorUtils.blendARGB(extreme.toArgb(), base.toArgb(), 0.15f)
        )
        ratio = contrastRatio(candidate, sheetBg)
    }

    // If we can cheaply push toward preferredHigh, do a final nudge
    if (ratio in hardMinimum..preferredHigh && ratio < preferredHigh) {
        var tweakAttempts = 0
        while (contrastRatio(candidate, sheetBg) < preferredHigh && tweakAttempts < 4) {
            candidate = adjustColorForContrast(candidate, makeDarker = sheetIsLight)
            tweakAttempts++
        }
    }

    return candidate
}

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

    // Store last cover and description so Discovery screen can show them
    LaunchedEffect(resolvedBook.id) {
        LastSelectedCoverStore.set(resolvedBook.coverImageUrl, resolvedBook.description)
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

    LaunchedEffect(book.coverImageUrl, dynamicTheming, detailsViewModel) {
        if (dynamicTheming && detailsViewModel != null) detailsViewModel.loadPalette(context, book.id, book.coverImageUrl, translucentSheet)
    }
    val paletteColor by (detailsViewModel?.paletteColor?.collectAsState() ?: remember { mutableStateOf<Color?>(null) })

    // Original sheet color reused as inner Surface background (not on full-width scaffold anymore)
    val sheetBgColor = (paletteColor ?: MaterialTheme.colorScheme.surface).let { base ->
        if (translucentSheet) base.copy(alpha = 0.80f) else base
    }

    val primaryColor = MaterialTheme.colorScheme.primary

    // Replace previous buttonColorTarget derivation with contrast-aware version
    val buttonColorTarget: Color = remember(paletteColor, primaryColor, sheetBgColor) {
        deriveContrastingButtonColor(paletteColor, primaryColor, sheetBgColor)
    }
    val buttonColor by animateColorAsState(
        targetValue = buttonColorTarget,
        animationSpec = tween(450),
        label = "button_color_anim"
    )
    val onButtonColor = if (buttonColor.luminance() > 0.5f) Color.Black else Color.White

    val isExpanded = sheetState.bottomSheetState.currentValue == SheetValue.Expanded
    val coverScale by animateFloatAsState(
        targetValue = if (isExpanded) 0.85f else 1f,
        animationSpec = tween(400),
        label = "cover_scale_anim"
    )

    Box(Modifier.fillMaxSize()) {
        // NEW: Global blurred background placed UNDER the scaffold (extends beneath top bar & sheet)
        if (!book.coverImageUrl.isNullOrBlank()) {
            AsyncImage(
                model = book.coverImageUrl,
                contentDescription = null,
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
                    book = book,
                    isFavorite = isFavorite,
                    onFavoriteToggle = onFavoriteToggle,
                    onNavigateBack = onNavigateBack
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
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

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
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
        dynamicTheming = false,
        translucentSheet = true,
        detailsViewModel = null,
        autoExpand = false,
        showDragHandle = false
    )
}