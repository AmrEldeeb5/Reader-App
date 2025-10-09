package com.example.reader.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LastSelectedCoverStore {
    private val _lastCoverUrl = MutableStateFlow<String?>(null)
    val lastCoverUrl: StateFlow<String?> = _lastCoverUrl.asStateFlow()

    private val _lastDescription = MutableStateFlow<String?>(null)
    val lastDescription: StateFlow<String?> = _lastDescription.asStateFlow()

    private val _lastTitle = MutableStateFlow<String?>(null)
    val lastTitle: StateFlow<String?> = _lastTitle.asStateFlow()

    private val _lastBookId = MutableStateFlow<Int?>(null)
    val lastBookId: StateFlow<Int?> = _lastBookId.asStateFlow()

    fun set(coverUrl: String?, description: String?, title: String? = null, bookId: Int? = null) {
        _lastCoverUrl.value = coverUrl
        _lastDescription.value = description?.takeIf { it.isNotBlank() }
        _lastTitle.value = title?.takeIf { it.isNotBlank() }
        _lastBookId.value = bookId
    }

    fun clear() {
        _lastCoverUrl.value = null
        _lastDescription.value = null
        _lastTitle.value = null
        _lastBookId.value = null
    }
}
