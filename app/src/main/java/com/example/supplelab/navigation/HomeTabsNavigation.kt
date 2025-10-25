package com.example.supplelab.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.supplelab.presentation.screens.home.cart.CartScreen
import com.example.supplelab.presentation.screens.home.details.DetailsScreen
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

    // Keep all backstacks alive, show/hide based on selection
    Box(modifier = modifier) {
        // Home tab (index 0)
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .then(if (selectedIndex == 0) Modifier else Modifier.hideComposable()),
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

        // Cart tab (index 1)
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .then(if (selectedIndex == 1) Modifier else Modifier.hideComposable()),
            backStack = cartBackStack,
            entryProvider = { key ->
                when (key) {
                    is CartTab -> NavEntry(key) { CartTabRoot() }
                    else -> error("Unknown key for CartTab backstack: $key")
                }
            }
        )

        // Grid tab (index 2)
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .then(if (selectedIndex == 2) Modifier else Modifier.hideComposable()),
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

/**
 * Hides a composable by making it zero-sized while keeping it in composition.
 * This preserves state and scroll position without rendering or consuming layout space.
 */
private fun Modifier.hideComposable(): Modifier = this.layout { measurable, _ ->
    // Measure with zero constraints but don't place
    measurable.measure(Constraints(0, 0, 0, 0))
    layout(0, 0) {
        // Don't place the composable - it stays in composition but invisible
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
        Text(text = "Grid tab")
    }
}
