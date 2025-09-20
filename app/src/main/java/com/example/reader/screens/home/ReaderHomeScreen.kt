package com.example.reader.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reader.R
import com.example.reader.data.Book
import com.example.reader.ui.theme.CardBackground
import com.example.reader.ui.theme.GreenPrimary
import com.example.reader.ui.theme.SubtleTextColor
import com.example.reader.ui.theme.TextColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController) {
    Scaffold(
        topBar = {
            HomeTopBar()
        },
        bottomBar = {
            BottomNavigationBar()
        },
        containerColor = MaterialTheme.colorScheme.background// Set the main background color here
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Use the Canvas-backed header section so the curves render
            BookFinderSection()
            Spacer(modifier = Modifier.height(8.dp))
            CategoryTabs()
            Spacer(modifier = Modifier.height(8.dp))
            BookGridSection()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    userName: String? = null,
    onNotificationsClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {}
) {
    // Compute an immediate name so we never show "Loading"
    val auth = FirebaseAuth.getInstance()
    val initialName = remember(userName, auth.currentUser) {
        userName ?: auth.currentUser?.displayName?.takeIf { it.isNotBlank() }
        ?: auth.currentUser?.email?.substringBefore('@')
        ?: "Reader"
    }
    var resolvedName by remember { mutableStateOf(initialName) }

    LaunchedEffect(initialName) {
        val currentUser = auth.currentUser
        if (currentUser == null) return@LaunchedEffect
        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.uid)
                .get()
                .await()
            if (snapshot.exists()) {
                val fromDoc = snapshot.getString("displayName")
                    ?: snapshot.getString("username")
                    ?: snapshot.getString("name")
                if (!fromDoc.isNullOrBlank() && fromDoc != resolvedName) {
                    resolvedName = fromDoc
                }
            }
        } catch (_: Exception) {
            // Ignore failures and keep the immediate fallback
        }
    }

    // Material 3 Top App Bar automatically handles status bar insets.
    TopAppBar(
        title = {
            // Greeting placed next to the avatar (start-aligned title)
            Column {
                GreetingSection(resolvedName)
            }
        },
        navigationIcon = {
            // Avatar icon on the left
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "User avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                tint = SubtleTextColor
            )
        },
        actions = {
            IconButton(onClick = onNotificationsClick) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = SubtleTextColor
                )
            }
            IconButton(onClick = onMessagesClick) {
                Icon(
                    imageVector = Icons.Filled.Bookmark,
                    contentDescription = "Messages",
                    tint = SubtleTextColor
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = TextColor,
            navigationIconContentColor = SubtleTextColor,
            actionIconContentColor = SubtleTextColor
        )
    )
}




@Composable
fun BookGridSection() {
    val books = remember {
        mutableStateListOf(
            Book(
                id = 1,
                title = "The trials of Apollo",
                author = "Rick Riordan",
                genre = "Greek Mythology, Fantasy",
                price = "$69",
                salePrice = "$138",
                rating = 4.4f,
                coverImageRes = R.drawable.person, // placeholder cover
                salePercentage = "50% Off",
                isFavorite = true
            ),
            Book(
                id = 2,
                title = "Sun Tzu - The Art of...",
                author = "Sun Tzu",
                genre = "Strategic, Fantasy",
                price = "$72",
                rating = 4.4f,
                coverImageRes = R.drawable.person, // placeholder cover
                isFavorite = false
            ),
            Book(
                id = 3,
                title = "The Art of War",
                author = "Sun Tzu",
                genre = "Strategic",
                price = "$72",
                rating = 4.4f,
                coverImageRes = R.drawable.person, // placeholder cover
                isFavorite = false
            )
        )
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(books) { book ->
            BookCard(book = book, onFavoriteToggle = {
                // This logic allows the favorite state to update visually
                val index = books.indexOf(book)
                if (index != -1) {
                    books[index] = book.copy(isFavorite = !book.isFavorite)
                }
            })
        }
    }
}


@Composable
fun BookCard(book: Book, onFavoriteToggle: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(310.dp), // Adjusted dimensions
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = book.coverImageRes),
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Sale badge with updated styling
                    book.salePercentage?.let { sale ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GreenPrimary)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = sale,
                                color = TextColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } ?: Spacer(modifier = Modifier.weight(1f)) // Add a spacer if no badge

                    // Favorite button
                    IconButton(onClick = onFavoriteToggle) {
                        Icon(
                            imageVector = if (book.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (book.isFavorite) Color.Red else TextColor
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = book.title,
                        color = TextColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = book.author,
                        color = SubtleTextColor,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Price with strikethrough for sale price
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = book.price,
                            color = TextColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        book.salePrice?.let { sale ->
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                                        append(sale)
                                    }
                                },
                                color = SubtleTextColor,
                                fontSize = 12.sp,
                            )
                        }
                    }

                    // Rating with a star icon
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = book.rating.toString(),
                            color = SubtleTextColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background, // Match top bar/background color
        tonalElevation = 0.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        val items = listOf(
            "Home" to Icons.Filled.Home,
            "Explore" to Icons.Filled.Search,
            "Download" to Icons.Filled.Download,
            "Saved" to Icons.Filled.Bookmark,
            "Profile" to Icons.Filled.Person
        )
        var selectedItem by remember { mutableStateOf(0) }

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                icon = {
                    Icon(
                        imageVector = item.second,
                        contentDescription = item.first,
                        modifier = Modifier.size(28.dp) // slightly larger icons
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GreenPrimary,
                    unselectedIconColor = SubtleTextColor,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}