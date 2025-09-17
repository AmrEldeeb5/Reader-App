package com.example.reader.screens.SignUp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reader.R
import com.example.reader.navigation.ReaderScreens
import com.example.reader.screens.login.RememberMeBox
import com.example.reader.screens.login.RememberMeBoxState
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.text.input.ImeAction
import com.example.reader.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    onSignUpClick: (String, String, String) -> Unit,
    viewModel: SignUpScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // Local input state
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisibility by rememberSaveable { mutableStateOf(false) }

    // Field error state
    var nameError by rememberSaveable { mutableStateOf<String?>(null) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmPasswordError by rememberSaveable { mutableStateOf<String?>(null) }
    var generalError by rememberSaveable { mutableStateOf<String?>(null) }

    // Check if all required fields are filled
    val isFormValid = remember(name, email, password, confirmPassword) {
        name.isNotBlank() &&
        email.isNotBlank() &&
        password.isNotBlank() &&
        confirmPassword.isNotBlank()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // ViewModel reactive state
    val loading by viewModel.loading.collectAsState() // StateFlow
    val signUpState by viewModel.signUpState.collectAsState() // StateFlow

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val screenWidth = configuration.screenWidthDp
    val isCompactHeight = screenHeight < 600
    val isVeryNarrow = screenWidth < 360
    val isShort = isCompactHeight

    val scrollState = rememberScrollState()

    val imageHeight = remember(screenHeight) {
        val fraction = when {
            screenHeight < 520 -> 0.20f
            screenHeight < 560 -> 0.22f
            screenHeight < 600 -> 0.24f
            else -> 0.30f
        }
        (screenHeight * fraction).dp.coerceIn(120.dp, 240.dp)
    }

    fun triggerSignUp() {
        if (loading || signUpState.status == LoadingState.Status.LOADING) return
        // Reset errors
        nameError = null; emailError = null; passwordError = null; confirmPasswordError = null; generalError = null
        var valid = true
        if (name.isBlank()) { nameError = "Name is required"; valid = false }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { emailError = "Invalid email format"; valid = false }
        if (password.length < 6) { passwordError = "Password must be at least 6 characters"; valid = false }
        if (confirmPassword != password) { confirmPasswordError = "Passwords do not match"; valid = false }
        if (!valid) return
        focusManager.clearFocus()
        keyboardController?.hide()
        viewModel.signUp(name, email, password) { success, errorMsg ->
            if (success) {
                onSignUpClick(name, email, password) // external hook if caller needs it
                navController.navigate(ReaderScreens.ReaderHomeScreen.name) {
                    popUpTo(ReaderScreens.CreateAccountScreen.name) { inclusive = true }
                }
            } else {
                generalError = errorMsg ?: "Sign up failed"
                coroutineScope.launch { snackbarHostState.showSnackbar(generalError!!) }
            }
        }
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
                .padding(top = 1.dp, start = 20.dp, end = 20.dp, bottom = if (isCompactHeight) 12.dp else 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                modifier = Modifier.padding(bottom = if (isCompactHeight) 4.dp else 8.dp)
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = null },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )
            if (nameError != null) {
                Text(nameError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )
            if (emailError != null) {
                Text(emailError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(imageVector = image, contentDescription = if (passwordVisibility) "Hide password" else "Show password")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = passwordError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )
            if (passwordError != null) {
                Text(passwordError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmPasswordError = null },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisibility = !confirmPasswordVisibility }) {
                        Icon(imageVector = image, contentDescription = if (confirmPasswordVisibility) "Hide password" else "Show password")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = confirmPasswordError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { triggerSignUp() })
            )
            if (confirmPasswordError != null) {
                Text(confirmPasswordError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
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

            Button(
                onClick = { triggerSignUp() },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid && !loading && signUpState.status != LoadingState.Status.LOADING,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isFormValid) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                if (loading || signUpState.status == LoadingState.Status.LOADING) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = if (isFormValid) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            if (generalError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(generalError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(if (isShort) 8.dp else 16.dp))

            LogInPrompt(navController)

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
