package com.example.reader.utils

import android.os.Bundle
import android.util.Log
import com.example.reader.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized logging utility for the app.
 *
 * Handles both debug logging and production crash reporting through Firebase.
 */
@Singleton
class AppLogger @Inject constructor() {

    private val crashlytics: FirebaseCrashlytics by lazy {
        FirebaseCrashlytics.getInstance()
    }

    companion object {
        private const val TAG = "ReaderApp"
    }

    /**
     * Log an error with optional exception.
     * In debug: logs to Logcat
     * In production: sends to Firebase Crashlytics
     */
    fun logError(message: String, throwable: Throwable? = null, tag: String = TAG) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, throwable)
        }

        crashlytics.log("ERROR: $message")
        throwable?.let {
            crashlytics.recordException(it)
        }
    }

    /**
     * Log a warning message.
     */
    fun logWarning(message: String, tag: String = TAG) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message)
        }

        crashlytics.log("WARNING: $message")
    }

    /**
     * Log an info message.
     */
    fun logInfo(message: String, tag: String = TAG) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message)
        }

        crashlytics.log("INFO: $message")
    }

    /**
     * Log a debug message (only in debug builds).
     */
    fun logDebug(message: String, tag: String = TAG) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    /**
     * Set user ID for crash reports.
     */
    fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }

    /**
     * Set custom key for crash reports.
     */
    fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    /**
     * Set custom key for crash reports (boolean).
     */
    fun setCustomKey(key: String, value: Boolean) {
        crashlytics.setCustomKey(key, value)
    }

    /**
     * Set custom key for crash reports (int).
     */
    fun setCustomKey(key: String, value: Int) {
        crashlytics.setCustomKey(key, value)
    }

    /**
     * Force a crash (for testing).
     * Only works in debug builds.
     */
    fun forceCrash(message: String = "Test crash") {
        if (BuildConfig.DEBUG) {
            throw RuntimeException(message)
        }
    }
}

/**
 * Analytics manager for tracking user events.
 */
@Singleton
class AnalyticsManager @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {

    /**
     * Log a book view event.
     */
    fun logBookView(bookId: String, title: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, bookId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, title)
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "book")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    /**
     * Log a search event.
     */
    fun logSearch(query: String, resultCount: Int) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, query)
            putInt("result_count", resultCount)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
    }

    /**
     * Log a favorite action.
     */
    fun logFavoriteAction(bookId: String, added: Boolean) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, bookId)
            putString("action", if (added) "add" else "remove")
        }
        firebaseAnalytics.logEvent("favorite_action", bundle)
    }

    /**
     * Log a category selection.
     */
    fun logCategorySelected(category: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category)
        }
        firebaseAnalytics.logEvent("category_selected", bundle)
    }

    /**
     * Log a rating action.
     */
    fun logBookRating(bookId: String, rating: Double) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, bookId)
            putDouble("rating", rating)
        }
        firebaseAnalytics.logEvent("book_rated", bundle)
    }

    /**
     * Log screen view.
     */
    fun logScreenView(screenName: String, screenClass: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    /**
     * Log user login.
     */
    fun logLogin(method: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

    /**
     * Log user signup.
     */
    fun logSignUp(method: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
    }

    /**
     * Set user property.
     */
    fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
    }
}

/**
 * Analytics event names.
 */
object AnalyticsEvents {
    const val BOOK_VIEWED = "book_viewed"
    const val BOOK_FAVORITED = "book_favorited"
    const val BOOK_UNFAVORITED = "book_unfavorited"
    const val BOOK_RATED = "book_rated"
    const val BOOK_SEARCH = "book_search"
    const val CATEGORY_SELECTED = "category_selected"
    const val SCREEN_VIEW = "screen_view"

    // Screen names
    const val SCREEN_HOME = "home"
    const val SCREEN_EXPLORE = "explore"
    const val SCREEN_FAVORITES = "favorites"
    const val SCREEN_PROFILE = "profile"
    const val SCREEN_BOOK_DETAILS = "book_details"
    const val SCREEN_LOGIN = "login"
    const val SCREEN_SIGNUP = "signup"
}

