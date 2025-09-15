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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reader.navigation.ReaderScreens
import com.example.reader.R
import kotlinx.coroutines.launch
// Added for keyboard actions & focus
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderLoginScreen(navController: NavController, onLoginClick: (String, String) -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }

    // Added login error + snackbar state (SnackbarHostState not saveable -> use remember)
    var loginError by rememberSaveable { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Loading state for login action
    var isLoading by rememberSaveable { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val screenWidth = configuration.screenWidthDp
    val isCompactHeight = screenHeight < 600
    val isVeryNarrow = screenWidth < 360
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

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

    // Centralized login trigger to reuse for button & IME Done action
    fun triggerLogin() {
        if (isLoading) return
        // Basic validation example (extend as needed)
        if (email.isBlank() || password.isBlank()) {
            loginError = "Email and password required"
            coroutineScope.launch { snackbarHostState.showSnackbar(loginError!!) }
            return
        }
        isLoading = true
        loginError = null // clear previous error
        focusManager.clearFocus()
        coroutineScope.launch {
            // Simulate network delay
            kotlinx.coroutines.delay(1200)
            // Success condition placeholder (keep existing demo logic)
            if (email == "already@used.com" && password == "1234") {
                onLoginClick(email, password)
                isLoading = false
                navController.navigate(ReaderScreens.ReaderHomeScreen.name) {
                    popUpTo(ReaderScreens.LoginScreen.name) { inclusive = true }
                }
            } else {
                loginError = "Invalid email or password"
                isLoading = false
                snackbarHostState.showSnackbar(loginError!!)
            }
        }
    }

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
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 1.dp, start = 20.dp, end = 20.dp, bottom = if (isCompactHeight) 12.dp else 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.reader_logo),
                    contentDescription = "Illustration of books and a mug",
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = "Reader",
                color = MaterialTheme.colorScheme.onBackground,
                style = if (isVeryNarrow) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = if (isCompactHeight) 16.dp else 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (loginError != null) loginError = null // clear error on input change
                },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (loginError != null) loginError = null // clear error on input change
                },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    // Trailing icon toggles password visibility
                    val image = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(image, contentDescription = "Toggle password visibility")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { triggerLogin() })
            )

            if (loginError != null) {
                Text(
                    text = loginError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

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

            Button(
                onClick = { triggerLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = MaterialTheme.shapes.medium,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Login",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isCompactHeight) 12.dp else 16.dp))

            AuthFooterLinks(
                navController = navController,
                isCompact = isCompactHeight,
                isVeryNarrow = isVeryNarrow
            )

            Spacer(modifier = Modifier.height(if (isCompactHeight) 8.dp else 24.dp ))
        }
    }
}
