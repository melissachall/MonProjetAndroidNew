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
object RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        RegisterScreenView(navigator = navigator)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenView(navigator: Navigator) {
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val phonePattern = Regex("^0\\d{9}$")
    val isPhoneValid = phonePattern.matches(phoneNumber)
    val isPasswordMatching = password == confirmPassword && password.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navigator.pop() }) {
                Icon(
                    painter = painterResource(Res.drawable.arrow_left),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp),
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

        TextFieldWithLabel(
            label = "Full Name",
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = "John Doe"
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextFieldWithLabel(
            label = "Enter your mobile number",
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = "0712345678",
            leading = {
                Text("+213 ▼", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextColor)
            },
            trailing = {
                Icon(
                    painter = painterResource(
                        if (isPhoneValid) Res.drawable.check else Res.drawable.check_before
                    ),
                    contentDescription = null,
                    tint = if (isPhoneValid) PrimaryColor else SecondTextColor,
                    modifier = Modifier.size(24.dp)
                )
            },
            isError = !isPhoneValid && phoneNumber.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(20.dp))

        PasswordFieldWithLabel(
            label = "Enter your password",
            password = password,
            onPasswordChange = { password = it },
            visible = passwordVisible,
            onVisibilityChange = { passwordVisible = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        PasswordFieldWithLabel(
            label = "Confirm Password",
            password = confirmPassword,
            onPasswordChange = { confirmPassword = it },
            visible = confirmPasswordVisible,
            onVisibilityChange = { confirmPasswordVisible = it }
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                navigator.push(OTPScreen("dummy_otp"))
            },
            enabled = fullName.isNotEmpty() && isPhoneValid && isPasswordMatching,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(PrimaryColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Register", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Text("Already have an account?", fontSize = 14.sp, color = SecondTextColor)
            Text(
                " Log In",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor,
                modifier = Modifier.clickable { navigator.push(LoginScreen) }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    isError: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextColor)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = SecondTextColor) },
            leadingIcon = leading,
            trailingIcon = trailing,
            isError = isError,
            shape = RoundedCornerShape(14.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = SecondTextColor.copy(alpha = 0.5f),
                cursorColor = PrimaryColor,
                focusedTextColor = TextColor,
                unfocusedTextColor = TextColor
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordFieldWithLabel(
    label: String,
    password: String,
    onPasswordChange: (String) -> Unit,
    visible: Boolean,
    onVisibilityChange: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextColor)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("**************", color = SecondTextColor) },
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onVisibilityChange(!visible) }) {
                    Icon(
                        painter = painterResource(
                            if (visible) Res.drawable.password_opened else Res.drawable.password_closed
                        ),
                        contentDescription = "Toggle Password",
                        tint = PrimaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = SecondTextColor.copy(alpha = 0.5f),
                cursorColor = PrimaryColor,
                focusedTextColor = TextColor,
                unfocusedTextColor = TextColor
            )
        )
    }
}
