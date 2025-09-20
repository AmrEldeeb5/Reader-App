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
            .padding(vertical = 40.dp), // Pushes the content away from the very top/bottom
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // This Box will contain all the stacked elements
        Box(contentAlignment = Alignment.TopCenter) {
            BookFinderBackground(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(250.dp)
                    .clip(RoundedCornerShape(9.dp))
            )
            // Column for the cards that overlap
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                // This pushes the cards down to start from the middle of the banner
                modifier = Modifier.padding(top = 100.dp)
            ) {
                QuoteCard()
                // The negative offset is the key to the overlap effect
                BookPlayerCard(modifier = Modifier.offset(y = (-40).dp))
            }
        }
    }
}



@Composable
fun QuoteCard() {
    Card(
        modifier = Modifier.fillMaxWidth(0.8f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp, top = 16.dp, start = 16.dp, end = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )){
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "On conviction to imprisonment for a period not exceeding four years...",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                lineHeight = 24.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Continue Reading",
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.clickable { /* Handle click */ }
            )
        }
    }}
}

@Composable
fun BookPlayerCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(top = 1.dp).fillMaxWidth(0.8f),
        shape = RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = "Book cover icon",
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f) // Takes up remaining space
            ) {
                Text(
                    text = "Born a Crime, Stories from a South...",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Chapter 1 of 4",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
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
                .padding(horizontal = 40.dp, vertical = 32.dp)
        ) {
            Text(
                text = "Find interesting books from all over the world",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                lineHeight = 30.sp,
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