package com.example.reader.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
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
import androidx.compose.ui.draw.rotate
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
fun CategoryTabs(modifier: Modifier = Modifier, isDarkTheme: Boolean) {
    val categories = listOf("Novels", "Self Love", "Science", "Romance")
    var selectedCategory by remember { mutableStateOf("Novels") }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .clickable { selectedCategory = category }
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
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
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(32.dp)
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

@Composable
fun FunThemeToggleCompact(
    isDark: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isDark) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_rotation"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF1A1B3A) else Color(0xFF3FAF9E).copy(alpha = 0.3f),
        animationSpec = tween(300),
        label = "background_color"
    )

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onToggle(!isDark) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isDark) Icons.Filled.DarkMode else Icons.Filled.LightMode,
            contentDescription = if (isDark) "Switch to Light Mode" else "Switch to Dark Mode",
            tint = if (isDark) Color.White else Color(0xFF4A4A4A),
            modifier = Modifier
                .size(24.dp)
                .rotate(rotationAngle)
        )
    }
}