package com.example.reader.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun BadgedIcon(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.TopEnd
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )

        // Small green dot badge
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .align(Alignment.TopEnd)
        )
    }
}
/**
 * Gets appropriate greeting based on current time
 */
@Composable
fun getTimeBasedGreeting(): String {
    return remember {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..20 -> "Good evening"
            else -> "Good night"
        }
    }
}
//Greeting section with user name and time-based greeting
@Composable
fun GreetingSection(
    userName: String,
    modifier: Modifier = Modifier
) {
    val greeting = getTimeBasedGreeting()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "Hi, $userName",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = greeting,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}