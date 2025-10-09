package com.example.reader.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LastSelectedCoverStore {
    private val _lastCoverUrl = MutableStateFlow<String?>(null)
    val lastCoverUrl: StateFlow<String?> = _lastCoverUrl.asStateFlow()

    private val _lastDescription = MutableStateFlow<String?>(null)
    val lastDescription: StateFlow<String?> = _lastDescription.asStateFlow()

    fun set(coverUrl: String?, description: String?) {
        _lastCoverUrl.value = coverUrl
        _lastDescription.value = description?.takeIf { it.isNotBlank() }
    }
}
