package com.example.reader.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.reader.ui.theme.CardBackground
import com.example.reader.ui.theme.ReaderTheme

@Composable
fun BookDiscoveryScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 24.dp), // Reduced top padding significantly
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top // Changed to Top alignment
    ) {
        // This Box will contain all the stacked elements
        Box(contentAlignment = Alignment.TopCenter) {
            BookFinderBackground(
                modifier = Modifier
                    .fillMaxWidth(0.95f) // Slightly wider
                    .height(270.dp) // Reduced height to match reference
                    .clip(RoundedCornerShape(16.dp)) // Slightly more rounded
            )
            // Column for the cards that overlap
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                // Reduced top padding to position cards higher
                modifier = Modifier.padding(top = 130.dp)
            ) {
                QuoteCard()
                // Reduced negative offset for tighter overlap
                BookPlayerCard(modifier = Modifier.offset(y = (-15).dp))
            }
        }
    }
}

@Composable
fun QuoteCard() {
    Card(
        modifier = Modifier.fillMaxWidth(0.88f), // Slightly wider
        shape = RoundedCornerShape(12.dp), // Less rounded
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, top = 12.dp, start = 12.dp, end = 12.dp), // Reduced padding
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp) // Reduced padding
            ) {
                Text(
                    text = "On conviction to imprisonment for a period not exceeding four years...",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp, // Slightly smaller font
                    lineHeight = 20.sp, // Reduced line height
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp)) // Reduced spacer
                Text(
                    text = "Continue Reading",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp, // Slightly smaller
                    modifier = Modifier.clickable { /* Handle click */ }
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
            .fillMaxWidth(0.88f), // Match quote card width
        shape = RoundedCornerShape(bottomEnd = 12.dp, bottomStart = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Reduced elevation
    ) {
        Row(
            modifier = Modifier.padding(16.dp), // Slightly reduced padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = "Book cover icon",
                modifier = Modifier
                    .size(48.dp) // Smaller icon
                    .clip(RoundedCornerShape(8.dp)),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(12.dp)) // Reduced spacer
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Born a Crime, Stories from a South...",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp, // Slightly smaller
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp)) // Reduced spacer
                Text(
                    text = "Chapter 1 of 4",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun BookFinderBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
    ) {
        // Draw background waves
        Canvas(modifier = Modifier.matchParentSize()) {
            // --- Background with richer gradient ---
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF00796B), // light teal
                        Color(0xFF00695C), // medium green
                        Color(0xFF004D40), // dark green
                        Color(0xFF00332C)  // deepest shade
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )
            )

            // --- Top main wave (bright highlight) ---
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
                        Color.White.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    start = Offset(size.width, 0f),
                    end = Offset(0f, size.height * 0.5f)
                )
            )

            // --- Middle wave (soft dark shade) ---
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
                        Color.Black.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    start = Offset(size.width, size.height * 0.25f),
                    end = Offset(0f, size.height)
                )
            )

            // --- Bottom wave (deep subtle accent) ---
            val wave3 = Path().apply {
                moveTo(size.width, size.height * 0.5f)
                quadraticTo(
                    size.width * 0.5f, size.height * 1.0f,
                    0f, size.height * 0.85f
                )
                lineTo(0f, size.height)
                lineTo(size.width, size.height)
                close()
            }
            drawPath(
                path = wave3,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF002F27).copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    start = Offset(size.width, size.height * 0.5f),
                    end = Offset(0f, size.height)
                )
            )
        }

        // Text overlay centered over the canvas
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 32.dp, vertical = 30.dp) // Reduced padding
        ) {
            Text(
                text = "Find interesting books from all over the world",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
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