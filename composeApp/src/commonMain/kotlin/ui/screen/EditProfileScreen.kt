package ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import travelbuddy.composeapp.generated.resources.Res
import travelbuddy.composeapp.generated.resources.back
import travelbuddy.composeapp.generated.resources.ci_location
import travelbuddy.composeapp.generated.resources.profile_icon
import theme.*
import util.BOTTOM_NAV_SPACE

object EditProfileScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        EditProfileScreenView(navigator = navigator)
    }
}

@Composable
fun EditProfileScreenView(navigator: Navigator) {
    // États pour les champs de saisie
    var firstName by remember { mutableStateOf("John") }
    var lastName by remember { mutableStateOf("Doe") }
    var email by remember { mutableStateOf("john.doe@example.com") }
    var phone by remember { mutableStateOf("+33 6 12 34 56 78") }
    var location by remember { mutableStateOf("Denpasar, Bali") }
    var bio by remember { mutableStateOf("Passionné de voyages et d'aventures. J'aime découvrir de nouveaux endroits et partager mes expériences.") }

    // État pour les messages de validation
    var showSaveMessage by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = BOTTOM_NAV_SPACE)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            item {
                EditProfileHeader(navigator = navigator)
            }

            item {
                EditProfilePhotoSection()
            }

            item {
                EditProfileFormSection(
                    firstName = firstName,
                    onFirstNameChange = { firstName = it },
                    lastName = lastName,
                    onLastNameChange = { lastName = it },
                    email = email,
                    onEmailChange = { email = it },
                    phone = phone,
                    onPhoneChange = { phone = it },
                    location = location,
                    onLocationChange = { location = it },
                    bio = bio,
                    onBioChange = { bio = it }
                )
            }

            item {
                EditProfileActionButtons(
                    onSave = {
                        showSaveMessage = true
                        // Ici vous pouvez ajouter la logique de sauvegarde
                    },
                    onCancel = { navigator.pop() }
                )
            }

            if (showSaveMessage) {
                item {
                    SaveSuccessMessage {
                        showSaveMessage = false
                        navigator.pop()
                    }
                }
            }
        }
    }
}

@Composable
private fun EditProfileHeader(navigator: Navigator) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            modifier = Modifier
                .size(36.dp)
                .clickable { navigator.pop() },
            painter = painterResource(Res.drawable.back),
            contentDescription = "Retour"
        )

        Text(
            text = "Modifier le profil",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextColor
        )

        // Espace pour équilibrer le layout
        Spacer(modifier = Modifier.size(36.dp))
    }
}

@Composable
private fun EditProfilePhotoSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            // Photo de profil
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(PrimaryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(80.dp),
                    painter = painterResource(Res.drawable.profile_icon),
                    contentDescription = "Photo de profil",
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(PrimaryColor)
                )
            }

            // Bouton d'édition de photo
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PrimaryColor)
                    .align(Alignment.BottomEnd)
                    .clickable { /* Logique pour changer la photo */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✎",
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Changer la photo",
            style = MaterialTheme.typography.bodySmall,
            color = PrimaryColor,
            modifier = Modifier.clickable { /* Logique pour changer la photo */ }
        )
    }
}

@Composable
private fun EditProfileFormSection(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Informations personnelles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextColor
            )

            // Prénom et Nom sur la même ligne
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EditProfileTextField(
                    value = firstName,
                    onValueChange = onFirstNameChange,
                    label = "Prénom",
                    modifier = Modifier.weight(1f)
                )

                EditProfileTextField(
                    value = lastName,
                    onValueChange = onLastNameChange,
                    label = "Nom",
                    modifier = Modifier.weight(1f)
                )
            }

            EditProfileTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                keyboardType = KeyboardType.Email
            )

            EditProfileTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = "Téléphone",
                keyboardType = KeyboardType.Phone
            )

            EditProfileTextField(
                value = location,
                onValueChange = onLocationChange,
                label = "Localisation",
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(Res.drawable.ci_location),
                        contentDescription = null,
                        tint = PrimaryColor
                    )
                }
            )

            EditProfileTextField(
                value = bio,
                onValueChange = onBioChange,
                label = "Biographie",
                maxLines = 4,
                singleLine = false
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                color = SecondTextColor,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        maxLines = maxLines,
        singleLine = singleLine,
        leadingIcon = leadingIcon,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = PrimaryColor,
            unfocusedBorderColor = SecondTextColor.copy(alpha = 0.5f),
            focusedLabelColor = PrimaryColor,
            unfocusedLabelColor = SecondTextColor,
            cursorColor = PrimaryColor,
            focusedTextColor = TextColor,
            unfocusedTextColor = TextColor
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
private fun EditProfileActionButtons(
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Bouton Annuler
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = SecondTextColor
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(SecondTextColor.copy(alpha = 0.5f))
            )
        ) {
            Text(
                text = "Annuler",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Bouton Sauvegarder
        Button(
            onClick = onSave,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor,
                contentColor = White
            )
        ) {
            Text(
                text = "Sauvegarder",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SaveSuccessMessage(onDismiss: () -> Unit) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000) // Afficher le message pendant 2 secondes
        onDismiss()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryColor.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "✓",
                color = PrimaryColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Profil mis à jour avec succès !",
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

