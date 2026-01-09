package com.example.reader.screens.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reader.screens.login.LoginScreenViewModel
import com.example.reader.screens.profile.UserProfileViewModel

@Composable
fun StatsScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    isGreenTheme: Boolean = true,
    onColorSchemeToggle: (Boolean) -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current
    val loginViewModel: LoginScreenViewModel? = if (isPreview) null else hiltViewModel()
    val userProfileViewModel: UserProfileViewModel? = if (isPreview) null else hiltViewModel()
    val context = LocalContext.current
    val username by (userProfileViewModel?.username?.collectAsState() ?: remember { mutableStateOf("Andy") })
    var showUsernameDialog by remember { mutableStateOf(false) }
    // Show logout confirmation dialog state
    var showLogoutDialog by remember { mutableStateOf(false) }

    UsernameEditDialog(
        currentUsername = username,
        isVisible = showUsernameDialog,
        isLoading = false,
        onDismiss = { showUsernameDialog = false },
        onSave = { newUsername ->
            userProfileViewModel?.updateUsername(newUsername)
            showUsernameDialog = false
        }
    )

    // Logout confirmation dialog
    LogoutConfirmationDialog(
        isVisible = showLogoutDialog,
        onDismiss = { showLogoutDialog = false },
        onConfirm = {
            loginViewModel?.logout()
            navController.navigate(ReaderScreens.LoginScreen.name) {
                popUpTo(navController.graph.startDestinationRoute ?: ReaderScreens.ReaderHomeScreen.name) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Profile",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        ProfileAvatar(navController, isGreenTheme)
        Spacer(Modifier.height(24.dp))

        UsernameRow(username) { showUsernameDialog = true }
        Spacer(Modifier.height(24.dp))

        MenuItem(R.drawable.solar__smile_circle_bold, "Your Feedback") {
            navController.navigate(ReaderScreens.YourFeedbackScreen.name)
        }
        MenuItem(R.drawable.solar__danger_circle_bold, "About Us") {
            navController.navigate(ReaderScreens.AboutScreen.name)
        }
        ThemeToggleRow(isDarkTheme, onThemeToggle)
        ColorSchemeRow(isGreenTheme, onColorSchemeToggle)
        MenuItem(R.drawable.solar__lock_password_bold, "Change Password") {
            navController.navigate(ReaderScreens.ChangePasswordScreen.name)
        }
        MenuItem(R.drawable.solar__login_2_bold, "Log Out") {
            // Open confirmation instead of logging out immediately
            showLogoutDialog = true
        }
    }
}

@Composable
private fun ProfileAvatar(navController: NavController, isGreenTheme: Boolean) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Box(Modifier.size(200.dp), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(
                    if (isGreenTheme) R.drawable.streamline_kameleon_color__eyeglasses
                    else R.drawable.streamline_kameleon_color_2__eyeglasses
                ),
                contentDescription = "User avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .clickable { navController.navigate(ReaderScreens.ReaderStatsScreen.name) }
            )

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
                    contentDescription = "Camera",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun UsernameRow(username: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().wrapContentHeight().clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBox(R.drawable.solar__user_bold)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                "Username",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                if (username.isNotBlank()) username else "edit",
                style = MaterialTheme.typography.bodyLarge,
                color = if (username.isNotBlank()) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun MenuItem(iconRes: Int, title: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().wrapContentHeight().clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBox(iconRes)
        Spacer(Modifier.width(8.dp))
        Text(
            title,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun ThemeToggleRow(isDark: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().wrapContentHeight().clickable { onToggle(!isDark) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToggleBox { ThemeToggle(isDark, onToggle) }
        Spacer(Modifier.width(8.dp))
        Text(
            "Dark Mode",
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun ColorSchemeRow(isGreen: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().wrapContentHeight().clickable { onToggle(!isGreen) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToggleBox { ColorSchemeToggle(isGreen, onToggle) }
        Spacer(Modifier.width(8.dp))
        Text(
            if (isGreen) "Green Theme" else "Brown Theme",
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun IconBox(iconRes: Int) {
    Box(
        Modifier
            .size(48.dp)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun ToggleBox(content: @Composable () -> Unit) {
    Box(
        Modifier
            .size(48.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun ThemeToggle(isDark: Boolean, onToggle: (Boolean) -> Unit) {
    val rotation by animateFloatAsState(
        if (isDark) 180f else 0f,
        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "rotation"
    )
    val bgColor by animateColorAsState(
        if (isDark) Color(0xFF1A1B3A) else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        tween(300),
        label = "bg"
    )

    Box(
        Modifier.size(48.dp).clip(CircleShape).background(bgColor).clickable { onToggle(!isDark) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            if (isDark) Icons.Filled.DarkMode else Icons.Filled.LightMode,
            contentDescription = null,
            tint = if (isDark) Color.White else Color(0xFF4A4A4A),
            modifier = Modifier.size(24.dp).rotate(rotation)
        )
    }
}

@Composable
private fun ColorSchemeToggle(isGreen: Boolean, onToggle: (Boolean) -> Unit) {
    val rotation by animateFloatAsState(
        if (isGreen) 0f else 360f,
        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "rotation"
    )
    val bgColor by animateColorAsState(
        if (isGreen) Color(0xFF24786D).copy(alpha = 0.3f) else Color(0xFF6D4C41).copy(alpha = 0.3f),
        tween(300),
        label = "bg"
    )

    Box(
        Modifier.size(48.dp).clip(CircleShape).background(bgColor).clickable { onToggle(!isGreen) },
        contentAlignment = Alignment.Center
    ) {
        Text(if (isGreen) "🌿" else "🌰", fontSize = 16.sp, modifier = Modifier.rotate(rotation))
    }
}

@Composable
private fun LogoutConfirmationDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painter = painterResource(R.drawable.solar__login_2_bold),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = { Text("Confirm Logout", style = MaterialTheme.typography.titleLarge) },
        text = { Text("Are you sure you want to logout?😟", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Logout", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(name = "Light", showBackground = true)
@Composable
private fun PreviewLight() {
    MaterialTheme {
        StatsScreen(rememberNavController(), false, {})
    }
}

@Preview(name = "Dark", showBackground = true)
@Composable
private fun PreviewDark() {
    MaterialTheme {
        StatsScreen(rememberNavController(), true, {})
    }
}