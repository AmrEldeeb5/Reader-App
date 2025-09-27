package com.example.reader.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.reader.navigation.ReaderScreens
import com.example.reader.ui.theme.GreenPrimary
import com.example.reader.ui.theme.SubtleTextColor
import com.example.reader.ui.theme.TextColor
import java.util.Calendar

@Composable
fun BadgedIcon(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(24.dp) // Smaller size
            .clickable { onClick() },
        contentAlignment = Alignment.TopEnd
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp), // Smaller icon
            tint = MaterialTheme.colorScheme.onBackground
        )

        // Small green dot badge
        Box(
            modifier = Modifier
                .size(6.dp) // Smaller badge
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

// Greeting section with user name and time-based greeting
@Composable
fun GreetingSection(
    userName: String,
    modifier: Modifier = Modifier
) {
    val greeting = getTimeBasedGreeting()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(1.dp) // Tighter spacing
    ) {
        Text(
            text = "Hi, $userName",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp // Slightly smaller
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = greeting,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp // Smaller subtitle
            ),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun CategoryTabs(modifier: Modifier = Modifier) {
    val categories = listOf("Novels", "Self Love", "Science", "Romance")
    var selectedCategory by remember { mutableStateOf("Novels") }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            // KEY CHANGE: Use a Box to create the background for the selected item
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) GreenPrimary else MaterialTheme.colorScheme.surface)
                    .clickable { selectedCategory = category }
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    color = if (isSelected) TextColor else SubtleTextColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String? = null
) {
    // Get current route if not provided
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val resolvedCurrentRoute = currentRoute ?: navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        val items = listOf(
            NavigationItem("Home", Icons.Filled.Home, ReaderScreens.ReaderHomeScreen.name),
            NavigationItem("Explore", Icons.Filled.Search, ReaderScreens.ExploreScreen.name),
            NavigationItem("Saved", Icons.Filled.Bookmark, ReaderScreens.SavedScreen.name),
            NavigationItem("Profile", Icons.Filled.Person, ReaderScreens.ReaderStatsScreen.name)
        )

        items.forEach { item ->
            NavigationBarItem(
                selected = resolvedCurrentRoute == item.route,
                onClick = {
                    if (resolvedCurrentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Pop up to the start destination to avoid building up a large stack
                            popUpTo(ReaderScreens.ReaderHomeScreen.name) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when re-selecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GreenPrimary,
                    unselectedIconColor = SubtleTextColor,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

// Navigation item data class
data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)