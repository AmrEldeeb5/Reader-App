package com.example.reader.screens.savedScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reader.R
import com.example.reader.navigation.ReaderScreens
import com.example.reader.screens.home.BookCard
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reader.screens.savedScreen.FavoritesViewModel
import androidx.compose.ui.graphics.Color

@Composable
fun SavedScreen(
    navController: NavController,
    isDarkTheme: Boolean = false,
    onThemeToggle: (Boolean) -> Unit = {},
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
) {
    val favoriteBooks by favoritesViewModel.favoriteBooks.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Title
        Text(
            text = "Saved Books",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        if (favoriteBooks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No saved books yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Start adding books to your favorites!",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.streamline_stickies_color__book_library),
                        contentDescription = "No Favorites Icon",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Unspecified
                    )

                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteBooks, key = { it.bookId }) { favorite ->
                    BookCard(
                        book = favorite.book,
                        isFavorite = true,
                        isDarkTheme = isDarkTheme,
                        onFavoriteToggle = {
                            favoritesViewModel.removeFavorite(favorite.bookId)
                        },
                        onRatingChange = { rating ->
                            favoritesViewModel.updateUserRating(favorite.bookId, rating)
                        },
                        onBookClick = {
                            favoritesViewModel.setCurrentBook(favorite.book)
                            navController.navigate(ReaderScreens.DetailScreen.name + "/${favorite.book.id}")
                        }
                    )
                }
            }
        }
    }
}