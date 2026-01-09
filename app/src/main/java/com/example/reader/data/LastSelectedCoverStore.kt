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

    private val _lastBookId = MutableStateFlow<String?>(null)
    val lastBookId: StateFlow<String?> = _lastBookId.asStateFlow()

    private val _lastCategoryName = MutableStateFlow<String?>(null)
    val lastCategoryName: StateFlow<String?> = _lastCategoryName.asStateFlow()

    fun set(
        coverUrl: String? = null,
        description: String? = null,
        title: String? = null,
        bookId: String? = null,
        categoryName: String? = null
    ) {
        if (coverUrl != null) _lastCoverUrl.value = coverUrl
        if (description != null) _lastDescription.value = description.takeIf { it.isNotBlank() }
        if (title != null) _lastTitle.value = title.takeIf { it.isNotBlank() }
        if (bookId != null) _lastBookId.value = bookId
        if (categoryName != null) _lastCategoryName.value = categoryName.takeIf { it.isNotBlank() }
    }

    fun clear() {
        _lastCoverUrl.value = null
        _lastDescription.value = null
        _lastTitle.value = null
        _lastBookId.value = null
        _lastCategoryName.value = null
    }
}
