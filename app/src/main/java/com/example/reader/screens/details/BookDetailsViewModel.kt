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

class BookDetailsViewModel : ViewModel() {
    private val _paletteColor = MutableStateFlow<Color?>(null)
    val paletteColor: StateFlow<Color?> = _paletteColor.asStateFlow()

    // In-memory cache of raw (non-alpha adjusted) base colors keyed by book id
    private val paletteCache: MutableMap<Int, Color> = mutableMapOf()

    fun loadPalette(context: Context, bookId: Int, url: String?, translucent: Boolean) {
        // If currently showing this book and already have a color, just ensure alpha matches translucent flag
        paletteCache[bookId]?.let { cached ->
            _paletteColor.value = if (translucent) cached.copy(alpha = 0.88f) else cached
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
                val palette = androidx.palette.graphics.Palette.from(bmp).clearFilters().generate()
                val swatch = palette.darkVibrantSwatch
                    ?: palette.vibrantSwatch
                    ?: palette.mutedSwatch
                    ?: palette.lightVibrantSwatch
                    ?: palette.dominantSwatch
                swatch?.let {
                    val base = Color(it.rgb)
                    paletteCache[bookId] = base // store raw base
                    _paletteColor.value = if (translucent) base.copy(alpha = 0.88f) else base
                }
            } catch (_: Exception) { /* ignore */ }
        }
    }

    fun reset() {
        _paletteColor.value = null
    }
}
