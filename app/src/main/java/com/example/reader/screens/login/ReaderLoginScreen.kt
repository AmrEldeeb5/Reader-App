package com.example.reader.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reader.navigation.ReaderScreens
import com.example.reader.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderLoginScreen(navController: NavController, onLoginClick: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val screenWidth = configuration.screenWidthDp
    val isCompactHeight = screenHeight < 600
    val isVeryNarrow = screenWidth < 360
    val scrollState = rememberScrollState()

    // Reused image sizing logic from SignUpScreen (fractions + clamp 120..240dp)
    val imageHeight = remember(screenHeight) {
        val fraction = when {
            screenHeight < 520 -> 0.20f
            screenHeight < 560 -> 0.22f
            screenHeight < 600 -> 0.24f
            else -> 0.30f
        }
        val target = (screenHeight * fraction).dp
        target.coerceIn(120.dp, 240.dp)
    }
    val verticalSpacingAfterFields = if (isCompactHeight) 16.dp else 24.dp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(ReaderScreens.OnBoardingScreen.name) {
                            popUpTo(ReaderScreens.LoginScreen.name) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 1.dp, start = 20.dp, end = 20.dp, bottom = if (isCompactHeight) 12.dp else 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header image (no extra large spacer before to match SignUp layout)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.reader_logo),
                    contentDescription = "Illustration of books and a mug",
                    // Removed .fillMaxSize(0.9f) so scaling matches SignUpScreen
                    contentScale = ContentScale.Fit
                )
            }
            // Minimal spacer after image like SignUpScreen
            Spacer(modifier = Modifier.height(1.dp))

            // App title
            Text(
                text = "Reader",
                color = MaterialTheme.colorScheme.onBackground,
                style = if (isVeryNarrow) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = if (isCompactHeight) 16.dp else 32.dp)
            )

            // Username input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(image, contentDescription = "Toggle password visibility")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // uses shared state by default
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RememberMeBox()
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Remember me",
                    style = if (isVeryNarrow) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.clickable { RememberMeBoxState.rememberMe = !RememberMeBoxState.rememberMe }
                )
            }

            Spacer(modifier = Modifier.height(verticalSpacingAfterFields))

            // Login button
            Button(
                onClick = {
                    if (email == "already@used.com" && password == "1234") {
                        onLoginClick(email, password)
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name) {
                            popUpTo(ReaderScreens.LoginScreen.name) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    "Login",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(if (isCompactHeight) 12.dp else 16.dp))

            // Footer links row (Forgot Password left, Create Account right)
            AuthFooterLinks(
                navController = navController,
                isCompact = isCompactHeight,
                isVeryNarrow = isVeryNarrow
            )

            Spacer(modifier = Modifier.height(if (isCompactHeight) 8.dp else 24.dp ))
        }
    }
}
