package com.example.supplelab.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.supplelab.presentation.screens.home.cart.CartScreen
import com.example.supplelab.presentation.screens.home.category.CategoryScreen
import com.example.supplelab.presentation.screens.home.products_overview.ProductOverviewScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface HomeTabKey : NavKey

@Serializable
object HomeTab : HomeTabKey


@Serializable
object CartTab : HomeTabKey

@Serializable
object GridTab : HomeTabKey

@Composable
fun HomeTabsNavContent(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    onNavigateToDetails: (String) -> Unit = {}
){
    val homeBackStack = rememberNavBackStack(HomeTab)
    val cartBackStack = rememberNavBackStack(CartTab)
    val gridBackStack = rememberNavBackStack(GridTab)

    // Animated content with slide and fade transitions
    AnimatedContent(
        targetState = selectedIndex,
        modifier = modifier,
        transitionSpec = {
            val slideDirection = if (targetState > initialState) 1 else -1
            val animationDuration = 200

            slideInHorizontally(
                initialOffsetX = { fullWidth -> slideDirection * fullWidth / 3 },
                animationSpec = tween(durationMillis = animationDuration)
            ) + fadeIn(
                animationSpec = tween(durationMillis = animationDuration)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { fullWidth -> -slideDirection * fullWidth / 3 },
                animationSpec = tween(durationMillis = animationDuration)
            ) + fadeOut(
                animationSpec = tween(durationMillis = animationDuration)
            )
        },
        label = "tab_transition"
    ) { targetIndex ->
        when (targetIndex) {
            0 -> {
                NavDisplay(
                    modifier = Modifier.fillMaxSize(),
                    backStack = homeBackStack,
                    entryProvider = { key ->
                        when (key) {
                            is HomeTab -> NavEntry(key) {
                                HomeTabRoot(onNavigateToDetails = onNavigateToDetails)
                            }
                            else -> error("Unknown key for HomeTab backstack: $key")
                        }
                    }
                )
            }
            1 -> {
                NavDisplay(
                    modifier = Modifier.fillMaxSize(),
                    backStack = cartBackStack,
                    entryProvider = { key ->
                        when (key) {
                            is CartTab -> NavEntry(key) { CartTabRoot() }
                            else -> error("Unknown key for CartTab backstack: $key")
                        }
                    }
                )
            }
            2 -> {
                NavDisplay(
                    modifier = Modifier.fillMaxSize(),
                    backStack = gridBackStack,
                    entryProvider = { key ->
                        when (key) {
                            is GridTab -> NavEntry(key) { GridTabRoot() }
                            else -> error("Unknown key for GridTab backstack: $key")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun HomeTabRoot(
    onNavigateToDetails: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ProductOverviewScreen(navigateToDetails = onNavigateToDetails)
    }
}

@Composable
private fun CartTabRoot() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CartScreen()
    }
}

@Composable
private fun GridTabRoot() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CategoryScreen()
    }
}
