package com.example.reader.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.reader.R
import com.example.reader.components.LoadingState
import com.example.reader.navigation.ReaderScreens
import kotlinx.coroutines.launch

// Constants for better maintainability
private object LoginConstants {
    const val COMPACT_HEIGHT_THRESHOLD = 600
    const val NARROW_WIDTH_THRESHOLD = 360
    const val DEFAULT_SPACING = 12
    const val LARGE_SPACING = 16
    const val EXTRA_LARGE_SPACING = 24
    const val BUTTON_HEIGHT = 50
    const val PROGRESS_INDICATOR_SIZE = 22
    const val PROGRESS_STROKE_WIDTH = 2
}

// Data classes for better state management
data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val loginError: String? = null
)

data class ScreenDimensions(
    val screenHeight: Int,
    val screenWidth: Int,
    val isCompactHeight: Boolean,
    val isVeryNarrow: Boolean,
    val imageHeight: androidx.compose.ui.unit.Dp,
    val verticalSpacingAfterFields: androidx.compose.ui.unit.Dp
)

/**
 * Calculates screen dimensions and responsive settings
 */
@Composable
private fun rememberScreenDimensions(): ScreenDimensions {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val screenWidth = configuration.screenWidthDp

    return remember(screenHeight, screenWidth) {
        val isCompactHeight = screenHeight < LoginConstants.COMPACT_HEIGHT_THRESHOLD
        val isVeryNarrow = screenWidth < LoginConstants.NARROW_WIDTH_THRESHOLD

        val imageHeight = run {
            val fraction = when {
                screenHeight < 520 -> 0.20f
                screenHeight < 560 -> 0.22f
                screenHeight < 600 -> 0.24f
                else -> 0.30f
            }
            (screenHeight * fraction).dp.coerceIn(120.dp, 240.dp)
        }

        val verticalSpacingAfterFields = if (isCompactHeight)
            LoginConstants.LARGE_SPACING.dp
        else
            LoginConstants.EXTRA_LARGE_SPACING.dp

        ScreenDimensions(
            screenHeight = screenHeight,
            screenWidth = screenWidth,
            isCompactHeight = isCompactHeight,
            isVeryNarrow = isVeryNarrow,
            imageHeight = imageHeight,
            verticalSpacingAfterFields = verticalSpacingAfterFields
        )
    }
}

/**
 * Validates login form input
 */
private fun validateLoginForm(email: String, password: String): String? {
    return when {
        email.isBlank() -> "Email is required"
        password.isBlank() -> "Password is required"
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
        else -> null
    }
}

/**
 * Top app bar for login screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginTopAppBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(
                text = "Login",
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.navigate(ReaderScreens.OnBoardingScreen.name) {
                        popUpTo(ReaderScreens.LoginScreen.name) { inclusive = true }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}

/**
 * App logo and title section
 */
@Composable
private fun LoginHeader(screenDimensions: ScreenDimensions) {
    // App Logo
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenDimensions.imageHeight),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.reader_logo),
            contentDescription = "Reader app logo with books and mug",
            contentScale = ContentScale.Fit
        )
    }

    Spacer(modifier = Modifier.height(1.dp))

    // App Title
    Text(
        text = "Reader",
        color = MaterialTheme.colorScheme.onBackground,
        style = if (screenDimensions.isVeryNarrow)
            MaterialTheme.typography.headlineLarge
        else
            MaterialTheme.typography.displayLarge,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(
            bottom = if (screenDimensions.isCompactHeight) 16.dp else 32.dp
        )
    )
}

/**
 * Email input field
 */
@Composable
private fun EmailTextField(
    email: String,
    onEmailChange: (String) -> Unit,
    onNext: () -> Unit
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { onNext() })
    )
}

/**
 * Password input field with visibility toggle
 */
@Composable
private fun PasswordTextField(
    password: String,
    passwordVisible: Boolean,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onDone: () -> Unit
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Password") },
        singleLine = true,
        visualTransformation = if (passwordVisible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else
                Icons.Filled.VisibilityOff

            IconButton(onClick = onPasswordVisibilityToggle) {
                Icon(
                    imageVector = image,
                    contentDescription = if (passwordVisible)
                        "Hide password"
                    else
                        "Show password"
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() })
    )
}

/**
 * Error message display
 */
@Composable
private fun ErrorMessage(errorMessage: String?) {
    if (errorMessage != null) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Remember me checkbox section
 */
@Composable
private fun RememberMeSection(screenDimensions: ScreenDimensions) {
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
            style = if (screenDimensions.isVeryNarrow)
                MaterialTheme.typography.bodySmall
            else
                MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.clickable {
                RememberMeBoxState.rememberMe = !RememberMeBoxState.rememberMe
            }
        )
    }
}

/**
 * Login button with loading state
 */
@Composable
private fun LoginButton(
    isLoading: Boolean,
    onLoginClick: () -> Unit
) {
    Button(
        onClick = onLoginClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(LoginConstants.BUTTON_HEIGHT.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = MaterialTheme.shapes.medium,
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(LoginConstants.PROGRESS_INDICATOR_SIZE.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                strokeWidth = LoginConstants.PROGRESS_STROKE_WIDTH.dp
            )
        } else {
            Text(
                text = "Login",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

/**
 * Main login screen composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderLoginScreen(
    navController: NavController,
    onLoginClick: (String, String) -> Unit,
    viewModel: LoginScreenViewModel = viewModel()
) {
    // Form state management
    var formState by remember { mutableStateOf(LoginFormState()) }

    // UI state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    // ViewModel state observation
    val loginState by viewModel.loginState.collectAsState()
    val isLoading = loginState.status == LoadingState.Status.LOADING

    // Screen dimensions and responsive settings
    val screenDimensions = rememberScreenDimensions()

    // Handle login state changes
    LaunchedEffect(loginState) {
        if (loginState.status == LoadingState.Status.ERROR) {
            val errorMessage = loginState.message ?: "Login failed"
            formState = formState.copy(loginError = errorMessage)
            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    /**
     * Handles login form submission
     */
    fun handleLogin() {
        if (isLoading) return

        // Validate form
        val validationError = validateLoginForm(formState.email, formState.password)
        if (validationError != null) {
            formState = formState.copy(loginError = validationError)
            coroutineScope.launch {
                snackbarHostState.showSnackbar(validationError)
            }
            return
        }

        // Clear previous errors and hide keyboard
        formState = formState.copy(loginError = null)
        focusManager.clearFocus()
        keyboardController?.hide()

        // Attempt login
        viewModel.login(formState.email, formState.password) { success, message ->
            if (success) {
                onLoginClick(formState.email, formState.password)
                navController.navigate(ReaderScreens.ReaderHomeScreen.name) {
                    popUpTo(ReaderScreens.LoginScreen.name) { inclusive = true }
                }
            } else {
                val errorMessage = message ?: "Login failed"
                formState = formState.copy(loginError = errorMessage)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
            }
        }
    }

    Scaffold(
        topBar = { LoginTopAppBar(navController) },
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    top = 1.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = if (screenDimensions.isCompactHeight)
                        LoginConstants.DEFAULT_SPACING.dp
                    else
                        LoginConstants.EXTRA_LARGE_SPACING.dp
                )
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            LoginHeader(screenDimensions)

            // Email Input
            EmailTextField(
                email = formState.email,
                onEmailChange = {
                    formState = formState.copy(
                        email = it,
                        loginError = null
                    )
                },
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )

            Spacer(modifier = Modifier.height(LoginConstants.DEFAULT_SPACING.dp))

            // Password Input
            PasswordTextField(
                password = formState.password,
                passwordVisible = formState.passwordVisible,
                onPasswordChange = {
                    formState = formState.copy(
                        password = it,
                        loginError = null
                    )
                },
                onPasswordVisibilityToggle = {
                    formState = formState.copy(
                        passwordVisible = !formState.passwordVisible
                    )
                },
                onDone = { handleLogin() }
            )

            // Error Message
            ErrorMessage(formState.loginError)

            Spacer(modifier = Modifier.height(LoginConstants.DEFAULT_SPACING.dp))

            // Remember Me Section
            RememberMeSection(screenDimensions)

            Spacer(modifier = Modifier.height(screenDimensions.verticalSpacingAfterFields))

            // Login Button
            LoginButton(
                isLoading = isLoading,
                onLoginClick = { handleLogin() }
            )

            Spacer(
                modifier = Modifier.height(
                    if (screenDimensions.isCompactHeight)
                        LoginConstants.DEFAULT_SPACING.dp
                    else
                        LoginConstants.LARGE_SPACING.dp
                )
            )

            // Footer Links (Forgot Password & Create Account)
            AuthFooterLinks(
                navController = navController,
                isCompact = screenDimensions.isCompactHeight,
                isVeryNarrow = screenDimensions.isVeryNarrow
            )

            Spacer(
                modifier = Modifier.height(
                    if (screenDimensions.isCompactHeight)
                        8.dp
                    else
                        LoginConstants.EXTRA_LARGE_SPACING.dp
                )
            )
        }
    }
}