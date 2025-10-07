package com.example.reader.navigation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reader.R
import com.example.reader.screens.explore.ExploreScreen
import com.example.reader.screens.SavedScreen
import com.example.reader.screens.home.Home
import com.example.reader.screens.stats.StatsScreen
import com.example.reader.ui.theme.GreenMid
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
        BottomNavScreen("Home", vectorRes = R.drawable.line_md__home_md),
        BottomNavScreen("Explore", vectorRes = R.drawable.line_md__search),
        BottomNavScreen("Saved", vectorRes = R.drawable.icon_park_outline__bookmark),
        BottomNavScreen("Profile", vectorRes = R.drawable.line_md__person_twotone)
    )

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { screens.size }
    )
    val coroutineScope = rememberCoroutineScope()

    Scaffold(modifier = Modifier
        .fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .height(56.dp)
                    .padding(vertical = 0.dp),
                containerColor = MaterialTheme.colorScheme.background
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
                            if (screen.icon != null) {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.label,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else if (screen.vectorRes != null) {
                                Icon(
                                    painter = painterResource(id = screen.vectorRes),
                                    contentDescription = screen.label,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
//                        label = {
//                            Text(
//                                text = screen.label,
//                                fontSize = MaterialTheme.typography.labelMedium.fontSize,
//                                fontWeight = if (pagerState.currentPage == index) FontWeight.ExtraBold else FontWeight.Medium,
//                            )
//                        },
//                        alwaysShowLabel = false,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GreenPrimary,
                            unselectedIconColor = SubtleTextColor,
                            indicatorColor = GreenMid.copy(alpha = 0.2f), // Background container color for selected icon
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
    val icon: ImageVector? = null,
    val vectorRes: Int? = null
)