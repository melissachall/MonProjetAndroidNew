package ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import data.FakeArticles
import kotlinx.coroutines.launch
import model.Destination
import org.jetbrains.compose.resources.stringResource
import travelbuddy.composeapp.generated.resources.*
import ui.component.*
import ui.viewmodel.HomeScreenModel
import util.BOTTOM_NAV_SPACE

@OptIn(ExperimentalMaterialApi::class)
@Composable
actual fun HomeScreenView(viewModel: HomeScreenModel) {
    val destinations by viewModel.destinations.collectAsState()
    val nearestDestinations = FakeArticles.destinations
    val categories by viewModel.categories.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val tabNavigator = LocalTabNavigator.current
    val navigator = LocalNavigator.currentOrThrow

    val sortedDestinations = remember(destinations) {
        destinations.sortedByDescending { it.popularity ?: 0 }
    }

    var refreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            coroutineScope.launch {
                refreshing = true
                viewModel.reloadData()
                refreshing = false
            }
        }
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = BOTTOM_NAV_SPACE)
    ) {
        Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
            when {
                isLoading -> ShimmerLoadingScreen()

                error != null -> ErrorScreen(
                    message = error ?: "Unknown error",
                    onRetry = { viewModel.reloadData() }
                )

                sortedDestinations.isEmpty() -> EmptyStateScreen(
                    message = "No destinations found. Try another category!"
                )

                else -> VerticalScrollLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),

                    // HEADER SECTION
                    ChildLayout(
                        contentType = "HEADER_SECTION",
                        content = {
                            homeHeader {
                                tabNavigator.current = ProfileTab
                            }
                        }
                    ),

                    // CATEGORY VIEW ALL
                    ChildLayout(
                        contentType = "CATEGORY_VIEW_ALL",
                        content = {
                            TitleWithViewAllItem(
                                stringResource(Res.string.category),
                                stringResource(Res.string.view_all),
                                Res.drawable.arrow_forward
                            )
                        }
                    ),

                    // CATEGORY SECTION
                    ChildLayout(
                        contentType = "CATEGORY_SECTION",
                        content = {
                            loadCategoryItems(categories) { category ->
                                viewModel.filterDestinationsByCategory(category)
                            }
                        }
                    ),

                    // DESTINATION LARGE SECTION
                    ChildLayout(
                        contentType = "DESTINATION_LARGE_SECTION",
                        content = {
                            AnimatedVisibility(
                                visible = sortedDestinations.isNotEmpty(),
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                loadDestinationLargeItems(
                                    destinations = sortedDestinations,
                                    checkFavorite = { viewModel.checkFavorite(it) },
                                    addFavorite = { viewModel.addFavorite(it) },
                                    removeFavorite = { viewModel.removeFavorite(it) },
                                    onItemClicked = {
                                        viewModel.setBottomNavBarVisible(false)
                                        navigator.push(DestinationDetailScreen(it))
                                    }
                                )
                            }
                        }
                    ),

                    // DESTINATION VIEW ALL
                    ChildLayout(
                        contentType = "DESTINATION_VIEW_ALL",
                        content = {
                            TitleWithViewAllItem(
                                stringResource(Res.string.popular_destination),
                                stringResource(Res.string.view_all),
                                Res.drawable.arrow_forward
                            )
                        }
                    ),

                    // DESTINATION SMALL SECTION
                    ChildLayout(
                        contentType = "DESTINATION_SMALL_SECTION",
                        items = sortedDestinations,
                        content = { item ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                LoadItemAfterSafeCast<Destination>(item) {
                                    destinationSmallItem(it) {
                                        viewModel.setBottomNavBarVisible(false)
                                        navigator.push(DestinationDetailScreen(it))
                                    }
                                }
                            }
                        }
                    ),

                    // NEAREST LOCATIONS
                    ChildLayout(
                        contentType = "DESTINATION_VIEW_ALL",
                        content = {
                            TitleWithViewAllItem(
                                "Nearest your location",
                                stringResource(Res.string.view_all),
                                Res.drawable.arrow_forward
                            )
                        }
                    ),

                    ChildLayout(
                        contentType = "NEAREST_LOCATIONS",
                        content = {
                            LazyRow(
                                modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(nearestDestinations) {
                                    AnimatedVisibility(
                                        visible = true,
                                        enter = fadeIn(),
                                        exit = fadeOut()
                                    ) {
                                        NearestLocationItem(it) {
                                            viewModel.setBottomNavBarVisible(false)
                                            navigator.push(DestinationDetailScreen(it))
                                        }
                                    }
                                }
                            }
                        }
                    ),

                    // ARTICLES SECTION
                    ChildLayout(
                        contentType = "DESTINATION_VIEW_ALL",
                        content = {
                            TitleWithViewAllItem(
                                "Articles",
                                stringResource(Res.string.view_all),
                                Res.drawable.arrow_forward
                            )
                        }
                    ),

                    ChildLayout(
                        contentType = "ARTICLES",
                        content = {
                            Column(
                                modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FakeArticles.articles.forEach {
                                    AnimatedVisibility(
                                        visible = true,
                                        enter = fadeIn(),
                                        exit = fadeOut()
                                    ) {
                                        ArticleCard(
                                            modifier = Modifier,
                                            article = it
                                        ) {
                                            viewModel.setBottomNavBarVisible(false)
                                            navigator.push(ArticleDetailScreen(it))
                                        }
                                    }
                                }
                            }
                        }
                    )
                )
            }

            PullRefreshIndicator(
                refreshing,
                pullRefreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun ShimmerLoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: (() -> Unit)? = null) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = MaterialTheme.colorScheme.error)
            if (onRetry != null) {
                Button(onClick = onRetry) { Text("Retry") }
            }
        }
    }
}

@Composable
fun EmptyStateScreen(
    message: String,
    buttonText: String = "Explore",
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message)
            if (onClick != null) {
                Button(onClick = onClick) { Text(buttonText) }
            }
        }
    }
}
