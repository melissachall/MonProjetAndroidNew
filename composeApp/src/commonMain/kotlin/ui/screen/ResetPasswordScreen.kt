// In: composeApp/src/commonMain/kotlin/ui/screen/ResetPasswordScreen.kt
package ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import data.AuthRepository // We will create this interface
import data.getAuthRepository // We will create this factory function
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import theme.*
import travelbuddy.composeapp.generated.resources.*

// Step 1: Modify the Screen class to accept the reset code
data class ResetPasswordScreen(val oobCode: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        // Pass the code and the repository to the view
        ResetPasswordScreenView(
            navigator = navigator,
            oobCode = oobCode,
            authRepository = getAuthRepository()
        )
    }
}

// Step 2: Implement the full view with logic
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
    val coroutineScope = rememberCoroutineScope()

    val isMatching = newPassword.length >= 6 && newPassword == confirmPassword

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ... (Your existing Image and Text Composables can go here) ...
        Text("Reset Your Password", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextColor)
        Spacer(modifier = Modifier.height(30.dp))

        // New Password Field
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            // ... (Add your styling and trailing icon) ...
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password Field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm New Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            // ... (Add your styling and trailing icon) ...
        )

        if (!isMatching && confirmPassword.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                if (newPassword.length < 6) "Password must be at least 6 characters" else "Passwords do not match",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (isMatching && !isLoading) {
                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch {
                        val result = authRepository.confirmPasswordReset(oobCode, newPassword)
                        if (result.isSuccess) {
                            // Success! Navigate to the login screen.
                            navigator.popUntil { it is LoginScreen } // Go back to login
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Failed to reset password."
                        }
                        isLoading = false
                    }
                }
            },
            enabled = isMatching && !isLoading, // Disable button when not matching or loading
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(PrimaryColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Reset Password", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Display error message if any
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}