package com.example.reader.screens.SignUp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.reader.components.FormErrors
import com.example.reader.components.LoadingState
import com.example.reader.components.SignUpConstants
import com.example.reader.components.SignUpFormState
import com.example.reader.navigation.ReaderScreens
import com.example.reader.screens.login.RememberMeBox
import com.example.reader.screens.login.RememberMeBoxState
import com.example.reader.screens.profile.UserProfileViewModel
import com.example.reader.ui.theme.animatedScaffoldContainerColor
import com.example.reader.utils.UserPreferences
import com.example.reader.utils.rememberResponsiveLayout
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reader.screens.SignUp.SignUpScreenViewModel


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
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = MaterialTheme.shapes.medium,
        enabled = isEnabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnabled) MaterialTheme.colorScheme.secondary
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
@Suppress("UNUSED_PARAMETER")
@Composable
fun SignUpScreen(
    navController: NavController,
    onSignUpClick: (String, String, String) -> Unit,
    viewModel: SignUpScreenViewModel = hiltViewModel(),
    isGreenTheme: Boolean = true
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    val userProfileViewModel: UserProfileViewModel = hiltViewModel()

    var formState by remember { mutableStateOf(SignUpFormState()) }
    var formErrors by remember { mutableStateOf(FormErrors()) }

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

    val loading by viewModel.loading.collectAsState()
    val signUpState by viewModel.signUpState.collectAsState()

    val layout = rememberResponsiveLayout()
    val scrollState = rememberScrollState()

    fun validateAndSignUp() {
        if (loading || signUpState.status == LoadingState.Status.LOADING) return

        val validationErrors = validateForm(formState)
        formErrors = validationErrors

        if (validationErrors.hasErrors) return

        focusManager.clearFocus()
        keyboardController?.hide()

        viewModel.signUp(formState.name, formState.email, formState.password) { success, errorMsg ->
            if (success) {
                userProfileViewModel.updateUsername(formState.name.trim())
                if (RememberMeBoxState.rememberMe) {
                    userPrefs.saveCredentials(
                        email = formState.email,
                        password = formState.password,
                        userName = formState.name
                    )
                    userPrefs.setRememberMe(true)
                } else {
                    userPrefs.updateUserName(formState.name.trim())
                    userPrefs.setRememberMe(false)
                }

                onSignUpClick(formState.name, formState.email, formState.password)
                navController.navigate(ReaderScreens.HomeScreen.name) {
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
        containerColor = animatedScaffoldContainerColor(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = layout.horizontalPadding, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = layout.contentMaxWidth)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "By signing up, you agree to our Terms & Conditions",
                        style = if (layout.isCompact)
                            MaterialTheme.typography.bodySmall
                        else
                            MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
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

                Spacer(modifier = Modifier.height(layout.verticalSpacing))

                // Sign Up Button
                SignUpButton(
                    onClick = { validateAndSignUp() },
                    isEnabled = isFormValid,
                    isLoading = loading || signUpState.status == LoadingState.Status.LOADING
                )

                if (formErrors.generalError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formErrors.generalError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                // Divider with "or"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "  or  ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                // Social Login Section
                Text(
                    text = "Join with your favorite Social Media Account",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SocialIconButton(
                        iconRes = R.drawable.icons8_google,
                        contentDescription = "Google login",
                        onClick = { /* TODO: Google Login */ }
                    )
                    SocialIconButton(
                        iconRes = R.drawable.facebook,
                        contentDescription = "Facebook login",
                        onClick = { /* TODO: Facebook Login */ }
                    )
                    SocialIconButton(
                        iconRes = R.drawable.devicon__github,
                        contentDescription = "GitHub login",
                        onClick = { /* TODO: GitHub Login */ }
                    )
                    SocialIconButton(
                        iconRes = R.drawable.devicon__apple,
                        contentDescription = "Apple login",
                        onClick = { /* TODO: Apple Login */ }
                    )
                }

                Spacer(modifier = Modifier.height(layout.verticalSpacing))

                // Already have an account? Sign In
                LogInPrompt(navController)

                // Extra breathing room at the end of the scroll content
                Spacer(modifier = Modifier.height(layout.verticalSpacing))
            }
        }
    }
}

@Composable
fun SocialIconButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(32.dp),
            contentScale = ContentScale.Fit
        )
    }
}
