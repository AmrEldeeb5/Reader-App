package com.example.reader.domain.error

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import retrofit2.HttpException
import java.io.IOException

/**
 * Sealed class hierarchy representing application-level errors.
 *
 * This provides a type-safe way to handle different error scenarios
 * with user-friendly error messages.
 */
sealed class AppError(message: String) : Exception(message) {
    
    /**
     * Network connectivity error.
     *
     * @property message Error message
     */
    data class NetworkError(
        override val message: String = "No internet connection. Please check your network settings."
    ) : AppError(message)
    
    /**
     * Server error with HTTP status code.
     *
     * @property code HTTP status code
     * @property message Error message
     */
    data class ServerError(
        val code: Int,
        override val message: String
    ) : AppError(message)
    
    /**
     * Authentication error.
     *
     * @property message Error message
     */
    data class AuthError(
        override val message: String
    ) : AppError(message)
    
    /**
     * Validation error for user input.
     *
     * @property message Error message
     */
    data class ValidationError(
        override val message: String
    ) : AppError(message)
    
    /**
     * Unknown or unexpected error.
     *
     * @property message Error message
     */
    data class UnknownError(
        override val message: String = "An unexpected error occurred. Please try again."
    ) : AppError(message)
}

/**
 * Extension function to convert any Throwable to an AppError.
 *
 * This provides consistent error mapping across the application.
 *
 * @return AppError corresponding to the exception type
 */
fun Throwable.toAppError(): AppError {
    return when (this) {
        is IOException -> AppError.NetworkError()
        is HttpException -> AppError.ServerError(
            code = code(),
            message = "Server error (${code()}): ${message()}"
        )
        is FirebaseAuthException -> AppError.AuthError(
            message = message ?: "Authentication failed. Please try again."
        )
        is FirebaseException -> AppError.UnknownError(
            message = message ?: "A Firebase error occurred."
        )
        is AppError -> this
        else -> AppError.UnknownError(
            message = message ?: "An unexpected error occurred."
        )
    }
}
