package com.example.reader.screens.details

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.palette.graphics.Palette
import androidx.core.graphics.ColorUtils as AndroidColorUtils
import com.example.reader.domain.model.Book
import com.example.reader.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {
    private val _paletteColor = MutableStateFlow<Color?>(null)
    val paletteColor: StateFlow<Color?> = _paletteColor.asStateFlow()

    private val _bookDetails = MutableStateFlow<Book?>(null)
    val bookDetails: StateFlow<Book?> = _bookDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _recommendations = MutableStateFlow<List<Book>>(emptyList())
    val recommendations: StateFlow<List<Book>> = _recommendations.asStateFlow()

    private val _isLoadingRecommendations = MutableStateFlow(false)
    val isLoadingRecommendations: StateFlow<Boolean> = _isLoadingRecommendations.asStateFlow()

    // In-memory cache of raw dominant colors keyed by book id
    private val paletteCache: MutableMap<String, Color> = mutableMapOf()

    /**
     * Fetch book details by ID from repository (with cache support).
     */
    fun fetchBookById(bookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = bookRepository.getBookById(bookId)

            result.fold(
                onSuccess = { book ->
                    _bookDetails.value = book
                    _isLoading.value = false
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Failed to load book"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Clear book details and error state
     */
    fun clearBookDetails() {
        _bookDetails.value = null
        _error.value = null
        _isLoading.value = false
    }

    /**
     * Fetch similar/recommended books based on the current book's category
     */
    fun fetchRecommendations(category: String?) {
        if (category.isNullOrBlank()) {
            _recommendations.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoadingRecommendations.value = true

            // Search for books in the same category
            val result = bookRepository.searchBooks(category)

            result.fold(
                onSuccess = { books ->
                    // Exclude the current book if present
                    val currentBookId = _bookDetails.value?.id
                    _recommendations.value = books
                        .filter { it.id != currentBookId }
                        .take(6) // Limit to 6 recommendations
                    _isLoadingRecommendations.value = false
                },
                onFailure = {
                    _recommendations.value = emptyList()
                    _isLoadingRecommendations.value = false
                }
            )
        }
    }

    /**
     * Load (or reuse) the dominant color from the book cover.
     * The 'translucent' flag is now ignored here; alpha is applied in the UI layer so we always cache raw color.
     */
    fun loadPalette(context: Context, bookId: String, url: String?, @Suppress("UNUSED_PARAMETER") translucent: Boolean) {
        paletteCache[bookId]?.let { cached ->
            _paletteColor.value = cached
            return
        }
        if (url.isNullOrBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val loader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .allowHardware(false)
                    .build()
                val result = (loader.execute(request) as? coil.request.SuccessResult)?.drawable
                val bmp = (result as? android.graphics.drawable.BitmapDrawable)?.bitmap ?: return@launch
                val palette = Palette.from(bmp).clearFilters().generate()
                val selected = selectDominantLikeSwatch(palette)
                selected?.let { swatch ->
                    val base = Color(swatch.rgb)
                    paletteCache[bookId] = base
                    _paletteColor.value = base // emit raw color (no alpha)
                }
            } catch (_: Exception) { /* ignore failures */ }
        }
    }

    /**
     * Choose the most usable dominant-like swatch. Preference order:
     * 1. True dominant swatch if sufficiently saturated OR has high population.
     * 2. Vibrant / Dark Vibrant / Light Vibrant
     * 3. Muted / Dark Muted / Light Muted
     * 4. Fallback: any swatch with highest population.
     */
    private fun selectDominantLikeSwatch(palette: Palette): Palette.Swatch? {
        val dominant = palette.dominantSwatch
        if (dominant != null && isAcceptable(dominant)) return dominant
        val candidates = listOfNotNull(
            palette.vibrantSwatch,
            palette.darkVibrantSwatch,
            palette.lightVibrantSwatch,
            palette.mutedSwatch,
            palette.darkMutedSwatch,
            palette.lightMutedSwatch
        ).filter { isAcceptable(it) }
        if (candidates.isNotEmpty()) return candidates.maxByOrNull { it.population }
        // Fallback: pick the swatch (including dominant) with max population even if low saturation
        val all = buildList {
            palette.swatches.forEach { add(it) }
        }
        return all.maxByOrNull { it.population }
    }

    private fun isAcceptable(swatch: Palette.Swatch): Boolean {
        val hsl = swatch.hsl
        val saturationOk = hsl[1] >= 0.12f // not totally gray
        val populationOk = swatch.population >= 24 // arbitrary small threshold to avoid noise
        return saturationOk && populationOk
    }

    fun reset() { _paletteColor.value = null }
}
