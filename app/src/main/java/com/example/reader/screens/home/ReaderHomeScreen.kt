package com.example.reader.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reader.data.Book
import com.example.reader.navigation.ReaderScreens
import com.example.reader.ui.theme.CardBackground
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.rotate
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    isDarkTheme: Boolean = false,
    onThemeToggle: (Boolean) -> Unit = {}
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            HomeTopBar(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute
            )
        },
        containerColor = MaterialTheme.colorScheme.background // Use Material 3 background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            BookDiscoveryScreen(isDarkTheme = isDarkTheme)
            CategoryTabs(
                modifier = Modifier.padding(top = 1.dp),
                isDarkTheme = isDarkTheme
            )
            Spacer(modifier = Modifier.height(16.dp))
            BookGridSection(isDarkTheme = isDarkTheme)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    userName: String? = null,
    onNotificationsClick: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val initialName = remember(userName, auth.currentUser) {
        userName ?: auth.currentUser?.displayName?.takeIf { it.isNotBlank() }
        ?: "Andy"
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
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        actions = {
            IconButton(onClick = onNotificationsClick) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
            FunThemeToggleCompact(
                isDark = isDarkTheme,
                onToggle = onThemeToggle
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
fun BookGridSection(isDarkTheme: Boolean) {
    val books = remember {
        mutableStateListOf(
            Book(
                id = 1,
                title = "The Moor and the Novel",
                author = "Mary B. Quinn",
                subtitle = "Narrating Absence in early modern Spain",
                rating = 4.4,
                coverImageUrl = "http://books.google.com/books/content?id=cn5lEAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
                isFavorite = true
            ),
            Book(
                id = 2,
                title = "The Radical Art of SelfLove",
                author = "Deepak Singh",
                subtitle = "The Radical Art of SelfLove",
                rating = 4.5,
                coverImageUrl = "http://books.google.com/books/content?id=NnnLEAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
                isFavorite = false
            ),
            Book(
                id = 3,
                title = "Computer Applications in the Social Sciences",
                author = "Edward E. Brent,"+"Ronald E. Anderson",
                subtitle = "Presenting an introduction to computing and advice on computer applications",
                rating = 4.0,
                coverImageUrl = "http://books.google.com/books/content?id=5KtoPaM6r9EC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
                isFavorite = false
            ),
            Book(
                id = 4,
                title = "Romantic Love",
                author = "Yolanda van Ede",
                subtitle = "important aspects of these relationships",
                rating = 4.0,
                coverImageUrl = "http://books.google.com/books/content?id=b1yOJtOwvWIC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
                isFavorite = true
            )
        )
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(books) { book ->
            BookCard(
                book = book,
                isDarkTheme = isDarkTheme, // Pass the theme parameter
                onFavoriteToggle = {
                    val index = books.indexOf(book)
                    if (index != -1) {
                        books[index] = book.copy(isFavorite = !book.isFavorite)
                    }
                }
            )
        }
    }
}

@Composable
fun BookCard(
    book: Book,
    onFavoriteToggle: () -> Unit,
    isDarkTheme: Boolean
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(290.dp)
            .clickable { /* Handle book click safely */ },
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) {
                CardBackground
            } else {
                MaterialTheme.colorScheme.surface // Use Material 3 surface color
            }
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkTheme) 4.dp else 1.dp
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(185.dp)
            ) {
                AsyncImage(
                    model = book.coverImageUrl,
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )



                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Top
                ) {
                    IconButton(onClick = onFavoriteToggle) {
                        val tint by animateColorAsState(
                            targetValue = if (book.isFavorite) Color.Red else Color.White,
                            animationSpec = tween(300),
                            label = "favorite_color"
                        )
                        Icon(
                            imageVector = if (book.isFavorite) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                            contentDescription = "Favorite",
                            tint = tint,
                            modifier = Modifier.size(24.dp)
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
                        color = MaterialTheme.colorScheme.onSurface, // Use Material 3 colors
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = book.author,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // Use Material 3 colors
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = book.rating.toString(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun FunThemeToggleCompact(
    isDark: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isDark) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_rotation"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF1A1B3A) else Color(0xFF3FAF9E).copy(alpha = 0.3f),
        animationSpec = tween(300),
        label = "background_color"
    )

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onToggle(!isDark) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isDark) Icons.Filled.DarkMode else Icons.Filled.LightMode,
            contentDescription = if (isDark) "Switch to Light Mode" else "Switch to Dark Mode",
            tint = if (isDark) Color.White else Color(0xFF4A4A4A),
            modifier = Modifier
                .size(24.dp)
                .rotate(rotationAngle)
        )
    }
}