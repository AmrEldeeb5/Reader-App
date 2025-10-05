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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
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
import com.example.reader.components.FormErrors
import com.example.reader.components.LoadingState
import com.example.reader.components.SignUpConstants
import com.example.reader.components.SignUpFormState
import com.example.reader.navigation.ReaderScreens
import com.example.reader.screens.login.RememberMeBox
import com.example.reader.screens.login.RememberMeBoxState
import com.example.reader.utils.rememberResponsiveLayout
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


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
    isLoading: Boolean
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
    viewModel: SignUpScreenViewModel = koinViewModel()
) {
    // Form state management
    var formState by remember { mutableStateOf(SignUpFormState()) }
    var formErrors by remember { mutableStateOf(FormErrors()) }

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

    // Modern responsive layout
    val layout = rememberResponsiveLayout()
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

    Scaffold(
        topBar = { SignUpTopAppBar(navController) },
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
                // App Logo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(layout.imageHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.reader_logo),
                        contentDescription = "Illustration of books and a mug",
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(layout.verticalSpacing / 4))

                // App Title
                Text(
                    text = "Reader",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = if (layout.isCompact)
                        MaterialTheme.typography.headlineLarge
                    else
                        MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = layout.verticalSpacing)
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

                Spacer(modifier = Modifier.height(layout.verticalSpacing))

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

                Spacer(modifier = Modifier.height(layout.verticalSpacing))

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

                Spacer(modifier = Modifier.height(layout.verticalSpacing))

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

                Spacer(modifier = Modifier.height(layout.verticalSpacing))

                // Remember Me Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = layout.verticalSpacing / 6),
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

                // Sign Up Button
                SignUpButton(
                    onClick = { validateAndSignUp() },
                    isEnabled = isFormValid,
                    isLoading = loading || signUpState.status == LoadingState.Status.LOADING
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

                Spacer(modifier = Modifier.height(layout.verticalSpacing))

                LogInPrompt(navController)
            }
        }
    }
}