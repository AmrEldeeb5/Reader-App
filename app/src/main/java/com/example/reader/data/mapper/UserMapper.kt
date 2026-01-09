package com.example.reader.data.mapper

import com.example.reader.domain.model.User
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper to convert between Firebase User and domain User model.
 */
@Singleton
class UserMapper @Inject constructor() {
    
    /**
     * Convert FirebaseUser to domain User.
     *
     * @param firebaseUser Firebase user object
     * @return Domain User model
     */
    fun toDomain(firebaseUser: FirebaseUser): User {
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName
        )
    }
}
