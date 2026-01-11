package com.example.reader.utils

import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver

/**
 * Accessibility utilities for improving app usability.
 * Provides semantic properties and helper functions.
 */

/**
 * Custom semantic property for reading progress.
 */
val ReadingProgressKey = SemanticsPropertyKey<Float>("ReadingProgress")
var SemanticsPropertyReceiver.readingProgress by ReadingProgressKey

/**
 * Custom semantic property for book rating.
 */
val BookRatingKey = SemanticsPropertyKey<Float>("BookRating")
var SemanticsPropertyReceiver.bookRating by BookRatingKey

/**
 * Custom semantic property for favorite status.
 */
val IsFavoriteKey = SemanticsPropertyKey<Boolean>("IsFavorite")
var SemanticsPropertyReceiver.isFavorite by IsFavoriteKey

/**
 * Helper to generate accessible book card descriptions.
 *
 * @param title Book title
 * @param author Book author
 * @param rating Book rating
 * @param isFavorite Whether book is favorited
 * @return Formatted accessibility description
 */
fun generateBookCardAccessibilityDescription(
    title: String,
    author: String,
    rating: Double,
    isFavorite: Boolean
): String {
    val favoriteText = if (isFavorite) "Favorited. " else ""
    val ratingText = if (rating > 0) "Rating: ${"%.1f".format(rating)} stars. " else "No rating. "
    return "${favoriteText}Book: $title by $author. $ratingText Double tap to view details."
}

/**
 * Helper to generate accessible button descriptions.
 *
 * @param action Action name (e.g., "favorite", "share")
 * @param isEnabled Whether button is enabled
 * @param currentState Current state (e.g., "favorited", "not favorited")
 * @return Formatted accessibility description
 */
fun generateButtonAccessibilityDescription(
    action: String,
    isEnabled: Boolean = true,
    currentState: String? = null
): String {
    val stateText = currentState?.let { "$it. " } ?: ""
    val actionText = if (isEnabled) "Tap to $action" else "$action unavailable"
    return "$stateText$actionText"
}

/**
 * Helper to announce reading progress to screen readers.
 *
 * @param currentPage Current page number
 * @param totalPages Total pages
 * @return Formatted accessibility announcement
 */
fun generateProgressAccessibilityDescription(
    currentPage: Int,
    totalPages: Int
): String {
    val percentage = if (totalPages > 0) {
        ((currentPage.toFloat() / totalPages) * 100).toInt()
    } else {
        0
    }
    return "Reading progress: $percentage percent complete. Page $currentPage of $totalPages."
}

/**
 * Minimum touch target size for accessibility (48dp recommended by Material Design).
 */
const val MIN_TOUCH_TARGET_DP = 48

/**
 * Check if contrast ratio meets WCAG AA standards (4.5:1 for normal text).
 * Simplified calculation - for production, use a proper color contrast library.
 *
 * @param foreground Foreground color (text)
 * @param background Background color
 * @return true if contrast is sufficient
 */
fun hasMinimumContrast(
    foreground: androidx.compose.ui.graphics.Color,
    background: androidx.compose.ui.graphics.Color
): Boolean {
    // Simplified luminance calculation
    val fgLuminance = calculateRelativeLuminance(foreground)
    val bgLuminance = calculateRelativeLuminance(background)

    val lighter = maxOf(fgLuminance, bgLuminance)
    val darker = minOf(fgLuminance, bgLuminance)

    val contrastRatio = (lighter + 0.05f) / (darker + 0.05f)

    // WCAG AA standard for normal text
    return contrastRatio >= 4.5f
}

/**
 * Calculate relative luminance of a color.
 * Uses the WCAG formula.
 */
private fun calculateRelativeLuminance(color: androidx.compose.ui.graphics.Color): Float {
    fun adjust(channel: Float): Float {
        return if (channel <= 0.03928f) {
            channel / 12.92f
        } else {
            Math.pow(((channel + 0.055) / 1.055).toDouble(), 2.4).toFloat()
        }
    }

    val r = adjust(color.red)
    val g = adjust(color.green)
    val b = adjust(color.blue)

    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}

/**
 * Helper to generate screen navigation announcements.
 *
 * @param screenName Name of the screen
 * @param additionalInfo Optional additional context
 * @return Formatted accessibility announcement
 */
fun generateNavigationAccessibilityDescription(
    screenName: String,
    additionalInfo: String? = null
): String {
    val info = additionalInfo?.let { ". $it" } ?: ""
    return "Navigated to $screenName$info"
}

/**
 * Helper to generate accessible text selection descriptions.
 *
 * @param selectedText The text that was selected
 * @param selectionStart Start position
 * @param selectionEnd End position
 * @return Formatted accessibility announcement
 */
fun generateTextSelectionAccessibilityDescription(
    selectedText: String,
    selectionStart: Int,
    selectionEnd: Int
): String {
    val wordCount = selectedText.split("\\s+".toRegex()).size
    return "Selected $wordCount ${if (wordCount == 1) "word" else "words"}: $selectedText"
}

/**
 * Helper to generate accessible search result descriptions.
 *
 * @param query Search query
 * @param resultCount Number of results found
 * @param hasFilters Whether filters are applied
 * @return Formatted accessibility announcement
 */
fun generateSearchResultAccessibilityDescription(
    query: String,
    resultCount: Int,
    hasFilters: Boolean = false
): String {
    val filterText = if (hasFilters) " with filters applied" else ""
    val resultText = when (resultCount) {
        0 -> "No results found"
        1 -> "1 result found"
        else -> "$resultCount results found"
    }
    return "$resultText for \"$query\"$filterText"
}

/**
 * Helper to generate accessible loading state descriptions.
 *
 * @param contentType Type of content being loaded (e.g., "books", "chapters")
 * @param isComplete Whether loading is complete
 * @return Formatted accessibility announcement
 */
fun generateLoadingAccessibilityDescription(
    contentType: String,
    isComplete: Boolean
): String {
    return if (isComplete) {
        "$contentType loaded successfully"
    } else {
        "Loading $contentType, please wait"
    }
}

/**
 * Helper to generate accessible error descriptions.
 *
 * @param errorType Type of error
 * @param canRetry Whether retry is available
 * @return Formatted accessibility announcement
 */
fun generateErrorAccessibilityDescription(
    errorType: String,
    canRetry: Boolean = true
): String {
    val retryText = if (canRetry) " Tap to retry." else ""
    return "Error: $errorType.$retryText"
}

/**
 * Helper to generate accessible filter descriptions.
 *
 * @param filterName Name of the filter
 * @param isActive Whether filter is active
 * @param selectedCount Number of selected items (optional)
 * @return Formatted accessibility announcement
 */
fun generateFilterAccessibilityDescription(
    filterName: String,
    isActive: Boolean,
    selectedCount: Int? = null
): String {
    val activeText = if (isActive) "Active" else "Inactive"
    val countText = selectedCount?.let { " ($it selected)" } ?: ""
    return "$filterName filter. $activeText$countText. Double tap to toggle."
}

/**
 * Helper to generate accessible list descriptions.
 *
 * @param itemName Name of the item type (e.g., "book", "chapter")
 * @param position Current position (1-based)
 * @param totalItems Total number of items
 * @return Formatted accessibility announcement
 */
fun generateListItemAccessibilityDescription(
    itemName: String,
    position: Int,
    totalItems: Int
): String {
    return "$itemName $position of $totalItems"
}

/**
 * Helper to generate accessible tab descriptions.
 *
 * @param tabName Name of the tab
 * @param position Current position (1-based)
 * @param totalTabs Total number of tabs
 * @param isSelected Whether tab is currently selected
 * @return Formatted accessibility announcement
 */
fun generateTabAccessibilityDescription(
    tabName: String,
    position: Int,
    totalTabs: Int,
    isSelected: Boolean
): String {
    val selectedText = if (isSelected) "Selected" else "Not selected"
    return "$tabName. Tab $position of $totalTabs. $selectedText. Double tap to activate."
}

/**
 * Helper to generate accessible dialog descriptions.
 *
 * @param dialogTitle Dialog title
 * @param hasActions Whether dialog has action buttons
 * @return Formatted accessibility announcement
 */
fun generateDialogAccessibilityDescription(
    dialogTitle: String,
    hasActions: Boolean = true
): String {
    val actionText = if (hasActions) " Swipe to navigate through options." else ""
    return "Dialog: $dialogTitle.$actionText"
}

/**
 * Helper to generate accessible slider descriptions.
 *
 * @param label Slider label (e.g., "Brightness", "Font size")
 * @param currentValue Current value
 * @param minValue Minimum value
 * @param maxValue Maximum value
 * @param unit Unit of measurement (optional)
 * @return Formatted accessibility announcement
 */
fun generateSliderAccessibilityDescription(
    label: String,
    currentValue: Float,
    minValue: Float,
    maxValue: Float,
    unit: String? = null
): String {
    val percentage = ((currentValue - minValue) / (maxValue - minValue) * 100).toInt()
    val unitText = unit?.let { " $it" } ?: ""
    return "$label: ${currentValue.toInt()}$unitText. $percentage percent. Swipe up or down to adjust."
}

/**
 * Helper to generate accessible badge descriptions.
 *
 * @param badgeText Badge text or count
 * @param parentDescription Parent element description
 * @return Formatted accessibility announcement
 */
fun generateBadgeAccessibilityDescription(
    badgeText: String,
    parentDescription: String
): String {
    return "$parentDescription. $badgeText notifications."
}

/**
 * Helper to generate accessible chip descriptions.
 *
 * @param chipLabel Chip label
 * @param isSelected Whether chip is selected
 * @param isDismissible Whether chip can be dismissed
 * @return Formatted accessibility announcement
 */
fun generateChipAccessibilityDescription(
    chipLabel: String,
    isSelected: Boolean = false,
    isDismissible: Boolean = false
): String {
    val selectedText = if (isSelected) "Selected. " else ""
    val dismissText = if (isDismissible) " Swipe right to dismiss." else ""
    return "${selectedText}$chipLabel chip. Double tap to toggle.$dismissText"
}

