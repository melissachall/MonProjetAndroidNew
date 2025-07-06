package ui.screen

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.style.TextAlign
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
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val phonePattern = Regex("^[567]\\d{8}$")
    val isPhoneNumberValid = phonePattern.matches(phoneNumber)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
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

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "Enter your mobile number",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start),
            color = TextColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("1712345678", color = SecondTextColor) },
            leadingIcon = {
                Text(
                    "+213 ▼",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 10.dp),
                    color = TextColor
                )
            },
            trailingIcon = {
                val icon = if (isPhoneNumberValid) Res.drawable.check else Res.drawable.check_before
                Icon(
                    painter = painterResource(icon),
                    contentDescription = "Phone Validation",
                    tint = if (isPhoneNumberValid) PrimaryColor else SecondTextColor,
                    modifier = Modifier.size(24.dp)
                )
            },
            isError = !isPhoneNumberValid && phoneNumber.isNotEmpty(),
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
                if (isPhoneNumberValid && !isLoading) {
                    isLoading = true
                    errorMessage = null
                    navigator.push(OTPScreen("dummy_verification_id"))
                } else if (!isPhoneNumberValid) {
                    errorMessage = "Please enter a valid phone number starting with 5, 6, or 7 and 9 digits total"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(PrimaryColor),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                Text("Processing...", color = White)
            } else {
                Text("Login", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Text("Don't have an account?", fontSize = 14.sp, color = SecondTextColor)
            Text(
                " Sign Up",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor,
                modifier = Modifier.clickable { navigator.push(RegisterScreen) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("or", fontSize = 14.sp, color = SecondTextColor)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = { /* Handle Google login */ },
            modifier = Modifier.fillMaxWidth(),
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
            onClick = { navigator.push(LoginScreenMail) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextColor),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = SolidColor(SecondTextColor.copy(alpha = 0.5f))
            )
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
            fontWeight = FontWeight.Bold,
            color = PrimaryColor,
            modifier = Modifier.clickable { navigator.push(ForgotPasswordScreen) }
        )
    }
}
