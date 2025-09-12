package com.example.reader.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
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
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val screenWidth = configuration.screenWidthDp
    val isCompactHeight = screenHeight < 600
    val isVeryNarrow = screenWidth < 360
    val scrollState = rememberScrollState()

    // Dynamic sizing
    val imageHeight = remember(screenHeight) {
        val target = (screenHeight * if (isCompactHeight) 0.28f else 0.34f).dp
        target.coerceIn(140.dp, 260.dp)
    }
    val titleTextStyle = if (isVeryNarrow) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.displayLarge
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
                .padding(horizontal = 20.dp, vertical = if (isCompactHeight) 12.dp else 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Illustration / header area
            Box(
                modifier = Modifier.padding(top = if (isCompactHeight) 16.dp else 32.dp)
                    .fillMaxWidth()
                    .height(imageHeight),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.reader_logo),
                    contentDescription = "Illustration of books and a mug",
                    modifier = Modifier.fillMaxSize(0.9f),
                    contentScale = ContentScale.Fit
                )
            }


            // App title
            Text(
                text = "Reader",
                color = MaterialTheme.colorScheme.onBackground,
                style = titleTextStyle,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = if (isCompactHeight) 16.dp else 32.dp)
            )

            // Username input
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
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

            Spacer(modifier = Modifier.height(verticalSpacingAfterFields))

            // Login button
            Button(
                onClick = {
                    if (username == "admin" && password == "1234") {
                        onLoginClick(username, password)
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

            // Sign up link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("New user?", style = if (isVeryNarrow) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                TextButton(onClick = {
                    navController.navigate(ReaderScreens.CreateAccountScreen.name)
                }) {
                    Text(
                        "Sign up",
                        style = if (isVeryNarrow) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height( if (isCompactHeight) 8.dp else 24.dp ))
        }
    }
}
