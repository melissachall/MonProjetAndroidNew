package ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
    var phoneNumber by remember { mutableStateOf("") }
    val phonePattern = Regex("^[567]\\d{8}$")
    val isPhoneNumberValid = phonePattern.matches(phoneNumber)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        // Back Icon
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

        // Illustration
        Image(
            painter = painterResource(Res.drawable.forget),
            contentDescription = "Forgot Password Illustration",
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Title
        Text(
            text = "Forgot Password",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextColor
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Subtitle
        Text(
            text = "Don't worry! It happens. Please enter the phone number associated with your account.",
            fontSize = 16.sp,
            color = SecondTextColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Phone Number Label
        Text(
            text = "Phone Number",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start),
            color = TextColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phone Number Input Field
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("1712345678", color = SecondTextColor) },
            leadingIcon = {
                Text(
                    text = "+213 ▼",
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

        Spacer(modifier = Modifier.height(30.dp))

        // Send Reset Link Button
        Button(
            onClick = {
                if (isPhoneNumberValid) {
                    navigator.push(OTPScreen("dummy_otp")) // TODO: Replace with real logic
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(PrimaryColor),
            shape = RoundedCornerShape(12.dp),
            enabled = isPhoneNumberValid
        ) {
            Text(
                text = "Send Reset Link",
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
