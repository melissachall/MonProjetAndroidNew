package ui.screen

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import di.HomeScreenModelProvider
import ui.component.BottomMenuBar
import ui.component.tabs
import util.AnimateVisibility

object TabbedHome : cafe.adriel.voyager.core.screen.Screen {
    @Composable
    override fun Content() {
        val viewModel = HomeScreenModelProvider.homeScreenModel
        val bottomNavBarVisible by viewModel.bottomNavBarVisible.collectAsState()

        TabNavigator(tabs.first()) { // généralement HomeTab
            Scaffold(
                content = { CurrentTab() },
                bottomBar = {
                    AnimateVisibility(
                        visible = bottomNavBarVisible,
                        modifier = Modifier.wrapContentSize(Alignment.BottomStart)
                    ) {
                        BottomMenuBar(tabs = tabs) { selectedTab ->
                            it.current = selectedTab
                        }
                    }
                }
            )
        }
    }
}
