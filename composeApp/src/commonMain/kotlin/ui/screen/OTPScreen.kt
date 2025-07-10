package ui.screen

import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import data.AuthService // Import the AuthService
import com.google.firebase.auth.PhoneAuthProvider // Import PhoneAuthProvider
import kotlinx.coroutines.launch // Import for coroutine scope
import data.getAuthRepository


@OptIn(ExperimentalMaterial3Api::class)
data class OTPScreen(val verificationId: String?) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        OTPScreenView(navigator = navigator, verificationId = verificationId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPScreenView(navigator: Navigator, verificationId: String?) {
    var otpCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope() // Coroutine scope for suspend functions
    val authRepository = getAuthRepository()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
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
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter 6-digit code", color = SecondTextColor) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
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
                if (otpCode.length == 6 && verificationId != null && !isLoading) {
                    isLoading = true
                    errorMessage = null
                    val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
                    coroutineScope.launch {
                        val result = authRepository.signInWithPhoneAuthCredential(credential)
                        isLoading = false
                        if (result.isSuccess) {
                            // OTP verified, navigate to HomeTab or appropriate screen
                            navigator.push(HomeTab) // Or your main app screen
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Erreur de vérification OTP."
                        }
                    }
                } else {
                    errorMessage = "Please enter a valid 6-digit code"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(PrimaryColor),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
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
            Text("Didn\'t receive code?", fontSize = 14.sp, color = SecondTextColor)
            TextButton(onClick = { navigator.pop() }) {
                Text(
                    "Resend",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            }
        }
    }
}
