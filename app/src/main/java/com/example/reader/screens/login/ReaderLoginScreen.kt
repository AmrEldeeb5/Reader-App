package com.example.reader.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reader.R
import com.example.reader.components.LoadingState
import com.example.reader.components.LoginConstants
import com.example.reader.components.LoginFormState
import com.example.reader.navigation.ReaderScreens
import com.example.reader.utils.ResponsiveLayout
import com.example.reader.utils.UserPreferences
import com.example.reader.utils.rememberResponsiveLayout
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


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
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
private fun LoginHeader(layout: ResponsiveLayout) {
    // App Logo
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(layout.imageHeight),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.reader_logo),
            contentDescription = "Reader app logo with books and mug",
            contentScale = ContentScale.Fit
        )
    }

}


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
private fun RememberMeSection(layout: ResponsiveLayout) {
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
            style = if (layout.isCompact)
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
 * Integrates with Firebase Authentication for secure login and session management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderLoginScreen(
    navController: NavController,
    onLoginClick: (String, String) -> Unit,
    viewModel: LoginScreenViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    // Form state management - Load saved email if "Remember Me" was checked
    var formState by remember {
        mutableStateOf(
            LoginFormState(
                email = if (userPrefs.getRememberMe()) userPrefs.getSavedEmail() ?: "" else ""
            )
        )
    }

    // UI state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    // ViewModel state observation
    val loginState by viewModel.loginState.collectAsState()
    val isLoading = loginState.status == LoadingState.Status.LOADING

    // Modern responsive layout
    val layout = rememberResponsiveLayout()

    // Handle login state changes
    LaunchedEffect(loginState) {
        if (loginState.status == LoadingState.Status.ERROR) {
            val errorMessage = loginState.message ?: "Login failed"
            formState = formState.copy(loginError = errorMessage)
            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    /**
     * Handles login form submission with Firebase Auth
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

        // Attempt Firebase login
        viewModel.login(formState.email, formState.password) { success, message ->
            if (success) {
                // Handle "Remember Me" for auto-login
                if (RememberMeBoxState.rememberMe) {
                    // Save credentials securely for auto-login
                    userPrefs.saveCredentials(
                        email = formState.email,
                        password = formState.password
                    )
                    userPrefs.setRememberMe(true)
                } else {
                    // Clear saved credentials if user unchecked "Remember Me"
                    userPrefs.clearCredentials()
                    userPrefs.setRememberMe(false)
                }

                // Firebase Auth automatically handles session persistence
                // No need to manually track login state

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = if (layout.isExpanded) Alignment.Center else Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = layout.contentMaxWidth)
                    .fillMaxWidth()
                    .padding(
                        horizontal = layout.horizontalPadding,
                        vertical = layout.verticalSpacing
                    )
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Section
                LoginHeader(layout)

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

                Spacer(modifier = Modifier.height(layout.verticalSpacing))

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

                Spacer(modifier = Modifier.height(layout.verticalSpacing))

                // Remember Me Section
                RememberMeSection(layout)

                Spacer(modifier = Modifier.height(layout.verticalSpacing))

                // Login Button
                LoginButton(
                    isLoading = isLoading,
                    onLoginClick = { handleLogin() }
                )

                Spacer(modifier = Modifier.height(layout.verticalSpacing))

                // Footer Links (Forgot Password & Create Account)
                AuthFooterLinks(
                    navController = navController,
                    isCompact = layout.isCompactHeight,
                    isVeryNarrow = layout.isCompact
                )
            }
        }
    }
}