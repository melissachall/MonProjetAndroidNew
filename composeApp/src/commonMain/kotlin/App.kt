package ui.app

import GeminiTab
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import theme.TravelAppTheme
import ui.screen.CartTab
import ui.screen.FavoriteTab
import ui.screen.HomeTab
import ui.screen.ProfileTab
import ui.screen.LoginScreenMail
import ui.component.BottomMenuBar
import ui.component.tabs
import util.AnimateVisibility
import di.HomeScreenModelProviderr
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import okio.FileSystem
import cafe.adriel.voyager.core.screen.Screen

@Composable
internal fun App() {
    TravelAppTheme {
        setSingletonImageLoaderFactory { context -> getAsyncImageLoader(context) }

        val viewModel = HomeScreenModelProviderr.homeScreenModel
        val bottomNavBarVisibility by viewModel.bottomNavBarVisible.collectAsState()

        Navigator(LoginScreenMail) { navigator ->
            val current = navigator.lastItem
            current.Content()
        }
    }
}

// Définir TabbedScreen ici (pas besoin de fichier séparé)
object TabbedScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = di.HomeScreenModelProviderr.homeScreenModel
        val bottomNavBarVisibility by viewModel.bottomNavBarVisible.collectAsState()
        TabNavigator(HomeTab) { tabNavigator ->
            Scaffold(
                content = { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        tabNavigator.current.Content()
                    }
                },
                bottomBar = {
                    AnimateVisibility(
                        visible = bottomNavBarVisibility,
                        modifier = Modifier.wrapContentSize(Alignment.BottomStart)
                    ) {
                        BottomMenuBar(tabs = tabs) { selectedTab ->
                            tabNavigator.current = selectedTab
                        }
                    }
                }
            )
        }
    }
}

val tabs = listOf(
    HomeTab,
    FavoriteTab,
    CartTab,
    GeminiTab,
    ProfileTab
)

@Composable
expect fun openUrl(url: String?)

expect fun ByteArray.toComposeImageBitmap(): androidx.compose.ui.graphics.ImageBitmap

fun getAsyncImageLoader(context: PlatformContext) =
    ImageLoader.Builder(context)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, 0.3)
                .strongReferencesEnabled(true)
                .build()
        }
        .diskCachePolicy(CachePolicy.ENABLED)
        .networkCachePolicy(CachePolicy.ENABLED)
        .diskCache {
            newDiskCache()
        }
        .crossfade(true)
        .logger(DebugLogger())
        .build()

fun newDiskCache(): DiskCache {
    return DiskCache.Builder()
        .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
        .maxSizeBytes(1024L * 1024 * 1024) // 1GB
        .build()
}