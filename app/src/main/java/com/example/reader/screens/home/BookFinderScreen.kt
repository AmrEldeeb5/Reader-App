package com.example.reader.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// --- Colors updated to match the new reference image ---
val PrimaryGreen = Color(0xFF014D40)
val AccentStrokeGreen = Color(0xFF00C896)
val DarkCardBackground = Color(0xFF22242C)
val CardTextColor = Color(0xFFAEB3B7)
val WhiteText = Color.White
val SubtleAccentFillGreen = Color(0xFF015C4E) // New color for the tonal difference


@Composable
fun BookFinderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // Total height to accommodate overlap
    ) {
        // --- Redesigned Green Background ---
        // We now use a single Canvas to draw the rounded rectangle background
        // and the decorative accent curve inside it.
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
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
                quadraticBezierTo(
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
                quadraticBezierTo(
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
                quadraticBezierTo(
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
                        Color(0xFF002F27).copy(alpha = 0.3f), // deep green accent
                        Color.Transparent
                    ),
                    start = Offset(size.width, size.height * 0.5f),
                    end = Offset(0f, size.height)
                )
            )
        }






        // --- Content overlay ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 32.dp) // Adjusted padding for content
        ) {
            // Main title - text updated to match reference
            Text(
                text = "Find interesting books from\nall over the world",
                color = WhiteText,
                fontSize = 24.sp, // Adjusted font size
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            // "Continue Reading" card - text updated to match reference
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        DarkCardBackground,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "On conviction to imprisonment for a period\nnot exceeding four years...",
                        color = CardTextColor,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Continue Reading",
                        color = AccentStrokeGreen, // Use the same accent color
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Overlapping card at the bottom
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF4A5568)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = "Book cover",
                        tint = WhiteText,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Born a Crime, Stories from a South...",
                        color = WhiteText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Chapter 1 of 4",
                        color = CardTextColor,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun DefaultPreview() {
    Surface(color = Color(0xFF1A1A2E)) { // Example background for preview
        BookFinderSection()
    }
}