package com.example.reader.navigation

enum class ReaderScreens {
    SplashScreen,
    LoginScreen,
    CreateAccountScreen,
    OnBoardingScreen,
    HomeScreen,
    DetailScreen,
    SearchScreen,
    UpdateScreen,
    StatsScreen,
    ExploreScreen,
    SavedScreen,
    AboutScreen,
    YourFeedbackScreen, // added
    ViewFeedbackScreen, // view saved feedback
    ChangePasswordScreen; // new

    companion object {
        fun fromRoute(route: String?): ReaderScreens
                = when (route?.substringBefore("/")) {
            SplashScreen.name -> SplashScreen
            LoginScreen.name -> LoginScreen
            CreateAccountScreen.name -> CreateAccountScreen
            OnBoardingScreen.name -> OnBoardingScreen
            HomeScreen.name -> HomeScreen
            DetailScreen.name -> DetailScreen
            SearchScreen.name -> SearchScreen
            UpdateScreen.name -> UpdateScreen
            StatsScreen.name -> StatsScreen
            ExploreScreen.name -> ExploreScreen
            SavedScreen.name -> SavedScreen
            AboutScreen.name -> AboutScreen
            YourFeedbackScreen.name -> YourFeedbackScreen
            ViewFeedbackScreen.name -> ViewFeedbackScreen
            ChangePasswordScreen.name -> ChangePasswordScreen
            null -> HomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized.")
        }
    }
}