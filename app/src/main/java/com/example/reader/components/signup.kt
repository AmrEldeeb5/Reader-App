package com.example.reader.components

object SignUpConstants {
    const val MIN_PASSWORD_LENGTH = 6
    const val PROGRESS_INDICATOR_SIZE = 22
    const val PROGRESS_STROKE_WIDTH = 2
}

// Data classes for better state management
data class SignUpFormState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false
)

data class FormErrors(
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null
) {
    val hasErrors: Boolean
        get() = nameError != null || emailError != null || passwordError != null ||
                confirmPasswordError != null || generalError != null
}
