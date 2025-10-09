package com.example.reader.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import coil.compose.AsyncImage
import com.example.reader.data.LastSelectedCoverStore
import com.example.reader.ui.theme.CardBackground
import com.example.reader.ui.theme.GreenDark
import com.example.reader.ui.theme.GreenLight
import com.example.reader.ui.theme.GreenMid
import com.example.reader.ui.theme.GreenPrimary
import com.example.reader.ui.theme.ReaderTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BookDiscoveryScreen(isDarkTheme: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(contentAlignment = Alignment.TopCenter) {
            BookFinderBackground(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(270.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 130.dp)
            ) {
                QuoteCard(isDarkTheme = isDarkTheme)
                BookPlayerCard(
                    modifier = Modifier.offset(y = (-15).dp),
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

@Composable
fun QuoteCard(isDarkTheme: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(0.88f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, top = 12.dp, start = 12.dp, end = 12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "On conviction to imprisonment for a period not exceeding four years...",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Continue Reading",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { /* Handle click */ }
                )
            }
        }
    }
}

@Composable
fun BookPlayerCard(modifier: Modifier = Modifier, isDarkTheme: Boolean) {
    Card(
        modifier = modifier
            .padding(top = 1.dp)
            .fillMaxWidth(0.88f),
        shape = RoundedCornerShape(bottomEnd = 12.dp, bottomStart = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val lastCoverUrl by LastSelectedCoverStore.lastCoverUrl.collectAsState(null)
            if (!lastCoverUrl.isNullOrBlank()) {
                AsyncImage(
                    model = lastCoverUrl,
                    contentDescription = "Last book cover",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = "Book cover icon",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))

            val lastDescription by LastSelectedCoverStore.lastDescription.collectAsState(null)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = lastDescription ?: "No description available",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Chapter 1 of 4",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun BookFinderBackground(modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            if (isDark) {
                // Dark theme gradient (existing)
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF00796B),
                            Color(0xFF00695C),
                            Color(0xFF004D40),
                            Color(0xFF00332C)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    )
                )
            } else {
                // Light theme gradient
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            GreenLight,      // Light teal
                            GreenMid,        // Medium green
                            GreenPrimary,    // Primary green
                            GreenDark        // Dark green
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    )
                )
            }

            // Wave patterns work for both themes
            val wave1 = Path().apply {
                moveTo(size.width, 0f)
                quadraticTo(
                    size.width * 0.5f, size.height * 0.6f,
                    0f, size.height * 0.3f
                )
                lineTo(0f, 0f)
                lineTo(size.width, 0f)
                close()
            }
            drawPath(
                path = wave1,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (isDark) 0.08f else 0.15f),
                        Color.Transparent
                    ),
                    start = Offset(size.width, 0f),
                    end = Offset(0f, size.height * 0.5f)
                )
            )

            val wave2 = Path().apply {
                moveTo(size.width, size.height * 0.25f)
                quadraticTo(
                    size.width * 0.55f, size.height * 0.75f,
                    0f, size.height * 0.55f
                )
                lineTo(0f, size.height)
                lineTo(size.width, size.height)
                close()
            }
            drawPath(
                path = wave2,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = if (isDark) 0.12f else 0.06f),
                        Color.Transparent
                    ),
                    start = Offset(size.width, size.height * 0.25f),
                    end = Offset(0f, size.height)
                )
            )
        }

        // Text remains white on both themes for good contrast
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 32.dp, vertical = 30.dp)
        ) {
            Text(
                text = "Find interesting books from all over the world",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun DefaultPreview() {
    ReaderTheme(darkTheme = true) {
        BookDiscoveryScreen(isDarkTheme = true)
    }
}