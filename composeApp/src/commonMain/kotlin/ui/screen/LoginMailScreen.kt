package ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import data.getAuthRepository
import data.isNetworkAvailable
import data.AuthService
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import theme.*
import travelbuddy.composeapp.generated.resources.*
import ui.screen.RegisterScreen
import ui.screen.ForgotPasswordScreen
import ui.screen.LoginScreen // For phone navigation, adapt as needed
import ui.app.TabbedScreen // Import TabbedScreen for navigation after login

@OptIn(ExperimentalMaterial3Api::class)
object LoginScreenMail : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current ?: error("No Navigator found")
        LoginScreenMailView(navigator = navigator)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenMailView(
    navigator: Navigator
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    val isEmailValid = emailPattern.matches(email)

    val coroutineScope = rememberCoroutineScope()
    val authRepository = getAuthRepository()
    var isNetworkConnected by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isNetworkConnected = isNetworkAvailable()
        }
    }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar && errorMessage != null) {
            snackbarHostState.showSnackbar(errorMessage!!)
            showSnackbar = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_left),
                        contentDescription = "Back",
                        modifier = Modifier.size(40.dp),
                        tint = TextColor
                    )
                }

                Button(
                    onClick = { /* Change language */ },
                    colors = ButtonDefaults.buttonColors(White),
                    shape = RoundedCornerShape(20.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text("English", color = PrimaryColor)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(Res.drawable.logofonce),
                contentDescription = "Logo",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Enter your email",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start),
                color = TextColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "loginEmailField" },
                placeholder = { Text("example@email.com", color = SecondTextColor) },
                shape = RoundedCornerShape(14.dp),
                trailingIcon = {
                    Icon(
                        painter = painterResource(
                            if (isEmailValid) Res.drawable.check else Res.drawable.check_before
                        ),
                        contentDescription = "Email Validation",
                        tint = if (isEmailValid) PrimaryColor else SecondTextColor,
                        modifier = Modifier.size(24.dp)
                    )
                },
                isError = !isEmailValid && email.isNotEmpty(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = SecondTextColor.copy(alpha = 0.5f),
                    focusedLabelColor = PrimaryColor,
                    unfocusedLabelColor = SecondTextColor,
                    cursorColor = PrimaryColor,
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Enter your password",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start),
                color = TextColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "loginPasswordField" },
                placeholder = { Text("**************", color = SecondTextColor) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(14.dp),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                if (passwordVisible) Res.drawable.password_opened else Res.drawable.password_closed
                            ),
                            contentDescription = "Toggle Password",
                            tint = PrimaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = SecondTextColor.copy(alpha = 0.5f),
                    focusedLabelColor = PrimaryColor,
                    unfocusedLabelColor = SecondTextColor,
                    cursorColor = PrimaryColor,
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor
                )
            )

            if (!isNetworkConnected) {
                Text(
                    "Aucune connexion internet.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (!isNetworkConnected) {
                        errorMessage = "Pas de connexion internet."
                        showSnackbar = true
                        return@Button
                    }
                    if (isEmailValid && password.isNotEmpty() && !isLoading) {
                        isLoading = true
                        errorMessage = null
                        coroutineScope.launch {
                            val result = authRepository.signInWithEmail(email, password)
                            isLoading = false
                            if (result.isSuccess) {
                                navigator.replace(TabbedScreen) // Go to main app tabs after login
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Erreur de connexion"
                                showSnackbar = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "mailLoginButton" },
                colors = ButtonDefaults.buttonColors(PrimaryColor),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && isEmailValid && password.isNotEmpty() && isNetworkConnected
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Connexion...", color = White)
                } else {
                    Text("Login", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                Text("Don't have an account?", fontSize = 14.sp, color = SecondTextColor)
                Text(
                    " Sign Up",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor,
                    modifier = Modifier
                        .clickable { navigator.push(RegisterScreen) }
                        .semantics { contentDescription = "signupLink" }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("or", fontSize = 14.sp, color = SecondTextColor)

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    AuthService.launchGoogleSignIn(
                        clientId = "YOUR_WEB_CLIENT_ID", // Replace with your clientId
                        requestCode = 555
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "googleLoginButton" },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextColor),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(SecondTextColor.copy(alpha = 0.5f))
                )
            ) {
                Image(
                    painter = painterResource(Res.drawable.google),
                    contentDescription = "Google",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Continue with Google", color = TextColor)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = { navigator.push(LoginScreen) },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "phoneLoginButton" },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextColor),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(SecondTextColor.copy(alpha = 0.5f))
                )
            ) {
                Image(
                    painter = painterResource(Res.drawable.phone),
                    contentDescription = "Phone",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Continue with Phone Number", color = TextColor)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("or", fontSize = 14.sp, color = SecondTextColor)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Forgot password?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor,
                modifier = Modifier
                    .clickable { navigator.push(ForgotPasswordScreen) }
                    .semantics { contentDescription = "forgotPasswordLink" }
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}