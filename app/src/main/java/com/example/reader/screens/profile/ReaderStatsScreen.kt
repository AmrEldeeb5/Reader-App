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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.reader.R
import com.example.reader.navigation.ReaderScreens
import com.example.reader.ui.theme.ReaderTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalContext
import com.example.reader.screens.login.LoginScreenViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun StatsScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit
) {
    // Obtain context and ViewModel for logout action
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val loginViewModel: LoginScreenViewModel? = if (isPreview) null else koinViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Title
        Text(
            text = "Profile",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Avatar area – don't consume all height so the rest can be seen
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            // Avatar container (first box) - no clip to avoid clipping the badge
            Box(
                modifier = Modifier
                    .size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.streamline_kameleon_color__eyeglasses),
                    contentDescription = "User avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(CircleShape)
                        .clickable { navController.navigate(ReaderScreens.ReaderStatsScreen.name) }
                )

                // Camera badge (second box) inset from bottom-right so it's fully visible
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-8).dp, y = (-8).dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color = Color.White.copy(alpha = 0.8f))
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

        Spacer(Modifier.height(24.dp))

        // user name input field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
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
                Icon(
                    painter = painterResource(id = R.drawable.solar__user_bold),
                    contentDescription = "User Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            var displayName by rememberSaveable { mutableStateOf("") }

            TextField(
                value = displayName,
                onValueChange = { displayName = it },
                modifier = Modifier
                    .weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    disabledIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    errorIndicatorColor = MaterialTheme.colorScheme.error,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                label = { Text("Username") },
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
        }
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(ReaderScreens.YourFeedbackScreen.name) }
            .height(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
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
                Icon(
                    painter = painterResource(id = R.drawable.solar__smile_circle_bold),
                    contentDescription = "Feedback Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text("Your Feedback",modifier = Modifier.
            padding(12.dp)
                ,style = MaterialTheme.typography.titleMedium
            ,color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { navController.navigate(ReaderScreens.AboutScreen.name) },
            verticalAlignment = Alignment.CenterVertically
        ){
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
                Icon(
                    painter = painterResource(id = R.drawable.solar__danger_circle_bold),
                    contentDescription = "About Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text("About Us",modifier = Modifier.
            padding(12.dp)
                ,style = MaterialTheme.typography.titleMedium
                ,color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier
            .fillMaxWidth().clickable { onThemeToggle(!isDarkTheme) }
            .height(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
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
            Text("Theme",modifier = Modifier.
            padding(12.dp)
                ,style = MaterialTheme.typography.titleMedium
                ,color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { navController.navigate(ReaderScreens.ChangePasswordScreen.name) },
            verticalAlignment = Alignment.CenterVertically
        ){
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
                Icon(
                    painter = painterResource(id = R.drawable.solar__lock_password_bold),
                    contentDescription = "Password Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text("Change Password",modifier = Modifier.
            padding(12.dp)
                ,style = MaterialTheme.typography.titleMedium
                ,color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable {
                // Sign out with Firebase via ViewModel and clear preferences
                loginViewModel?.logout(context)

                // Navigate to login (or onboarding) and clear back stack
                val startRoute = navController.graph.startDestinationRoute
                navController.navigate(ReaderScreens.LoginScreen.name) {
                    popUpTo(startRoute ?: ReaderScreens.ReaderHomeScreen.name) { inclusive = true }
                    launchSingleTop = true
                }
            },
            verticalAlignment = Alignment.CenterVertically
        ){
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
                Icon(
                    painter = painterResource(id = R.drawable.solar__login_2_bold),
                    contentDescription = "Sign Out Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text("Log Out",modifier = Modifier.
            padding(12.dp)
                ,style = MaterialTheme.typography.titleMedium
                ,color = MaterialTheme.colorScheme.onSurfaceVariant)
        }


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
