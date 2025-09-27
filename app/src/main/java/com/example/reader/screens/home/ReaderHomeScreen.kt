package com.example.reader.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.reader.navigation.ReaderScreens
import com.example.reader.ui.theme.CardBackground
import com.example.reader.ui.theme.GreenPrimary
import com.example.reader.ui.theme.SubtleTextColor
import com.example.reader.ui.theme.TextColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            HomeTopBar(navController)
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            BookDiscoveryScreen()
            CategoryTabs(modifier = Modifier.padding(top = 1.dp))
            Spacer(modifier = Modifier.height(16.dp))
            BookGridSection()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(navController: NavController,
    userName: String? = null,
    onNotificationsClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val initialName = remember(userName, auth.currentUser) {
        userName ?: auth.currentUser?.displayName?.takeIf { it.isNotBlank() }
        ?: "Andy" // Changed to match reference
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

    TopAppBar(
        title = {
            Column {
                GreetingSection(resolvedName)
            }
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "User avatar",
                modifier = Modifier.clickable(onClick = {
                    navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                })
                    .size(48.dp)
                    .clip(CircleShape),
                tint = SubtleTextColor
            )
        },
        actions = {
            IconButton(onClick = onNotificationsClick) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = SubtleTextColor,
                    modifier = Modifier.size(24.dp)
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
                title = "The trials of apollo th...", // Match reference text
                author = "Greek Mythology, Fantasy",
                genre = "Greek Mythology, Fantasy",
                rating = 4.4f,
                coverImageRes = R.drawable.person,
                salePercentage = "50% Off",
                isFavorite = true
            ),
            Book(
                id = 2,
                title = "Sun Tzu - The Art of...",
                author = "Strategic, Fantasy",
                genre = "Strategic, Fantasy",
                rating = 4.4f,
                coverImageRes = R.drawable.person,
                isFavorite = false
            ),
            Book(
                id = 3,
                title = "The Art of War",
                author = "Strategic",
                genre = "Strategic",
                rating = 4.4f,
                coverImageRes = R.drawable.person,
                isFavorite = false
            )
        )
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp) // Reduced spacing
    ) {
        items(books) { book ->
            BookCard(book = book, onFavoriteToggle = {
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
            .width(150.dp) // Slightly narrower
            .height(290.dp), // Slightly shorter
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp) // Less rounded
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(185.dp) // Adjusted height
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
                        .padding(6.dp), // Reduced padding
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Top
                ) {
                    IconButton(onClick = onFavoriteToggle) {
                        Icon(
                            imageVector = if (book.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.Favorite,
                            contentDescription = "Favorite",
                            tint = if (book.isFavorite) Color.Red else Color.White,
                            modifier = Modifier.size(24.dp) // Smaller icon
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp), // Reduced padding
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = book.title,
                        color = TextColor,
                        fontSize = 14.sp, // Smaller font
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = book.author,
                        color = SubtleTextColor,
                        fontSize = 11.sp, // Smaller font
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                )  {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = Color.Yellow,
                            modifier = Modifier.size(24.dp) // Smaller icon
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = book.rating.toString(),
                            color = SubtleTextColor,
                            fontSize = 16.sp, // Smaller font
                            fontWeight = FontWeight.Medium
                        )
                    }

            }
        }
    }
}

