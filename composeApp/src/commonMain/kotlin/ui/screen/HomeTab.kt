package ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.DrawableResource
import travelbuddy.composeapp.generated.resources.*
import di.HomeScreenModelProviderr
import ui.viewmodel.HomeScreenModel

import ui.component.Tabx // ← importer Tabx depuis component

@Composable
expect fun HomeScreenView(viewModel: HomeScreenModel)

object HomeTab : Tabx {
    @Composable
    override fun Content() {
        HomeScreenView(HomeScreenModelProviderr.homeScreenModel)
    }

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 0u,
            title = "Home",
            icon = painterResource(resource = Res.drawable.home)
        )

    // OBLIGATOIRE car Tabx le demande !
    override fun defaultTitle(): StringResource = Res.string.home_tab
    override fun defaultIcon(): DrawableResource = Res.drawable.home
}