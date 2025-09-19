package com.example.reader.screens.home
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reader.R
import com.example.reader.ui.theme.CardBackground
import com.example.reader.ui.theme.DarkGreenBackground
import com.example.reader.ui.theme.SubtleTextColor
import com.example.reader.ui.theme.TextColor

// You can adjust these colors to perfectly match your design



val GreenBackground = Color(0xFF0B5345) // Dark green background
val GreenAccent = Color(0xFF00C896) // Bright teal for "Continue Reading"
val DarkCardBackground = Color(0xFF1E2A3A) // Dark blue-gray for cards
val WhiteText = Color(0xFFFFFFFF)
val GrayText = Color(0xFFB0B8C4)

@Composable
fun BookFinderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // Total height to accommodate overlap
    ) {
        // Main curved green background
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
        ) {
            val width = size.width
            val height = size.height

            // Create curved path that matches the reference
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(width, 0f)
                lineTo(width, height * 0.85f)
                // Create the curved bottom
                quadraticTo(
                    width * 0.7f, height * 1.1f,
                    width * 0.3f, height * 0.9f
                )
                quadraticTo(
                    width * 0.1f, height * 0.85f,
                    0f, height * 0.75f
                )
                close()
            }

            // Fill with green gradient
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GreenBackground,
                        GreenBackground.copy(alpha = 0.9f)
                    )
                )
            )
        }

        // Content overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            // Main title - exact text from reference
            Text(
                text = "Find interesting books from\nall over the world",
                color = WhiteText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            // "Continue Reading" card - matches reference exactly
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
                        color = GrayText,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Continue Reading",
                        color = GreenAccent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Overlapping "Born a Crime" card at the bottom
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkCardBackground
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Book cover placeholder - replace with actual book cover
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF4A5568)),
                    contentAlignment = Alignment.Center
                ) {
                    // Replace this Icon with your actual book cover image:
                    // Image(
                    //     painter = painterResource(id = R.drawable.born_a_crime_cover),
                    //     contentDescription = "Born a Crime cover",
                    //     contentScale = ContentScale.Crop,
                    //     modifier = Modifier.fillMaxSize()
                    // )
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Book cover",
                        tint = WhiteText,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Book info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
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
                        color = GrayText,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// If you want to use this in your existing screen, replace the BookFinderContent()
// function call with BookFinderSection()
@Composable
fun BookFinderScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E)) // Your app's background color
    ) {
        BookFinderSection()

        // Add your other content below
        // CategoryTabs()
        // BookGridSection()
        // etc.
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun DefaultPreview() {
    // You can wrap your preview in a theme if you have one
    // YourAppTheme {
    Surface {
        BookFinderScreen()
    }
    // }
}