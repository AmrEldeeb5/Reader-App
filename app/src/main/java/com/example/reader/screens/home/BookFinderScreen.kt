package com.example.reader.screens.home
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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



@Composable
fun BookFinderScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGreenBackground)
    ) {
        // --- This is for the curved background shapes ---
        CurvedBackgroundHeaderShapes()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Main title text
            Text(
                text = "Find interesting books from all over the world",
                color = TextColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp,
                modifier = Modifier.padding(top = 60.dp)
            )

            Spacer(modifier = Modifier.weight(1f)) // Pushes the next item to the bottom

            // --- THIS IS WHERE YOUR COMPOSABLE GOES ---
            // You can replace this placeholder Box with your own implementation
            // for the "On conviction to imprisonment..." card.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // Example height
                    .background(CardBackground, shape = RoundedCornerShape(16.dp))
            ) {
                // TODO: Replace this with your dark section composable
                Text(
                    "Your composable goes here",
                    color = SubtleTextColor,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            // --- END OF YOUR SECTION ---


            // Bottom book card
            BookProgressCard(
                bookCoverResId = R.drawable.person, // Your image here
                title = "Born a Crime, Stories from a South...",
                progress = "Chapter 1 of 4"
            )
        }
    }
}

@Composable
fun CurvedBackgroundHeaderShapes() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val backgroundCurve = Path().apply {
            moveTo(width * 0.0f, height * 0.4f)
            quadraticTo(
                x1 = width * 0.4f,
                y1 = height * 0.6f,
                x2 = width * 1.1f,
                y2 = height * 0.2f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        val foregroundCurve = Path().apply {
            moveTo(width * -0.2f, height * 0.5f)
            quadraticTo(
                x1 = width * 0.3f,
                y1 = height * 0.8f,
                x2 = width * 1.1f,
                y2 = height * 0.3f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = backgroundCurve,
            color = Color(0xFF2E7D32).copy(alpha = 0.5f)
        )

        drawPath(
            path = foregroundCurve,
            color = Color(0xFF2E7D32)
        )
    }
}

@Composable
fun BookProgressCard(
    modifier: Modifier = Modifier,
    bookCoverResId: Int,
    title: String,
    progress: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground) // Use the consistent card color
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = bookCoverResId),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = progress,
                color = SubtleTextColor,
                fontSize = 14.sp
            )
        }
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