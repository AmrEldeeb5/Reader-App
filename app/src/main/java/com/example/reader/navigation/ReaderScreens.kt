package com.example.reader.navigation

enum class ReaderScreens {
    SplashScreen,
    LoginScreen,
    CreateAccountScreen,
    OnBoardingScreen,
    ReaderHomeScreen,
    DetailScreen,
    SearchScreen,
    UpdateScreen,
    ReaderStatsScreen,
    ExploreScreen,    // Add this
    SavedScreen;      // Add this

    companion object {
        fun fromRoute(route: String?): ReaderScreens
                = when (route?.substringBefore("/")) {
            SplashScreen.name -> SplashScreen
            LoginScreen.name -> LoginScreen
            CreateAccountScreen.name -> CreateAccountScreen
            OnBoardingScreen.name -> OnBoardingScreen
            ReaderHomeScreen.name -> ReaderHomeScreen
            DetailScreen.name -> DetailScreen
            SearchScreen.name -> SearchScreen
            UpdateScreen.name -> UpdateScreen
            ReaderStatsScreen.name -> ReaderStatsScreen
            ExploreScreen.name -> ExploreScreen
            SavedScreen.name -> SavedScreen
            null -> ReaderHomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized.")
        }
    }
}