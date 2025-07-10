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
import data.getAuthRepository
import data.FirebaseUtils
import kotlinx.coroutines.launch

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
    var firstName by remember { mutableStateOf("") }
    var familyName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    val isEmailValid = emailPattern.matches(email)
    val phonePattern = Regex("^[567]\\d{8}$")
    val isPhoneValid = phonePattern.matches(phoneNumber)
    val isPasswordMatching = password == confirmPassword && password.isNotEmpty()

    val coroutineScope = rememberCoroutineScope()
    val authRepository = getAuthRepository()

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
                    modifier = Modifier.size(36.dp),
                    tint = TextColor
                )
            }
            Button(
                onClick = { /* Change language */ },
                colors = ButtonDefaults.buttonColors(White),
                shape = RoundedCornerShape(18.dp),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Text("English", color = PrimaryColor)
            }
        }

        Image(
            painter = painterResource(Res.drawable.logofonce),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(100.dp)
        )

        TextFieldWithLabel(
            label = "First Name",
            value = firstName,
            onValueChange = { firstName = it },
            placeholder = "Melissa"
        )

        Spacer(modifier = Modifier.height(18.dp))

        TextFieldWithLabel(
            label = "Family Name",
            value = familyName,
            onValueChange = { familyName = it },
            placeholder = "CHALLAM"
        )

        Spacer(modifier = Modifier.height(18.dp))

        TextFieldWithLabel(
            label = "Phone Number",
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = "5XXXXXXXX",
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

        Spacer(modifier = Modifier.height(18.dp))

        TextFieldWithLabel(
            label = "Enter your email",
            value = email,
            onValueChange = { email = it },
            placeholder = "example@email.com",
            trailing = {
                Icon(
                    painter = painterResource(
                        if (isEmailValid) Res.drawable.check else Res.drawable.check_before
                    ),
                    contentDescription = null,
                    tint = if (isEmailValid) PrimaryColor else SecondTextColor,
                    modifier = Modifier.size(24.dp)
                )
            },
            isError = !isEmailValid && email.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(18.dp))

        PasswordFieldWithLabel(
            label = "Enter your password",
            password = password,
            onPasswordChange = { password = it },
            visible = passwordVisible,
            onVisibilityChange = { passwordVisible = it }
        )

        Spacer(modifier = Modifier.height(18.dp))

        PasswordFieldWithLabel(
            label = "Confirm Password",
            password = confirmPassword,
            onPasswordChange = { confirmPassword = it },
            visible = confirmPasswordVisible,
            onVisibilityChange = { confirmPasswordVisible = it }
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (firstName.isNotEmpty() && familyName.isNotEmpty() && isPhoneValid && isEmailValid && isPasswordMatching && !isLoading) {
                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch {
                        val result = authRepository.signUpWithEmail(email, password)
                        isLoading = false
                        if (result.isSuccess) {
                            val uid = result.getOrNull()
                            uid?.let { userId ->
                                val cleanedPhoneNumber = if (phoneNumber.startsWith("0")) phoneNumber.drop(1) else phoneNumber

                                FirebaseUtils.createUserInFirestore(
                                    uid = userId,
                                    firstName = firstName,
                                    lastName = familyName,
                                    phoneNumber = cleanedPhoneNumber,
                                    email = email,
                                    onSuccess = { navigator.push(LoginScreenMail) },
                                    onFailure = { e -> errorMessage = "Erreur enregistrement Firestore : ${e.message}" }
                                )
                            }
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Erreur d'inscription"
                        }
                    }
                }
            },
            enabled = firstName.isNotEmpty() && familyName.isNotEmpty() && isPhoneValid && isEmailValid && isPasswordMatching && !isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(PrimaryColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Inscription...", color = White)
            } else {
                Text("Register", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
    trailing: @Composable (() -> Unit)? = null,
    isError: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextColor)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            placeholder = {
                Text(
                    placeholder,
                    color = SecondTextColor,
                    fontSize = 12.sp
                )
            },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 13.sp,
                lineHeight = 14.sp
            ),
            trailingIcon = trailing,
            isError = isError,
            shape = RoundedCornerShape(12.dp),
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
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextColor)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            placeholder = {
                Text(
                    "**************",
                    color = SecondTextColor,
                    fontSize = 12.sp
                )
            },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 13.sp,
                lineHeight = 14.sp
            ),
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onVisibilityChange(!visible) }) {
                    Icon(
                        painter = painterResource(
                            if (visible) Res.drawable.password_opened else Res.drawable.password_closed
                        ),
                        contentDescription = "Toggle Password",
                        tint = PrimaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
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
