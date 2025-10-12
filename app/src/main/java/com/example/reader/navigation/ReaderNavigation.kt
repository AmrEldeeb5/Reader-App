package com.example.reader.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.reader.screens.onboarding.OnBoardingScreen
import com.example.reader.screens.SignUp.SignUpScreen
import com.example.reader.screens.details.BookDetailsScreen
import com.example.reader.screens.login.ReaderLoginScreen
import com.example.reader.screens.screen.SplashScreen
import com.example.reader.screens.profile.AboutScreen
import com.example.reader.screens.profile.YourFeedbackScreen
import com.example.reader.screens.profile.ChangePasswordScreen

@Composable
fun ReaderNavigation(
    isDarkTheme: Boolean = false,
    onThemeToggle: (Boolean) -> Unit = {},
    isGreenTheme: Boolean = true,
    onColorSchemeToggle: (Boolean) -> Unit = {}
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ReaderScreens.SplashScreen.name
    ) {
        composable(ReaderScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }
        composable(ReaderScreens.OnBoardingScreen.name) {
            OnBoardingScreen(navController = navController)
        }
        composable(ReaderScreens.LoginScreen.name) {
            ReaderLoginScreen(
                navController = navController,
                onLoginClick = { email, password -> },
                isGreenTheme = isGreenTheme
            )
        }
        composable(ReaderScreens.CreateAccountScreen.name) {
            SignUpScreen(
                navController = navController,
                onSignUpClick = { name, email, password -> },
                isGreenTheme = isGreenTheme
            )
        }
        // Detail screen with argument
        composable(
            route = ReaderScreens.DetailScreen.name + "/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId")
            BookDetailsScreen(navController = navController, bookId = bookId)
        }

        // Main app screens with bottom navigation
        composable(ReaderScreens.ReaderHomeScreen.name) {
            SwipeableBottomNavigation(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                isGreenTheme = isGreenTheme,
                onColorSchemeToggle = onColorSchemeToggle,
                initialPage = 0
            )
        }
        composable(ReaderScreens.ExploreScreen.name) {
            SwipeableBottomNavigation(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                isGreenTheme = isGreenTheme,
                onColorSchemeToggle = onColorSchemeToggle,
                initialPage = 1
            )
        }
        composable(ReaderScreens.SavedScreen.name) {
            SwipeableBottomNavigation(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                isGreenTheme = isGreenTheme,
                onColorSchemeToggle = onColorSchemeToggle,
                initialPage = 2
            )
        }
        composable(ReaderScreens.ReaderStatsScreen.name) {
            SwipeableBottomNavigation(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                isGreenTheme = isGreenTheme,
                onColorSchemeToggle = onColorSchemeToggle,
                initialPage = 3
            )
        }
        // About screen route
        composable(ReaderScreens.AboutScreen.name) {
            AboutScreen(navController = navController)
        }
        // Your Feedback route
        composable(ReaderScreens.YourFeedbackScreen.name) {
            YourFeedbackScreen(navController = navController)
        }
        // Change Password route
        composable(ReaderScreens.ChangePasswordScreen.name) {
            ChangePasswordScreen(navController = navController)
        }
    }
}