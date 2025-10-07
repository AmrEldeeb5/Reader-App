package com.example.reader.navigation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reader.screens.explore.ExploreScreen
import com.example.reader.screens.SavedScreen
import com.example.reader.screens.home.Home
import com.example.reader.screens.stats.StatsScreen
import com.example.reader.ui.theme.GreenPrimary
import com.example.reader.ui.theme.SubtleTextColor
import kotlinx.coroutines.launch

@Composable
fun SwipeableBottomNavigation(
    navController: NavController,
    isDarkTheme: Boolean = false,
    onThemeToggle: (Boolean) -> Unit = {},
    initialPage: Int = 0
) {
    // Define the screens that are part of bottom navigation
    val screens = listOf(
        BottomNavScreen("Home", Icons.Filled.Home),
        BottomNavScreen("Explore", Icons.Filled.Search),
        BottomNavScreen("Saved", Icons.Filled.Bookmark),
        BottomNavScreen("Profile", Icons.Filled.Person)
    )

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { screens.size }
    )
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                screens.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.label,
                                modifier = Modifier.size(32.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.label,
                                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                fontWeight = FontWeight.Medium,
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GreenPrimary,
                            unselectedIconColor = SubtleTextColor,
                            indicatorColor = Color.Transparent,
                            selectedTextColor = GreenPrimary,
                            unselectedTextColor = SubtleTextColor
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            userScrollEnabled = true, // Enable swipe gestures
            beyondViewportPageCount = 1 // Changed from beyondBoundsPageCount
        ) { page ->
            when (page) {
                0 -> Home(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle
                )
                1 -> ExploreScreen(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle
                )
                2 -> SavedScreen(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle
                )
                3 -> StatsScreen(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle
                )
            }
        }
    }
}

// Data class for bottom navigation items
data class BottomNavScreen(
    val label: String,
    val icon: ImageVector
)