package ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import org.jetbrains.compose.resources.painterResource
import travelbuddy.composeapp.generated.resources.*
import theme.PrimaryColor
import theme.TextColor
import theme.White
import theme.SecondTextColor

@OptIn(ExperimentalMaterial3Api::class)
object ResetPasswordScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        ResetPasswordScreenView(navigator = navigator)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreenView(navigator: Navigator) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isMatching = newPassword == confirmPassword && newPassword.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(Res.drawable.reset),
            contentDescription = "Reset Password Illustration",
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Reset Password",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextColor
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "Now you can reset your old password.",
            fontSize = 16.sp,
            color = SecondTextColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // New Password
        Text(
            "Enter your new password",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start),
            color = TextColor
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            modifier = Modifier.fillMaxWidth(),
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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = SecondTextColor.copy(alpha = 0.5f),
                cursorColor = PrimaryColor,
                focusedTextColor = TextColor,
                unfocusedTextColor = TextColor
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Confirm Password
        Text(
            "Confirm your new password",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start),
            color = TextColor
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("**************", color = SecondTextColor) },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(14.dp),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        painter = painterResource(
                            if (confirmPasswordVisible) Res.drawable.password_opened else Res.drawable.password_closed
                        ),
                        contentDescription = "Toggle Password",
                        tint = PrimaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
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
                "Passwords do not match",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                // TODO: Implement reset logic
                navigator.pop() // ou HomeTab si tu veux rediriger ailleurs
            },
            enabled = isMatching,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(PrimaryColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Reset Password",
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
