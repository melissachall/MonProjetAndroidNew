package ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.AuthService
import data.checkIfUserExists
import data.getAuthRepository
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import theme.PrimaryColor
import theme.SecondTextColor
import theme.TextColor
import theme.White
import travelbuddy.composeapp.generated.resources.*
import data.isNetworkAvailable

@OptIn(ExperimentalMaterial3Api::class)
object LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        LoginScreenView(navigator = navigator)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenView(navigator: Navigator) {
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    val phonePattern = Regex("^[567]\\d{8}$")
    val isPhoneValid = phonePattern.matches(phoneNumber)
    val authRepository = getAuthRepository()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val phoneFocusRequester = remember { FocusRequester() }
    var isNetworkConnected by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isNetworkConnected = isNetworkAvailable()
        }
        phoneFocusRequester.requestFocus()
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
                        tint = TextColor
                    )
                }
                Button(
                    onClick = { /* Switch Language */ },
                    colors = ButtonDefaults.buttonColors(White),
                    shape = RoundedCornerShape(18.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text("English", color = PrimaryColor)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(Res.drawable.logofonce),
                contentDescription = "Logo",
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                "Enter your mobile number",
                fontSize = 14.sp,
                color = TextColor,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(phoneFocusRequester)
                    .semantics { contentDescription = "phoneNumberField" },
                placeholder = { Text("5XXXXXXXX", color = SecondTextColor) },
                leadingIcon = { Text("+213", color = TextColor) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(
                            if (isPhoneValid) Res.drawable.check else Res.drawable.check_before
                        ),
                        contentDescription = "Validation",
                        tint = if (isPhoneValid) PrimaryColor else SecondTextColor,
                        modifier = Modifier.size(22.dp)
                    )
                },
                isError = !isPhoneValid && phoneNumber.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = SecondTextColor.copy(alpha = 0.5f),
                    cursorColor = PrimaryColor,
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor
                )
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Entrez votre numéro sans le 0, juste ex: 612345678",
                fontSize = 12.sp,
                color = SecondTextColor,
                modifier = Modifier.align(Alignment.Start)
            )

            if (!isNetworkConnected) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Aucune connexion internet.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(4.dp))
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
                    coroutineScope.launch {
                        val networkOk = isNetworkAvailable()
                        if (!networkOk) {
                            errorMessage = "Pas de connexion internet."
                            showSnackbar = true
                            return@launch
                        }
                        if (isPhoneValid && !isLoading) {
                            isLoading = true
                            errorMessage = null
                            val exists = checkIfUserExists("+213$phoneNumber")
                            if (exists) {
                                authRepository.sendVerificationCode(
                                    phoneNumber = "+213$phoneNumber",
                                    onVerificationCompleted = { credential ->
                                        coroutineScope.launch {
                                            isLoading = true
                                            authRepository.signInWithPhoneAuthCredential(credential)
                                                .onSuccess {
                                                    isLoading = false
                                                    navigator.push(HomeTab)
                                                }
                                                .onFailure { e ->
                                                    isLoading = false
                                                    errorMessage = e.message ?: "Authentication failed."
                                                    showSnackbar = true
                                                }
                                        }
                                    },
                                    onVerificationFailed = { e ->
                                        isLoading = false
                                        errorMessage = e.message ?: "Phone verification failed."
                                        showSnackbar = true
                                    },
                                    onCodeSent = { verificationId ->
                                        isLoading = false
                                        navigator.push(OTPScreen(verificationId, "+213$phoneNumber"))
                                    }
                                )
                            } else {
                                isLoading = false
                                errorMessage = "You are not signed up yet."
                                showSnackbar = true
                            }
                        } else if (!isPhoneValid) {
                            errorMessage = "Please enter a valid phone number starting with 5, 6, or 7."
                            showSnackbar = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "loginButton" },
                colors = ButtonDefaults.buttonColors(PrimaryColor),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && isPhoneValid && isNetworkConnected
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Processing...", color = White)
                } else {
                    Text("Login", color = White, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row {
                Text("Don't have an account?", fontSize = 14.sp, color = SecondTextColor)
                Text(
                    " Sign Up",
                    fontSize = 14.sp,
                    color = PrimaryColor,
                    modifier = Modifier
                        .clickable { navigator.push(RegisterScreen) }
                        .semantics { contentDescription = "signupLink" }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("or", fontSize = 14.sp, color = SecondTextColor)

            Spacer(modifier = Modifier.height(20.dp))

            // Google Sign-In Button - use AuthService to launch intent
            OutlinedButton(
                onClick = {
                    AuthService.launchGoogleSignIn(
                        clientId = "YOUR_WEB_CLIENT_ID", // Remplace par ton vrai clientId
                        requestCode = 555
                    )
                    // Optionnel: tu peux activer un état de loading ici si tu le veux
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
                if (isLoading) {
                    CircularProgressIndicator(color = TextColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Connexion...", color = TextColor)
                } else {
                    Text("Continue with Google", color = TextColor)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = { navigator.push(LoginScreenMail) },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "mailLoginButton" },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextColor),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(SecondTextColor.copy(alpha = 0.5f)))
            ) {
                Image(
                    painter = painterResource(Res.drawable.mail),
                    contentDescription = "Mail",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Continue with E-mail", color = TextColor)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("or", fontSize = 14.sp, color = SecondTextColor)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Forgot password?",
                fontSize = 14.sp,
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