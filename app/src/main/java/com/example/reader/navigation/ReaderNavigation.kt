package com.example.reader.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reader.screens.onboarding.OnBoardingScreen
import com.example.reader.screens.SignUp.SignUpScreen
import com.example.reader.screens.home.Home
import com.example.reader.screens.login.ReaderLoginScreen
import com.example.reader.screens.screen.SplashScreen

@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = ReaderScreens.SplashScreen.name) {

        composable(ReaderScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }
        composable(ReaderScreens.OnBoardingScreen.name) {
             OnBoardingScreen(navController = navController)
        }
        composable(ReaderScreens.LoginScreen.name) {
            ReaderLoginScreen(navController = navController, onLoginClick = { email, password -> })
        }
        composable(ReaderScreens.CreateAccountScreen.name) {
            SignUpScreen(navController = navController, onSignUpClick = { name, email, password -> })
        }
        composable(ReaderScreens.ReaderHomeScreen.name) {
            Home(navController = navController)
        }
    }
}

