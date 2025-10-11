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
import com.example.reader.ui.theme.BrownDark
import com.example.reader.ui.theme.BrownLight
import com.example.reader.ui.theme.BrownMid
import com.example.reader.ui.theme.BrownPrimary
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
fun BookDiscoveryScreen(
    onContinueReading: (Int) -> Unit = {},
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isGreenTheme: Boolean = true
) {
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
                    .clip(RoundedCornerShape(16.dp)),
                isDarkTheme = isDarkTheme,
                isGreenTheme = isGreenTheme
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 130.dp)
            ) {
                val lastBookId by LastSelectedCoverStore.lastBookId.collectAsState(null)
                val lastDescription by LastSelectedCoverStore.lastDescription.collectAsState(null)

                if (lastBookId != null) {
                    QuoteCard(
                        text = lastDescription ?: "Continue your last reading",
                        onContinue = { onContinueReading(lastBookId!!) }
                    )
                }

                // Only show BookPlayerCard after a BookCard has been clicked (state set)
                if (lastBookId != null) {
                    BookPlayerCard(
                        modifier = Modifier.offset(y = (-15).dp)
                    )
                }
            }
        }
    }
}

@Composable
fun QuoteCard(text: String, onContinue: (() -> Unit)? = null) {
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
                    text = text,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
                val clickableModifier = if (onContinue != null) Modifier.clickable { onContinue() } else Modifier
                Text(
                    text = "Continue Reading",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = clickableModifier
                )
            }
        }
    }
}

@Composable
fun BookPlayerCard(modifier: Modifier = Modifier) {
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
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))

            val lastTitle by LastSelectedCoverStore.lastTitle.collectAsState(null)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = lastTitle ?: "No title available",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                val lastCategory by LastSelectedCoverStore.lastCategoryName.collectAsState(null)
                Text(
                    text = lastCategory ?: "Category",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun BookFinderBackground(modifier: Modifier = Modifier, isDarkTheme: Boolean, isGreenTheme: Boolean) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // Choose gradient colors based on theme combination
            val gradientColors = when {
                isDarkTheme && isGreenTheme -> listOf(
                    Color(0xFF00796B),  // Teal
                    Color(0xFF00695C),  // Dark teal
                    Color(0xFF004D40),  // Darker teal
                    Color(0xFF00332C)   // Darkest teal
                )
                isDarkTheme && !isGreenTheme -> listOf(
                    Color(0xFF6D4C41),  // Brown primary
                    Color(0xFF5D4037),  // Darker brown
                    Color(0xFF4E342E),  // Even darker brown
                    Color(0xFF3E2723)   // Darkest brown
                )
                !isDarkTheme && isGreenTheme -> listOf(
                    Color(0xFF3FAF9E),  // Light green
                    Color(0xFF2F8F81),  // Green mid
                    Color(0xFF24786D),  // Green primary
                    Color(0xFF0F4F47)   // Green dark
                )
                else -> listOf(  // Light brown theme
                    BrownLight,      // Light brown
                    BrownMid,        // Medium brown
                    BrownPrimary,    // Primary brown
                    BrownDark        // Dark brown
                )
            }

            // Draw main gradient background
            drawRect(
                brush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )
            )

            // Wave 1 - Top decorative wave
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
                        Color.White.copy(alpha = if (isDarkTheme) 0.08f else 0.15f),
                        Color.Transparent
                    ),
                    start = Offset(size.width, 0f),
                    end = Offset(0f, size.height * 0.5f)
                )
            )

            // Wave 2 - Mid decorative wave
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
                        Color.Black.copy(alpha = if (isDarkTheme) 0.12f else 0.06f),
                        Color.Transparent
                    ),
                    start = Offset(size.width, size.height * 0.25f),
                    end = Offset(0f, size.height)
                )
            )

            // Wave 3 - Additional depth wave
            val wave3 = Path().apply {
                moveTo(0f, size.height * 0.70f)
                quadraticTo(
                    size.width * 0.40f, size.height * 0.95f,
                    size.width, size.height * 0.75f
                )
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(
                path = wave3,
                brush = Brush.linearGradient(
                    colors = listOf(
                        if (isDarkTheme) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height * 0.70f)
                )
            )

            // Wave 4 - Theme-aware accent wave
            val wave4 = Path().apply {
                moveTo(size.width, size.height * 0.62f)
                quadraticTo(
                    size.width * 0.55f, size.height * 0.88f,
                    0f, size.height * 0.74f
                )
                lineTo(0f, size.height)
                lineTo(size.width, size.height)
                close()
            }

            val wave4Color = when {
                isDarkTheme -> Color.White.copy(alpha = 0.05f)
                isGreenTheme -> Color(0xFF2F8F81).copy(alpha = 0.07f)  // Green accent
                else -> BrownMid.copy(alpha = 0.07f)  // Brown accent
            }

            drawPath(
                path = wave4,
                brush = Brush.linearGradient(
                    colors = listOf(wave4Color, Color.Transparent),
                    start = Offset(size.width, size.height * 0.62f),
                    end = Offset(0f, size.height)
                )
            )

            // Wave 5 - Bottom subtle layering
            val wave5 = Path().apply {
                moveTo(size.width, size.height * 0.85f)
                quadraticTo(
                    size.width * 0.70f, size.height * 1.05f,
                    0f, size.height * 0.90f
                )
                lineTo(0f, size.height)
                lineTo(size.width, size.height)
                close()
            }

            val wave5Color = when {
                isDarkTheme -> Color.White.copy(alpha = 0.035f)
                isGreenTheme -> Color(0xFF0F4F47).copy(alpha = 0.06f)  // Green dark
                else -> BrownDark.copy(alpha = 0.06f)  // Brown dark
            }

            drawPath(
                path = wave5,
                brush = Brush.linearGradient(
                    colors = listOf(wave5Color, Color.Transparent),
                    start = Offset(size.width, size.height),
                    end = Offset(0f, size.height * 0.85f)
                )
            )
        }

        // Text remains white on both themes for good contrast
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Text(
                text = "Find interesting books from all over the world",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun DefaultPreview() {
    ReaderTheme(darkTheme = true) {
        BookDiscoveryScreen()
    }
}