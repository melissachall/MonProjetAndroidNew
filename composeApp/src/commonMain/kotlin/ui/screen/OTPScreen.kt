package ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.painterResource
import travelbuddy.composeapp.generated.resources.*
import theme.PrimaryColor
import theme.TextColor
import theme.White
import theme.SecondTextColor
import data.AuthService
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import data.getAuthRepository
import data.isNetworkAvailable

@OptIn(ExperimentalMaterial3Api::class)
data class OTPScreen(val verificationId: String?, val phoneNumber: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        OTPScreenView(navigator = navigator, verificationId = verificationId, phoneNumber = phoneNumber)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPScreenView(navigator: Navigator, verificationId: String?, phoneNumber: String) {
    var otpCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    var timer by remember { mutableStateOf(60) }
    var resendEnabled by remember { mutableStateOf(false) }
    var codeExpired by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val authRepository = getAuthRepository()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    // Network state
    var isNetworkConnected by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isNetworkConnected = isNetworkAvailable()
        }
    }

    // Start timer when entering screen
    LaunchedEffect(Unit) {
        resendEnabled = false
        timer = 60
        codeExpired = false
        while (timer > 0) {
            delay(1_000)
            timer--
        }
        resendEnabled = true
        codeExpired = true
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
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(15.dp))

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

            Spacer(modifier = Modifier.height(30.dp))

            Image(
                painter = painterResource(Res.drawable.logofonce),
                contentDescription = "Logo",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Verification Code",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Enter the 6-digit code sent to your phone number",
                fontSize = 14.sp,
                color = SecondTextColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = otpCode,
                onValueChange = {
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        otpCode = it
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "otpField" },
                placeholder = { Text("Enter 6-digit code", color = SecondTextColor) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = codeExpired || (otpCode.length == 6 && !otpCode.all { it.isDigit() }),
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = SecondTextColor.copy(alpha = 0.5f),
                    cursorColor = PrimaryColor,
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor
                )
            )

            if (codeExpired) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Code expired. Please request a new code.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val networkOk = isNetworkAvailable()
                        if (!networkOk) {
                            errorMessage = "Pas de connexion internet."
                            showSnackbar = true
                            return@launch
                        }
                        if (otpCode.length == 6 && verificationId != null && !isLoading && !codeExpired) {
                            isLoading = true
                            errorMessage = null
                            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
                            val result = authRepository.signInWithPhoneAuthCredential(credential)
                            isLoading = false
                            if (result.isSuccess) {
                                navigator.push(HomeTab)
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Erreur de vérification OTP."
                                showSnackbar = true
                            }
                        } else {
                            errorMessage = if (codeExpired) "Le code a expiré. Renvoyez un code." else "Please enter a valid 6-digit code"
                            showSnackbar = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "otpSubmitButton" },
                colors = ButtonDefaults.buttonColors(PrimaryColor),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && otpCode.length == 6 && !codeExpired && isNetworkConnected
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verifying...", color = White)
                } else {
                    Text("Verify", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Didn't receive code?", fontSize = 14.sp, color = SecondTextColor)
                TextButton(
                    enabled = resendEnabled && isNetworkConnected,
                    onClick = {
                        coroutineScope.launch {
                            val networkOk = isNetworkAvailable()
                            if (!networkOk) {
                                errorMessage = "Pas de connexion internet."
                                showSnackbar = true
                                return@launch
                            }
                            codeExpired = false
                            resendEnabled = false
                            timer = 60
                            authRepository.sendVerificationCode(
                                phoneNumber = phoneNumber,
                                onVerificationCompleted = {},
                                onVerificationFailed = { e ->
                                    errorMessage = e.message ?: "Erreur lors du renvoi du code."
                                    showSnackbar = true
                                },
                                onCodeSent = { _ ->
                                    errorMessage = "Nouveau code envoyé."
                                    showSnackbar = true
                                }
                            )
                            while (timer > 0) {
                                delay(1_000)
                                timer--
                            }
                            resendEnabled = true
                            codeExpired = true
                        }
                    }
                ) {
                    Text(
                        if (resendEnabled) "Resend" else "Resend (${timer}s)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (resendEnabled) PrimaryColor else SecondTextColor
                    )
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}