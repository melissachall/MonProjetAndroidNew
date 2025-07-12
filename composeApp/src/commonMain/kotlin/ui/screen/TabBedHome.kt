package ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import di.HomeScreenModelProviderr
import ui.component.BottomMenuBar
import ui.component.tabs
import util.AnimateVisibility
import androidx.activity.compose.BackHandler
import org.jetbrains.compose.resources.painterResource
import travelbuddy.composeapp.generated.resources.*
import ui.component.Tabx

@OptIn(ExperimentalMaterial3Api::class)
object TabbedHome {
    @Composable
    fun Content() {
        val viewModel = HomeScreenModelProviderr.homeScreenModel
        val bottomNavBarVisible by viewModel.bottomNavBarVisible.collectAsState()
        val tabList: List<Tabx> = tabs
        var selectedTab by remember { mutableStateOf(tabList.first()) }

        // Theme toggle
        var isDarkTheme by remember { mutableStateOf(false) }

        // Back stack for tabs
        val tabBackStack = remember { mutableStateListOf(selectedTab) }

        MaterialTheme(
            colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Travel Buddy") },
                        actions = {
                            IconButton(onClick = { isDarkTheme = !isDarkTheme }) {
                                Icon(
                                    painter = painterResource(
                                        if (isDarkTheme) travelbuddy.composeapp.generated.resources.Res.drawable.ic_light_mode
                                        else travelbuddy.composeapp.generated.resources.Res.drawable.ic_dark_mode
                                    ),
                                    contentDescription = if (isDarkTheme) "Light Mode" else "Dark Mode",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    )
                },
                content = { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Crossfade(targetState = selectedTab) { tab: Tabx ->
                            tab.Content()
                        }
                    }
                },
                bottomBar = {
                    AnimateVisibility(
                        visible = bottomNavBarVisible,
                        modifier = Modifier.wrapContentSize(Alignment.BottomStart)
                    ) {
                        BottomMenuBar(tabs = tabList) { tab ->
                            val tabx = tab as Tabx
                            if (tabx != selectedTab) {
                                tabBackStack.add(tabx)
                                selectedTab = tabx
                            }
                        }
                    }
                }
            )

            BackHandler(enabled = tabBackStack.size > 1) {
                tabBackStack.removeLast()
                selectedTab = tabBackStack.last()
            }
        }
    }
}