# Realm DB Implementation Guide

## Overview
This document explains the Realm DB implementation in the Reader App for persisting:
- User feedback with sentiment ratings
- Favorite books
- User profile information (username and email)

## What Has Been Implemented

### 1. Realm Database Setup

#### Dependencies Added
- **Realm Kotlin**: `io.realm.kotlin:library-base:1.16.0`
- Added to `build.gradle.kts` with the Realm plugin

#### Database Models (`RealmModels.kt`)
Three Realm object models have been created:

1. **FeedbackRealm**
   - Stores user feedback submissions
   - Fields: feedback text, sentiment index (0-3), timestamp
   
2. **FavoriteBookRealm**
   - Stores favorite books persistently
   - Fields: book ID, title, author, subtitle, rating, cover image URL, user rating, description
   
3. **UserProfileRealm**
   - Stores user profile information
   - Fields: username, email, last updated timestamp

#### Database Configuration (`RealmDatabase.kt`)
- Singleton pattern for database access
- Configured with schema version 1
- Database file: `reader_app.realm`

### 2. Repository Layer (`RealmRepository.kt`)

The repository provides methods for:

**Feedback Operations:**
- `saveFeedback()` - Save new feedback
- `getAllFeedback()` - Retrieve all feedback (Flow)
- `deleteFeedback()` - Delete specific feedback

**Favorite Books Operations:**
- `saveFavoriteBook()` - Add book to favorites
- `removeFavoriteBook()` - Remove book from favorites
- `updateBookRating()` - Update user's rating for a book
- `getAllFavoriteBooks()` - Get all favorites as Flow<List<Book>>
- `isFavorite()` - Check if a book is favorited

**User Profile Operations:**
- `saveUserProfile()` - Save/update username and email
- `getUserProfile()` - Get profile as Flow
- `getUserProfileOnce()` - Get profile once (suspend function)

### 3. ViewModels Updated

#### FavoritesViewModel
- **Updated** to use Realm DB for persistent storage
- Constructor now takes `RealmRepository`
- Favorites are loaded from Realm on initialization
- All favorite operations (add/remove/update rating) are persisted to Realm
- Uses Flow to automatically update UI when database changes

#### FeedbackViewModel (New)
- Manages feedback submissions
- Loads all saved feedback
- Provides delete functionality

#### UserProfileViewModel
- **Updated** to include Realm DB storage
- Constructor now takes both `UserPreferences` and `RealmRepository`
- Username is saved to multiple locations for redundancy:
  1. Realm DB (local persistent storage)
  2. UserPreferences (encrypted SharedPreferences)
  3. Firebase Firestore (cloud backup)
- Fallback hierarchy ensures username is always available

### 4. UI Screens

#### YourFeedbackScreen (Updated)
- Now saves feedback to Realm DB when submitted
- Uses `FeedbackViewModel` to persist data
- Shows confirmation message after saving

#### ViewFeedbackScreen (New)
- Displays history of all submitted feedback
- Shows sentiment icon, rating, text, and timestamp
- Allows deletion of individual feedback items
- Empty state when no feedback exists

#### SavedScreen (Existing - Enhanced)
- Already displays favorite books
- Now powered by Realm DB for persistence
- Favorites survive app restarts

### 5. Dependency Injection

#### AppModule
- Added `RealmRepository` as singleton

#### ViewModelModule
- Updated `FavoritesViewModel` to receive `RealmRepository`
- Added `FeedbackViewModel` with `RealmRepository`
- Updated `UserProfileViewModel` to receive both `UserPreferences` and `RealmRepository`

### 6. Application Initialization

#### MyApplication.kt
- Realm database initialized in `onCreate()`
- Happens before Koin initialization

## Navigation

New screen added to navigation:
- **ViewFeedbackScreen** - Route: `ReaderScreens.ViewFeedbackScreen`

## How to Use

### Saving Feedback
```kotlin
// In any composable with FeedbackViewModel injected
feedbackViewModel.saveFeedback("Great app!", sentimentIndex = 3)
```

### Managing Favorites
```kotlin
// In any composable with FavoritesViewModel injected
favoritesViewModel.addFavorite(book)
favoritesViewModel.removeFavorite(bookId)
favoritesViewModel.updateUserRating(bookId, 4.5)
```

### Updating Username
```kotlin
// In any composable with UserProfileViewModel injected
userProfileViewModel.updateUsername("John Doe")
```

### Observing Data
All data is exposed as StateFlow, making it easy to observe in Composables:

```kotlin
val favoriteBooks by favoritesViewModel.favoriteBooks.collectAsState()
val feedbackList by feedbackViewModel.feedbackList.collectAsState()
val username by userProfileViewModel.username.collectAsState()
```

## Benefits

1. **Persistence**: All data survives app restarts
2. **Offline Support**: Works without internet connection
3. **Reactive**: UI automatically updates when data changes (Flow/StateFlow)
4. **Type-Safe**: Kotlin-first approach with compile-time safety
5. **Performance**: Efficient queries and lazy loading
6. **Redundancy**: User data saved to multiple locations (Realm, SharedPreferences, Firebase)

## Data Storage Locations

### User Profile
- **Primary**: Realm DB (local, fast access)
- **Backup**: Encrypted SharedPreferences
- **Cloud**: Firebase Firestore (if user is logged in)

### Favorites
- **Storage**: Realm DB only
- **Synced**: Automatically across all screens via singleton ViewModel

### Feedback
- **Storage**: Realm DB only
- **Accessible**: Via ViewFeedbackScreen

## Future Enhancements

Potential improvements:
1. Add Firebase sync for favorites (cross-device sync)
2. Export feedback to CSV/JSON
3. Add statistics dashboard for reading habits
4. Implement data migration from Room to Realm (if needed)
5. Add search/filter for feedback history
6. Backup/restore functionality

## Testing

To verify the implementation:
1. Add books to favorites → Close app → Reopen → Favorites should persist
2. Submit feedback → Navigate to ViewFeedbackScreen → See saved feedback
3. Update username → Close app → Reopen → Username should persist

## Database Location

The Realm database file is stored at:
```
/data/data/com.example.reader/files/reader_app.realm
```

You can inspect it using Realm Studio for debugging.

## Notes

- Realm operations are performed on background threads (suspend functions)
- UI updates happen automatically via Flow
- Database is initialized once at app startup
- All ViewModels use the same singleton RealmRepository instance
- Favorites ViewModel is also a singleton to maintain state across navigation

