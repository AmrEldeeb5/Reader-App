package com.example.reader.domain.model

/**
 * Filter state for book lists.
 */
data class BookFilters(
    val minRating: Double? = null,  // Minimum rating (e.g., 4.0 for 4+ stars)
    val dateFilter: DateFilter = DateFilter.ALL_TIME,
    val selectedCategories: Set<String> = emptySet(),
    val sortBy: SortBy = SortBy.RELEVANCE
)

/**
 * Date filter options.
 */
enum class DateFilter {
    ALL_TIME,
    THIS_YEAR,
    THIS_MONTH
}

/**
 * Sort options for book lists.
 */
enum class SortBy {
    RELEVANCE,      // Default - most relevant
    RATING_DESC,    // Highest rated first
    RATING_ASC,     // Lowest rated first
    TITLE_ASC,      // A to Z
    TITLE_DESC,     // Z to A
    DATE_DESC,      // Newest first
    DATE_ASC        // Oldest first
}

