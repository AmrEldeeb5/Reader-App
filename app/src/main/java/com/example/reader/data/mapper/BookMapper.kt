package com.example.reader.data.mapper

import com.example.reader.data.source.local.realm.entities.FavoriteBookRealm
import com.example.reader.data.source.remote.api.dto.BookItemDto
import com.example.reader.domain.model.Book
import com.example.reader.domain.model.Favorite
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper to convert between DTOs/Realm entities and domain models.
 */
@Singleton
class BookMapper @Inject constructor() {
    
    /**
     * Convert BookItemDto to domain Book.
     *
     * @param dto Book item DTO from API
     * @return Domain Book model
     */
    fun toDomain(dto: BookItemDto): Book {
        return Book(
            id = dto.id,
            title = dto.volumeInfo.title ?: "Unknown",
            author = dto.volumeInfo.authors?.joinToString(", ") ?: "Unknown",
            subtitle = dto.volumeInfo.subtitle ?: "",
            rating = dto.volumeInfo.averageRating ?: 0.0,
            coverImageUrl = dto.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://"),
            description = dto.volumeInfo.description,
            publishedDate = dto.volumeInfo.publishedDate
        )
    }
    
    /**
     * Convert domain Book to FavoriteBookRealm entity.
     *
     * @param book Domain Book model
     * @param userRating User's rating (optional)
     * @return Realm entity
     */
    fun toRealm(book: Book, userRating: Double? = null): FavoriteBookRealm {
        return FavoriteBookRealm().apply {
            id = book.id
            title = book.title
            author = book.author
            subtitle = book.subtitle
            rating = book.rating
            coverImageUrl = book.coverImageUrl
            this.userRating = userRating
            description = book.description
            publishedDate = book.publishedDate
            addedTimestamp = System.currentTimeMillis()
        }
    }
    
    /**
     * Convert FavoriteBookRealm entity to domain Favorite.
     *
     * @param entity Realm entity
     * @return Domain Favorite model
     */
    fun fromRealm(entity: FavoriteBookRealm): Favorite {
        return Favorite(
            bookId = entity.id,
            book = Book(
                id = entity.id,
                title = entity.title,
                author = entity.author,
                subtitle = entity.subtitle,
                rating = entity.rating,
                coverImageUrl = entity.coverImageUrl,
                description = entity.description,
                publishedDate = entity.publishedDate
            ),
            userRating = entity.userRating,
            addedTimestamp = entity.addedTimestamp
        )
    }
    
    /**
     * Convert FavoriteBookRealm entity to domain Book.
     *
     * @param entity Realm entity
     * @return Domain Book model
     */
    fun realmToBook(entity: FavoriteBookRealm): Book {
        return Book(
            id = entity.id,
            title = entity.title,
            author = entity.author,
            subtitle = entity.subtitle,
            rating = entity.rating,
            coverImageUrl = entity.coverImageUrl,
            description = entity.description,
            publishedDate = entity.publishedDate
        )
    }
}
