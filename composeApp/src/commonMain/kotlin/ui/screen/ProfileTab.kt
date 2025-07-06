package ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import travelbuddy.composeapp.generated.resources.Res
import travelbuddy.composeapp.generated.resources.arrow_forward
import travelbuddy.composeapp.generated.resources.back
import travelbuddy.composeapp.generated.resources.ci_location
import travelbuddy.composeapp.generated.resources.humberg_icon
import travelbuddy.composeapp.generated.resources.menu_fav
import travelbuddy.composeapp.generated.resources.profile_icon
import travelbuddy.composeapp.generated.resources.profile_tab
import theme.*
import ui.component.Tabx
import util.BOTTOM_NAV_SPACE

data object ProfileTab : Tabx {
    override fun defaultTitle(): StringResource = Res.string.profile_tab
    override fun defaultIcon(): DrawableResource = Res.drawable.profile_icon

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.profile_tab)
            val icon = painterResource(Res.drawable.profile_icon)

            return remember {
                TabOptions(
                    index = 4u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(ProfileScreen)
    }
}

object ProfileScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        ProfileScreenView(navigator = navigator)
    }
}

@Composable
fun ProfileScreenView(navigator: Navigator) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = BOTTOM_NAV_SPACE)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ProfileHeader()
            }

            /*item {
                ProfileInfoCard()
            }*/

            item {
                ProfileMenuSection(navigator = navigator)
            }

            item {
                SettingsSection()
            }

            item {
                LogoutButton()
            }
        }
    }
}

@Composable
private fun ProfileHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
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

        Spacer(modifier = Modifier.height(16.dp))

        // Nom de l'utilisateur
        Text(
            text = "John Doe",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextColor
        )

        // Email
        Text(
            text = "john.doe@example.com",
            style = MaterialTheme.typography.bodyMedium,
            color = SecondTextColor
        )
    }
}

/*@Composable
private fun ProfileInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Informations du voyage",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProfileStatItem(
                    title = "Voyages",
                    value = "12"
                )
                ProfileStatItem(
                    title = "Favoris",
                    value = "8"
                )
                ProfileStatItem(
                    title = "Avis",
                    value = "24"
                )
            }
        }
    }
}*/

@Composable
private fun ProfileStatItem(title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = SecondTextColor
        )
    }
}

@Composable
private fun ProfileMenuSection(navigator: Navigator) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {

            ProfileMenuItem(
                icon = Res.drawable.profile_icon,
                title = "Modifier le profil",
                onClick = { navigator.push(EditProfileScreen) }
            )
        }
    }
}

@Composable
private fun SettingsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            ProfileMenuItem(
                icon = Res.drawable.humberg_icon,
                title = "Paramètres",
                onClick = { /* Navigation vers paramètres */ }
            )

            ProfileMenuItem(
                icon = Res.drawable.humberg_icon,
                title = "Notifications",
                onClick = { /* Navigation vers notifications */ }
            )

            ProfileMenuItem(
                icon = Res.drawable.humberg_icon,
                title = "Aide et support",
                onClick = { /* Navigation vers aide */ }
            )

            /*ProfileMenuItem(
                icon = Res.drawable.humberg_icon,
                title = "À propos",
                onClick = { /* Navigation vers à propos */ }
            )*/
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: DrawableResource,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(icon),
            contentDescription = title,
            tint = PrimaryColor
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = TextColor
        )

        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(Res.drawable.arrow_forward),
            contentDescription = "Aller à $title",
            tint = SecondTextColor
        )
    }
}

@Composable
private fun LogoutButton() {
    Button(
        onClick = { /* Logique de déconnexion */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Red.copy(alpha = 0.1f),
            contentColor = Red
        )
    ) {
        Text(
            text = "Se déconnecter",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

