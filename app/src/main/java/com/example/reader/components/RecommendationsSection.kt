package com.example.reader.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.reader.domain.model.Book
import com.example.reader.screens.home.BookCard

/**
 * Recommendations Section Component
 * Shows "Similar Books" based on the current book or user's favorites
 */
@Composable
fun RecommendationsSection(
    modifier: Modifier = Modifier,
    title: String = "You Might Also Like",
    recommendedBooks: List<Book>,
    favoriteIds: Set<String> = emptySet(),
    isDarkTheme: Boolean = false,
    onFavoriteToggle: (Book) -> Unit = {},
    onRatingChange: (String, Float) -> Unit = { _, _ -> },
    onBookClick: (Book) -> Unit = {}
) {
    if (recommendedBooks.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Horizontal scrollable book list
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(recommendedBooks, key = { it.id }) { book ->
                BookCard(
                    book = book,
                    isFavorite = favoriteIds.contains(book.id),
                    isDarkTheme = isDarkTheme,
                    onFavoriteToggle = { onFavoriteToggle(book) },
                    onRatingChange = { rating -> onRatingChange(book.id, rating.toFloat()) },
                    onBookClick = { onBookClick(book) }
                )
            }
        }
    }
}

/**
 * Loading skeleton for recommendations section
 */
@Composable
fun RecommendationsSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Skeleton header
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .width(180.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        // Skeleton book cards
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(3) {
                BookCardSkeleton()
            }
        }
    }
}

