package ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.AuthRepository
import data.getAuthRepository
import data.isNetworkAvailable
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import theme.*
import travelbuddy.composeapp.generated.resources.*

data class ResetPasswordScreen(val oobCode: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        ResetPasswordScreenView(
            navigator = navigator,
            oobCode = oobCode,
            authRepository = getAuthRepository()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreenView(
    navigator: Navigator,
    oobCode: String,
    authRepository: AuthRepository
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var passwordStrength by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    val isMatching = newPassword.length >= 6 && newPassword == confirmPassword
    val isValid = isMatching && passwordStrength >= 2

    val coroutineScope = rememberCoroutineScope()

    // Network state
    var isNetworkConnected by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        isNetworkConnected = isNetworkAvailable()
    }

    // Password strength check
    fun checkPasswordStrength(pwd: String): Int {
        var score = 0
        if (pwd.length >= 8) score++
        if (pwd.any { it.isDigit() }) score++
        if (pwd.any { it.isUpperCase() }) score++
        if (pwd.any { !it.isLetterOrDigit() }) score++
        return score
    }

    LaunchedEffect(newPassword) {
        passwordStrength = checkPasswordStrength(newPassword)
    }

    // Snackbar feedback
    LaunchedEffect(showSnackbar) {
        if (showSnackbar && errorMessage != null) {
            snackbarHostState.showSnackbar(errorMessage!!)
            showSnackbar = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Reset Your Password", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextColor)
            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "newPasswordField" },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            painter = painterResource(
                                if (isPasswordVisible) Res.drawable.password_opened else Res.drawable.password_closed
                            ),
                            contentDescription = "Toggle Password",
                            tint = PrimaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = SecondTextColor.copy(alpha = 0.5f),
                    cursorColor = PrimaryColor,
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor
                )
            )
            // Password strength indicator
            Row(modifier = Modifier.align(Alignment.Start)) {
                val colors = listOf(MaterialTheme.colorScheme.error, SecondTextColor, PrimaryColor, PrimaryColor)
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(40.dp)
                            .background(if (passwordStrength > index) colors[index] else SecondTextColor.copy(alpha = 0.2f))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
            Text(
                text = when (passwordStrength) {
                    0, 1 -> "Mot de passe faible"
                    2 -> "Mot de passe moyen"
                    3, 4 -> "Mot de passe fort"
                    else -> ""
                },
                color = when (passwordStrength) {
                    0, 1 -> MaterialTheme.colorScheme.error
                    2 -> SecondTextColor
                    else -> PrimaryColor
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm New Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "confirmPasswordField" },
                visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                        Icon(
                            painter = painterResource(
                                if (isConfirmPasswordVisible) Res.drawable.password_opened else Res.drawable.password_closed
                            ),
                            contentDescription = "Toggle Confirm Password",
                            tint = PrimaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = SecondTextColor.copy(alpha = 0.5f),
                    cursorColor = PrimaryColor,
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor
                )
            )

            if (!isMatching && confirmPassword.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    if (newPassword.length < 6) "Password must be at least 6 characters" else "Passwords do not match",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!isNetworkConnected) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Aucune connexion internet.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    if (!isNetworkConnected) {
                        errorMessage = "Pas de connexion internet."
                        showSnackbar = true
                        return@Button
                    }
                    if (isValid && !isLoading) {
                        isLoading = true
                        errorMessage = null
                        //val coroutineScope = rememberCoroutineScope()
                        coroutineScope.launch {
                            val result = authRepository.confirmPasswordReset(oobCode, newPassword)
                            if (result.isSuccess) {
                                navigator.popUntil { it is LoginScreen }
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Failed to reset password."
                                showSnackbar = true
                            }
                            isLoading = false
                        }
                    }
                },
                enabled = isValid && !isLoading && isNetworkConnected,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .semantics { contentDescription = "resetPasswordButton" },
                colors = ButtonDefaults.buttonColors(PrimaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Reset Password", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}