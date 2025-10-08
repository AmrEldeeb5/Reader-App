package com.example.reader.screens.details

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reader.R
import com.example.reader.data.model.Book
import com.example.reader.screens.saved.FavoritesViewModel
import org.koin.compose.koinInject

@Composable
fun BookDetailsScreen(
                      navController: NavController,
                      favoritesViewModel: FavoritesViewModel = koinInject(),

) {

    val favoriteBooks by favoritesViewModel.favoriteBooks.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize().
            background(MaterialTheme.colorScheme.background)

    ) {
        // Title
        BookDetailsHeader(book = book,
            isFavorite = favoriteBooks.any { it.id == book.id },
            onFavoriteToggle = { onFavoriteToggle(book) },

        )
        Card(modifier = Modifier.fillMaxSize()){
            Column(modifier = Modifier.fillMaxWidth()
                ,horizontalAlignment = Alignment.CenterHorizontally
                ,verticalArrangement = Arrangement.Center){

            }

        }
    }
}

@Composable
fun BookDetailsHeader(book: Book, isFavorite: Boolean, onFavoriteToggle: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()
        ,horizontalArrangement = Arrangement.SpaceBetween){
        // Title
        Icon(
            painter = painterResource(id = R.drawable.solar__alt_arrow_left_line_duotone),
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp)
        )
        IconButton(onClick = onFavoriteToggle) {
            val tint by animateColorAsState(
                targetValue = if (book.isFavorite) Color.Red else Color.White,
                animationSpec = tween(300),
                label = "favorite_color"
            )
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Favorite",
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}