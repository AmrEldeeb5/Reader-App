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
        is HttpException -> {
            val userFriendlyMessage = when (code()) {
                400 -> "Invalid request. Please try again."
                401 -> "Authentication required. Please sign in again."
                403 -> "Access denied. You don't have permission for this action."
                404 -> "The requested content was not found."
                408 -> "Request timeout. Please check your connection and try again."
                429 -> "Too many requests. Please wait a moment and try again."
                500 -> "Server error. Please try again later."
                502, 503 -> "Service temporarily unavailable. Please try again later."
                else -> "Server error (${code()}). Please try again."
            }
            AppError.ServerError(
                code = code(),
                message = userFriendlyMessage
            )
        }
        is FirebaseAuthException -> {
            val authMessage = when (this.errorCode) {
                "ERROR_INVALID_EMAIL" -> "Invalid email address. Please check and try again."
                "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please try again."
                "ERROR_USER_NOT_FOUND" -> "No account found with this email. Please sign up first."
                "ERROR_USER_DISABLED" -> "This account has been disabled. Please contact support."
                "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already registered. Please sign in instead."
                "ERROR_WEAK_PASSWORD" -> "Password is too weak. Please use at least 6 characters."
                "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Please check your connection and try again."
                else -> message ?: "Authentication failed. Please try again."
            }
            AppError.AuthError(message = authMessage)
        }
        is FirebaseException -> AppError.UnknownError(
            message = message ?: "A Firebase error occurred. Please try again."
        )
        is AppError -> this
        else -> AppError.UnknownError(
            message = message ?: "An unexpected error occurred. Please try again."
        )
    }
}

/**
 * Get a user-friendly error message from an AppError.
 * Provides context-aware error messages with actionable suggestions.
 */
fun AppError.toUserFriendlyMessage(): String {
    return when (this) {
        is AppError.NetworkError -> this.message
        is AppError.ServerError -> this.message
        is AppError.AuthError -> this.message
        is AppError.ValidationError -> this.message
        is AppError.UnknownError -> this.message
    }
}

/**
 * Get a user-friendly error message from any Throwable.
 */
fun Throwable.toUserFriendlyMessage(): String {
    return this.toAppError().toUserFriendlyMessage()
}
