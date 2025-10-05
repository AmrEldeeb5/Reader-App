package com.example.reader.components

object LoginConstants {
    const val BUTTON_HEIGHT = 50
    const val PROGRESS_INDICATOR_SIZE = 22
    const val PROGRESS_STROKE_WIDTH = 2
}

// Data classes for better state management
data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val loginError: String? = null
)
