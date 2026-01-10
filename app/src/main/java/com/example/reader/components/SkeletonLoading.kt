package com.example.reader.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.reader.ui.theme.CornerRadius
import com.example.reader.ui.theme.Spacing

/**
 * Shimmer effect brush for loading skeletons.
 *
 * Creates an animated gradient that moves across the component.
 */
@Composable
fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 500, translateAnim - 500),
        end = Offset(translateAnim, translateAnim)
    )
}

/**
 * Skeleton loading placeholder for a book card.
 */
@Composable
fun BookCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(CornerRadius.md),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book cover skeleton
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(110.dp)
                    .clip(RoundedCornerShape(CornerRadius.sm))
                    .background(shimmerBrush())
            )

            Spacer(modifier = Modifier.width(Spacing.md))

            // Book info skeleton
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                // Title skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(CornerRadius.xs))
                        .background(shimmerBrush())
                )

                // Author skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(CornerRadius.xs))
                        .background(shimmerBrush())
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                // Rating skeleton
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(CornerRadius.xs))
                        .background(shimmerBrush())
                )

                // Description skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(CornerRadius.xs))
                        .background(shimmerBrush())
                )
            }
        }
    }
}

/**
 * Skeleton loading placeholder for a small book card (grid view).
 */
@Composable
fun BookCardGridSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(130.dp)
            .height(200.dp),
        shape = RoundedCornerShape(CornerRadius.md),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.sm)
        ) {
            // Book cover skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(CornerRadius.sm))
                    .background(shimmerBrush())
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Title skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(CornerRadius.xs))
                    .background(shimmerBrush())
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            // Author skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(CornerRadius.xs))
                    .background(shimmerBrush())
            )
        }
    }
}

/**
 * Skeleton loading list showing multiple book card skeletons.
 *
 * @param count Number of skeleton items to show
 */
@Composable
fun BookListSkeleton(
    count: Int = 5,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        repeat(count) {
            BookCardSkeleton()
        }
    }
}

/**
 * Generic rectangular skeleton placeholder.
 *
 * @param modifier Modifier for customization
 */
@Composable
fun RectangleSkeleton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.xs))
            .background(shimmerBrush())
    )
}

/**
 * Generic circular skeleton placeholder.
 *
 * @param size Size of the circle
 * @param modifier Modifier for customization
 */
@Composable
fun CircleSkeleton(
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(50))
            .background(shimmerBrush())
    )
}

/**
 * Skeleton loading placeholder for book details screen.
 * Shows a placeholder while book data is being fetched.
 */
@Composable
fun BookDetailsSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header with back button skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CircleSkeleton(size = 40.dp)
            CircleSkeleton(size = 40.dp)
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        // Book cover skeleton (large, centered)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xxxl),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(270.dp)
                    .clip(RoundedCornerShape(CornerRadius.md))
                    .background(shimmerBrush())
            )
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Title skeleton
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RectangleSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(28.dp)
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Author skeleton
            RectangleSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(20.dp)
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Rating skeleton
            RectangleSkeleton(
                modifier = Modifier
                    .width(120.dp)
                    .height(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Action buttons skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(2) {
                RectangleSkeleton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .padding(horizontal = Spacing.xs)
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Description section skeleton
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
        ) {
            RectangleSkeleton(
                modifier = Modifier
                    .width(100.dp)
                    .height(20.dp)
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            repeat(5) {
                RectangleSkeleton(
                    modifier = Modifier
                        .fillMaxWidth(if (it == 4) 0.6f else 1f)
                        .height(16.dp)
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
            }
        }
    }
}

/**
 * Skeleton for search results grid.
 * Shows a grid of book card skeletons.
 */
@Composable
fun SearchResultsSkeleton(
    count: Int = 6,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Create rows of 2 items
        for (i in 0 until count step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                BookCardGridSkeleton(modifier = Modifier.weight(1f))
                if (i + 1 < count) {
                    BookCardGridSkeleton(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Skeleton for category section with horizontal scroll.
 */
@Composable
fun CategorySectionSkeleton(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Section title skeleton
        RectangleSkeleton(
            modifier = Modifier
                .width(150.dp)
                .height(24.dp)
                .padding(horizontal = Spacing.md)
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        // Horizontal scrolling books skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Spacer(modifier = Modifier.width(Spacing.xs))
            repeat(4) {
                BookCardGridSkeleton()
            }
            Spacer(modifier = Modifier.width(Spacing.xs))
        }
    }
}
