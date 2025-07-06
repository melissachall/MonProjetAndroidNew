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
object LoginScreenMail : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        LoginScreenMailView(navigator = navigator)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenMailView(navigator: Navigator) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    val isEmailValid = emailPattern.matches(email)

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
            modifier = Modifier.fillMaxWidth(),
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
                focusedLabelColor = PrimaryColor,
                unfocusedLabelColor = SecondTextColor,
                cursorColor = PrimaryColor,
                focusedTextColor = TextColor,
                unfocusedTextColor = TextColor
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navigator.push(HomeTab) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(PrimaryColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

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

        Spacer(modifier = Modifier.height(10.dp))

        Text("or", fontSize = 14.sp, color = SecondTextColor)

        Spacer(modifier = Modifier.height(10.dp))

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
            onClick = { navigator.push(LoginScreen) },
            modifier = Modifier.fillMaxWidth(),
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
            modifier = Modifier.clickable { navigator.push(ForgotPasswordScreen) }
        )
    }
}
