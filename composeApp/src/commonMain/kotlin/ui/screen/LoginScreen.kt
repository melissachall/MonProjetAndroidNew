package ui.screen // Gardez ce package inchangé

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.checkIfUserExists // Importez la fonction expect
import data.getAuthRepository // Importez la fonction expect
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import theme.PrimaryColor
import theme.SecondTextColor
import theme.TextColor
import theme.White
import travelbuddy.composeapp.generated.resources.*

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
    val phonePattern = Regex("^[567]\\d{8}$")
    val isPhoneValid = phonePattern.matches(phoneNumber)
    val authRepository = getAuthRepository() // Obtenez l'instance du dépôt d'authentification
    val coroutineScope = rememberCoroutineScope()

    // Simplifiez les rappels en utilisant directement les lambdas pour éviter l'objet anonyme
    // et clarifier les types de paramètres.
    // Il n'est pas nécessaire de définir 'callbacks' comme un 'remember' objet ici.
    // Vous pouvez passer directement les lambdas aux fonctions.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
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
            modifier = Modifier.fillMaxWidth(),
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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = SecondTextColor.copy(alpha = 0.5f),
                cursorColor = PrimaryColor,
                focusedTextColor = TextColor,
                unfocusedTextColor = TextColor
            )
        )

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
                if (isPhoneValid && !isLoading) {
                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch { // Nouveau coroutineScope pour les opérations asynchrones
                        // Répétition des lignes ci-dessous, j'ai simplifié pour une meilleure logique.
                        // isLoading et errorMessage sont déjà gérés en haut du bloc onClick.

                        val exists = checkIfUserExists("+213$phoneNumber") // Appel de la fonction expect
                        if (exists) {
                            authRepository.sendVerificationCode( // Appel de la fonction expect
                                phoneNumber = "+213$phoneNumber",
                                onVerificationCompleted = { credential ->
                                    // 'credential' est de type Any, tel que défini dans l'expect fun
                                    coroutineScope.launch {
                                        isLoading = true // Affiche le spinner de chargement pendant la connexion
                                        authRepository.signInWithPhoneAuthCredential(credential) // Appel de la fonction expect
                                            .onSuccess {
                                                isLoading = false // Cache le spinner après succès
                                                navigator.push(HomeScreen) // Navigue vers l'écran d'accueil
                                            }
                                            .onFailure { e ->
                                                isLoading = false // Cache le spinner après échec
                                                errorMessage = e.message ?: "Authentication failed."
                                            }
                                    }
                                },
                                onVerificationFailed = { e ->
                                    isLoading = false // Cache le spinner si la vérification échoue
                                    errorMessage = e.message ?: "Phone verification failed."
                                },
                                onCodeSent = { verificationId ->
                                    isLoading = false // Cache le spinner une fois le code envoyé
                                    navigator.push(OTPScreen(verificationId)) // Navigue vers l'écran OTP
                                }
                            )
                        } else {
                            isLoading = false // Cache le spinner si l'utilisateur n'existe pas
                            errorMessage = "You are not signed up yet."
                        }
                    }

                } else if (!isPhoneValid) {
                    errorMessage = "Please enter a valid phone number starting with 5, 6, or 7."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(PrimaryColor),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
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
                modifier = Modifier.clickable { navigator.push(RegisterScreen) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("or", fontSize = 14.sp, color = SecondTextColor)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = { /* TODO: Google login */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextColor),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(SecondTextColor.copy(alpha = 0.5f)))
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
            onClick = { navigator.push(LoginScreenMail) },
            modifier = Modifier.fillMaxWidth(),
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
            modifier = Modifier.clickable { navigator.push(ForgotPasswordScreen) }
        )
    }
}