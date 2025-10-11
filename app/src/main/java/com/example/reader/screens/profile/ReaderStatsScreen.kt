package com.example.reader.screens.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.reader.R
import com.example.reader.navigation.ReaderScreens
import com.example.reader.screens.login.LoginScreenViewModel
import com.example.reader.ui.theme.ReaderTheme
import org.koin.androidx.compose.koinViewModel


@Composable
fun StatsScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    isGreenTheme: Boolean = true,
    onColorSchemeToggle: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val loginViewModel: LoginScreenViewModel? = if (isPreview) null else koinViewModel()
    val userProfileViewModel: UserProfileViewModel? = if (isPreview) null else koinViewModel()

    val username by (userProfileViewModel?.username?.collectAsState() ?: remember { mutableStateOf("") })
    val isLoading by (userProfileViewModel?.isLoading?.collectAsState() ?: remember { mutableStateOf(false) })
    var showUsernameDialog by remember { mutableStateOf(false) }

    // Username Edit Dialog
    UsernameEditDialog(
        currentUsername = username,
        isVisible = showUsernameDialog,
        isLoading = isLoading,
        onDismiss = { showUsernameDialog = false },
        onSave = { newUsername ->
            userProfileViewModel?.updateUsername(newUsername)
            showUsernameDialog = false
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        ProfileTitle()
        ProfileAvatar(navController, isGreenTheme)
        Spacer(Modifier.height(24.dp))

        UsernameSection(
            username = username,
            onClick = { showUsernameDialog = true }
        )

        Spacer(Modifier.height(24.dp))

        // Menu Items
        ProfileMenuItem(
            iconRes = R.drawable.solar__smile_circle_bold,
            title = "Your Feedback",
            onClick = { navController.navigate(ReaderScreens.YourFeedbackScreen.name) }
        )

        ProfileMenuItem(
            iconRes = R.drawable.solar__danger_circle_bold,
            title = "About Us",
            onClick = { navController.navigate(ReaderScreens.AboutScreen.name) }
        )

        ThemeMenuItem(
            isDarkTheme = isDarkTheme,
            onThemeToggle = onThemeToggle
        )

        ColorSchemeMenuItem(
            isGreenTheme = isGreenTheme,
            onColorSchemeToggle = onColorSchemeToggle
        )

        ProfileMenuItem(
            iconRes = R.drawable.solar__lock_password_bold,
            title = "Change Password",
            onClick = { navController.navigate(ReaderScreens.ChangePasswordScreen.name) }
        )

        LogoutMenuItem(
            onClick = {
                loginViewModel?.logout(context)
                val startRoute = navController.graph.startDestinationRoute
                navController.navigate(ReaderScreens.LoginScreen.name) {
                    popUpTo(startRoute ?: ReaderScreens.ReaderHomeScreen.name) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }
}

@Composable
private fun ProfileTitle() {
    Text(
        text = "Profile",
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
private fun ProfileAvatar(navController: NavController, isGreenTheme: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = if (isGreenTheme) painterResource(id = R.drawable.streamline_kameleon_color__eyeglasses) else painterResource(id = R.drawable.streamline_kameleon_color_2__eyeglasses),
                contentDescription = "User avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .clickable { navController.navigate(ReaderScreens.ReaderStatsScreen.name) }
            )

            // Camera badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-8).dp, y = (-8).dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.8f))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.solar__camera_linear),
                    contentDescription = "Camera Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun UsernameSection(
    username: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileIconBox(iconRes = R.drawable.solar__user_bold)

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Username",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (username.isNotBlank()) username else "edit",
                style = MaterialTheme.typography.bodyLarge,
                color = if (username.isNotBlank())
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    iconRes: Int,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileIconBox(iconRes = iconRes)
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun ThemeMenuItem(
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onThemeToggle(!isDarkTheme) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .fillMaxHeight()
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    RoundedCornerShape(12.dp)
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            ThemeToggleCompact(
                isDark = isDarkTheme,
                onToggle = onThemeToggle
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Dark Mode",
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun ColorSchemeMenuItem(
    isGreenTheme: Boolean,
    onColorSchemeToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onColorSchemeToggle(!isGreenTheme) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .fillMaxHeight()
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    RoundedCornerShape(12.dp)
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            ColorSchemeToggleCompact(
                isGreen = isGreenTheme,
                onToggle = onColorSchemeToggle
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (isGreenTheme) "Green Theme" else "Brown Theme",
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun LogoutMenuItem(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileIconBox(iconRes = R.drawable.solar__login_2_bold)
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Log Out",
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileIconBox(iconRes: Int) {
    Box(
        modifier = Modifier
            .width(48.dp)
            .height(48.dp)
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun ThemeToggleCompact(
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
        targetValue = if (isDark) Color(0xFF1A1B3A) else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
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

@Composable
fun ColorSchemeToggleCompact(
    isGreen: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isGreen) 0f else 360f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_rotation"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isGreen) Color(0xFF24786D).copy(alpha = 0.3f) else Color(0xFF6D4C41).copy(alpha = 0.3f),
        animationSpec = tween(300),
        label = "background_color"
    )

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onToggle(!isGreen) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isGreen) "🌿" else "🌰",
            fontSize = 24.sp,
            modifier = Modifier.rotate(rotationAngle)
        )
    }
}

@Preview(name = "Stats - Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewStatsScreenLight() {
    ReaderTheme(darkTheme = false) {
        StatsScreen(
            navController = rememberNavController(),
            isDarkTheme = false,
            onThemeToggle = {}
        )
    }
}

@Preview(name = "Stats - Dark", showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PreviewStatsScreenDark() {
    ReaderTheme(darkTheme = true) {
        StatsScreen(
            navController = rememberNavController(),
            isDarkTheme = true,
            onThemeToggle = {}
        )
    }
}
