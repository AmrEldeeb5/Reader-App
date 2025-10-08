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

class BookDetailsViewModel : ViewModel() {
    private val _paletteColor = MutableStateFlow<Color?>(null)
    val paletteColor: StateFlow<Color?> = _paletteColor.asStateFlow()

    // In-memory cache of raw dominant colors keyed by book id
    private val paletteCache: MutableMap<Int, Color> = mutableMapOf()

    /**
     * Load (or reuse) the dominant color from the book cover.
     * The 'translucent' flag is now ignored here; alpha is applied in the UI layer so we always cache raw color.
     */
    fun loadPalette(context: Context, bookId: Int, url: String?, @Suppress("UNUSED_PARAMETER") translucent: Boolean) {
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
