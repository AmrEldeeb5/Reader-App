package com.example.reader.screens.SignUp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.reader.screens.login.RememberMeBox
import com.example.reader.screens.login.RememberMeBoxState
import kotlinx.coroutines.launch

// Constants for better maintainability
private object SignUpConstants {
    const val MIN_PASSWORD_LENGTH = 6
    const val COMPACT_HEIGHT_THRESHOLD = 600
    const val NARROW_WIDTH_THRESHOLD = 360
    const val DEFAULT_SPACING = 12
    const val SMALL_SPACING = 4
    const val LARGE_SPACING = 16
    const val EXTRA_LARGE_SPACING = 48
    const val PROGRESS_INDICATOR_SIZE = 22
    const val PROGRESS_STROKE_WIDTH = 2
}

// Data classes for better state management
data class SignUpFormState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false
)

data class FormErrors(
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null
) {
    val hasErrors: Boolean
        get() = nameError != null || emailError != null || passwordError != null ||
                confirmPasswordError != null || generalError != null
}

// Helper data class for screen dimensions
data class ScreenDimensions(
    val screenHeight: Int,
    val screenWidth: Int,
    val isCompactHeight: Boolean,
    val isVeryNarrow: Boolean,
    val imageHeight: androidx.compose.ui.unit.Dp
)

@Composable
private fun rememberScreenDimensions(): ScreenDimensions {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val screenWidth = configuration.screenWidthDp

    return remember(screenHeight, screenWidth) {
        val isCompactHeight = screenHeight < SignUpConstants.COMPACT_HEIGHT_THRESHOLD
        val isVeryNarrow = screenWidth < SignUpConstants.NARROW_WIDTH_THRESHOLD

        val imageHeight = run {
            val fraction = when {
                screenHeight < 520 -> 0.20f
                screenHeight < 560 -> 0.22f
                screenHeight < 600 -> 0.24f
                else -> 0.30f
            }
            (screenHeight * fraction).dp.coerceIn(120.dp, 240.dp)
        }

        ScreenDimensions(
            screenHeight = screenHeight,
            screenWidth = screenWidth,
            isCompactHeight = isCompactHeight,
            isVeryNarrow = isVeryNarrow,
            imageHeight = imageHeight
        )
    }
}

// Validation functions
private fun validateForm(formState: SignUpFormState): FormErrors {
    return FormErrors(
        nameError = when {
            formState.name.isBlank() -> "Name is required"
            else -> null
        },
        emailError = when {
            formState.email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(formState.email).matches() -> "Invalid email format"
            else -> null
        },
        passwordError = when {
            formState.password.isBlank() -> "Password is required"
            formState.password.length < SignUpConstants.MIN_PASSWORD_LENGTH ->
                "Password must be at least ${SignUpConstants.MIN_PASSWORD_LENGTH} characters"
            else -> null
        },
        confirmPasswordError = when {
            formState.confirmPassword.isBlank() -> "Please confirm your password"
            formState.confirmPassword != formState.password -> "Passwords do not match"
            else -> null
        }
    )
}

// Reusable UI Components
@Composable
private fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityToggle: (() -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = error != null,
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (isPassword && onPasswordVisibilityToggle != null) {
                {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = onPasswordVisibilityToggle) {
                        Icon(
                            imageVector = image,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary
            ),
            keyboardOptions = KeyboardOptions(imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onNext = { onImeAction() },
                onDone = { onImeAction() }
            )
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun SignUpButton(
    onClick: () -> Unit,
    isEnabled: Boolean,
    isLoading: Boolean,
    screenDimensions: ScreenDimensions
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = isEnabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnabled) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isEnabled) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(SignUpConstants.PROGRESS_INDICATOR_SIZE.dp),
                color = if (isEnabled) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                strokeWidth = SignUpConstants.PROGRESS_STROKE_WIDTH.dp
            )
        } else {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    onSignUpClick: (String, String, String) -> Unit,
    viewModel: SignUpScreenViewModel = viewModel()
) {
    // Form state management
    var formState by rememberSaveable { mutableStateOf(SignUpFormState()) }
    var formErrors by rememberSaveable { mutableStateOf(FormErrors()) }

    // Check if all required fields are filled
    val isFormValid = remember(formState) {
        formState.name.isNotBlank() &&
                formState.email.isNotBlank() &&
                formState.password.isNotBlank() &&
                formState.confirmPassword.isNotBlank()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // ViewModel reactive state
    val loading by viewModel.loading.collectAsState() // StateFlow
    val signUpState by viewModel.signUpState.collectAsState() // StateFlow

    // Screen dimensions and responsive settings
    val screenDimensions = rememberScreenDimensions()
    val scrollState = rememberScrollState()


    // Validation logic
    fun validateAndSignUp() {
        if (loading || signUpState.status == LoadingState.Status.LOADING) return

        val validationErrors = validateForm(formState)
        formErrors = validationErrors

        if (validationErrors.hasErrors) return

        focusManager.clearFocus()
        keyboardController?.hide()

        viewModel.signUp(formState.name, formState.email, formState.password) { success, errorMsg ->
            if (success) {
                onSignUpClick(formState.name, formState.email, formState.password)
                navController.navigate(ReaderScreens.ReaderHomeScreen.name) {
                    popUpTo(ReaderScreens.CreateAccountScreen.name) { inclusive = true }
                }
            } else {
                val error = errorMsg ?: "Sign up failed"
                formErrors = formErrors.copy(generalError = error)
                coroutineScope.launch { snackbarHostState.showSnackbar(error) }
            }
        }
    }



    val content = @Composable {
        // App Logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenDimensions.imageHeight),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.reader_logo),
                contentDescription = "Illustration of books and a mug",
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
                bottom = if (screenDimensions.isCompactHeight)
                    SignUpConstants.SMALL_SPACING.dp
                else
                    SignUpConstants.SMALL_SPACING.dp * 2
            )
        )

        // Form Fields
        SignUpTextField(
            value = formState.name,
            onValueChange = {
                formState = formState.copy(name = it)
                formErrors = formErrors.copy(nameError = null)
            },
            label = "Name",
            error = formErrors.nameError,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(SignUpConstants.DEFAULT_SPACING.dp))

        SignUpTextField(
            value = formState.email,
            onValueChange = {
                formState = formState.copy(email = it)
                formErrors = formErrors.copy(emailError = null)
            },
            label = "Email",
            error = formErrors.emailError,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(SignUpConstants.DEFAULT_SPACING.dp))

        SignUpTextField(
            value = formState.password,
            onValueChange = {
                formState = formState.copy(password = it)
                formErrors = formErrors.copy(passwordError = null)
            },
            label = "Password",
            error = formErrors.passwordError,
            isPassword = true,
            passwordVisible = formState.passwordVisible,
            onPasswordVisibilityToggle = {
                formState = formState.copy(passwordVisible = !formState.passwordVisible)
            },
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(SignUpConstants.DEFAULT_SPACING.dp))

        SignUpTextField(
            value = formState.confirmPassword,
            onValueChange = {
                formState = formState.copy(confirmPassword = it)
                formErrors = formErrors.copy(confirmPasswordError = null)
            },
            label = "Confirm Password",
            error = formErrors.confirmPasswordError,
            isPassword = true,
            passwordVisible = formState.confirmPasswordVisible,
            onPasswordVisibilityToggle = {
                formState = formState.copy(confirmPasswordVisible = !formState.confirmPasswordVisible)
            },
            imeAction = ImeAction.Done,
            onImeAction = { validateAndSignUp() }
        )

        Spacer(modifier = Modifier.height(SignUpConstants.DEFAULT_SPACING.dp))

        // Remember Me Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SignUpConstants.SMALL_SPACING.dp),
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

        // Sign Up Button
        SignUpButton(
            onClick = { validateAndSignUp() },
            isEnabled = isFormValid,
            isLoading = loading || signUpState.status == LoadingState.Status.LOADING,
            screenDimensions = screenDimensions
        )

        // General Error Display
        if (formErrors.generalError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formErrors.generalError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(
            if (screenDimensions.isCompactHeight)
                8.dp
            else
                SignUpConstants.LARGE_SPACING.dp
        ))

        LogInPrompt(navController)

        Spacer(modifier = Modifier.height(SignUpConstants.EXTRA_LARGE_SPACING.dp))
    }



    Scaffold(
        topBar = { SignUpTopAppBar(navController) },
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
                        SignUpConstants.DEFAULT_SPACING.dp
                    else
                        24.dp
                )
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}
