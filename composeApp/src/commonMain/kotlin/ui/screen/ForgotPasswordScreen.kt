package ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import data.getAuthRepository
import data.isNetworkAvailable
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
object ForgotPasswordScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        ForgotPasswordScreenView(navigator = navigator)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreenView(navigator: Navigator) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    val isEmailValid = emailPattern.matches(email)
    val coroutineScope = rememberCoroutineScope()
    val authRepository = getAuthRepository()

    // Network state
    var isNetworkConnected by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        isNetworkConnected = isNetworkAvailable()
    }

    // Snackbar feedback
    LaunchedEffect(showSnackbar) {
        if (showSnackbar && (errorMessage != null || successMessage != null)) {
            snackbarHostState.showSnackbar(errorMessage ?: successMessage!!)
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
            Spacer(modifier = Modifier.height(18.dp))

            Icon(
                painter = painterResource(Res.drawable.arrow_left),
                contentDescription = "Back",
                tint = TextColor,
                modifier = Modifier
                    .align(Alignment.Start)
                    .size(40.dp)
                    .clickable { navigator.pop() }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(Res.drawable.forget),
                contentDescription = "Forgot Password Illustration",
                modifier = Modifier.size(250.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Forgot Password",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Don't worry! It happens. Please enter the email associated with your account.",
                fontSize = 16.sp,
                color = SecondTextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Email",
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
                    .semantics { contentDescription = "forgotEmailField" },
                placeholder = { Text("example@email.com", color = SecondTextColor) },
                trailingIcon = {
                    val icon = if (isEmailValid) Res.drawable.check else Res.drawable.check_before
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = "Email Validation",
                        tint = if (isEmailValid) PrimaryColor else SecondTextColor,
                        modifier = Modifier.size(24.dp)
                    )
                },
                isError = !isEmailValid && email.isNotEmpty(),
                shape = RoundedCornerShape(14.dp),
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

            if (successMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = successMessage!!,
                    color = PrimaryColor,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
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
                    if (isEmailValid && !isLoading) {
                        isLoading = true
                        errorMessage = null
                        successMessage = null
                        coroutineScope.launch {
                            val result = authRepository.sendPasswordResetEmail(email)
                            isLoading = false
                            if (result.isSuccess) {
                                successMessage = "Un lien de réinitialisation a été envoyé à votre adresse email."
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Erreur lors de l'envoi du lien de réinitialisation."
                                showSnackbar = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "sendResetLinkButton" },
                colors = ButtonDefaults.buttonColors(PrimaryColor),
                shape = RoundedCornerShape(12.dp),
                enabled = isEmailValid && !isLoading && isNetworkConnected
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Envoi...", color = White)
                } else {
                    Text(
                        text = "Send Reset Link",
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
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