package com.example.reader.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.example.reader.ui.theme.animatedScaffoldContainerColor
import com.example.reader.ui.theme.animatedTopBarContainerColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(navController: NavController) {
    val auth = remember { FirebaseAuth.getInstance() }
    val user = auth.currentUser

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showCurrent by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun validate(): String? {
        return when {
            currentPassword.isBlank() -> "Current password is required"
            newPassword.isBlank() -> "New password is required"
            newPassword.length < 6 -> "New password must be at least 6 characters"
            confirmPassword.isBlank() -> "Please confirm your new password"
            newPassword != confirmPassword -> "Passwords do not match"
            currentPassword == newPassword -> "New password must be different"
            else -> null
        }
    }

    fun changePassword() {
        if (isLoading) return
        val validationError = validate()
        if (validationError != null) {
            errorMessage = validationError
            scope.launch { snackbarHostState.showSnackbar(validationError) }
            return
        }
        if (user == null || user.email.isNullOrBlank()) {
            errorMessage = "No authenticated email user found. Please login again."
            scope.launch { snackbarHostState.showSnackbar(errorMessage!!) }
            return
        }

        isLoading = true
        errorMessage = null
        focusManager.clearFocus()
        keyboardController?.hide()

        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
        user.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            isLoading = false
                            if (updateTask.isSuccessful) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Password updated successfully")
                                    delay(800)
                                    navController.popBackStack()
                                }
                            } else {
                                val msg = updateTask.exception?.localizedMessage ?: "Failed to update password"
                                errorMessage = msg
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                        }
                } else {
                    isLoading = false
                    val msg = reauthTask.exception?.localizedMessage ?: "Re-authentication failed"
                    errorMessage = msg
                    scope.launch { snackbarHostState.showSnackbar(msg) }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change Password") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = animatedTopBarContainerColor()
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = animatedScaffoldContainerColor()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally){
            // Current password
            OutlinedTextField(
                value = currentPassword,
                onValueChange = {
                    currentPassword = it
                    errorMessage = null
                },
                label = { Text("Current password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showCurrent) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showCurrent = !showCurrent }) {
                        Icon(
                            imageVector = if (showCurrent) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showCurrent) "Hide" else "Show"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { /* move focus handled by system */ })
            )

            Spacer(Modifier.height(12.dp))

            // New password
            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorMessage = null
                },
                label = { Text("New password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showNew = !showNew }) {
                        Icon(
                            imageVector = if (showNew) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showNew) "Hide" else "Show"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { /* move focus handled by system */ })
            )

            Spacer(Modifier.height(12.dp))

            // Confirm password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = null
                },
                label = { Text("Confirm new password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(
                            imageVector = if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showConfirm) "Hide" else "Show"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { changePassword() })
            )

            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { changePassword() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Update Password")
                }
            }
        }
    }
}
