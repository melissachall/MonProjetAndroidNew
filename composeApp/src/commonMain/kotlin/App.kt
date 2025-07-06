import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import di.HomeScreenModelProvider
import okio.FileSystem
import theme.TravelAppTheme
import ui.component.BottomMenuBar
import ui.component.tabs
import ui.screen.CartTab
import ui.screen.FavoriteTab
import ui.screen.HomeTab
import ui.screen.GeminiTab
import ui.screen.LoginScreen
import ui.screen.ProfileTab
import util.AnimateVisibility

/*@Composable
internal fun App() {
    TravelAppTheme {

        setSingletonImageLoaderFactory { context ->
            getAsyncImageLoader(context)
        }

        val viewModel = HomeScreenModelProvider.homeScreenModel

        val bottomNavBarVisibility by viewModel.bottomNavBarVisible.collectAsState()

        Navigator(LoginScreen) { navigator ->
            val currentScreen = navigator.lastItem

            Scaffold(
                content = { padding ->
                    currentScreen.Content()
                },
                bottomBar = {
                    val viewModel = HomeScreenModelProvider.homeScreenModel
                    val bottomNavBarVisibility by viewModel.bottomNavBarVisible.collectAsState()

                    AnimateVisibility(
                        visible = bottomNavBarVisibility,
                        modifier = Modifier.wrapContentSize(Alignment.BottomStart)
                    ) {
                        BottomMenuBar(tabs = tabs) { selectedTab ->
                            when (selectedTab) {
                                HomeTab -> navigator.push(HomeTab)
                                FavoriteTab -> navigator.push(FavoriteTab)
                                CartTab -> navigator.push(CartTab)
                                GeminiTab -> navigator.push(GeminiTab)
                                ProfileTab -> navigator.push(ProfileTab)
                            }
                        }
                    }
                }
            )
        }
    }
}*/

@Composable
internal fun App() {
    TravelAppTheme {

        setSingletonImageLoaderFactory { context ->
            getAsyncImageLoader(context)
        }

        Navigator(LoginScreen) { navigator ->
            Scaffold(
                content = {
                    navigator.lastItem.Content()
                }
            )
        }
    }
}



internal expect fun openUrl(url: String?)

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

expect fun ByteArray.toComposeImageBitmap(): ImageBitmap
