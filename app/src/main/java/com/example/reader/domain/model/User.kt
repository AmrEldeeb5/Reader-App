package com.example.reader.domain.model

import androidx.compose.runtime.Immutable

/**
 * Domain model representing a user in the Reader application.
 *
 * @property id Unique identifier for the user
 * @property email User's email address
 * @property displayName User's display name (optional)
 */
@Immutable
data class User(
    val id: String,
    val email: String,
    val displayName: String?
)
