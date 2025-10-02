package com.example.supplelab.navigation

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
){
    val homeBackStack = rememberNavBackStack(HomeTab)
    val cartBackStack = rememberNavBackStack(CartTab)
    val gridBackStack = rememberNavBackStack(GridTab)

    val activeBackStack = when (selectedIndex) {
        0 -> homeBackStack
        1 -> cartBackStack
        else -> gridBackStack
    }

    NavDisplay(
        modifier = modifier,
        backStack = activeBackStack,
        entryProvider = { key ->
            when (key) {
                is HomeTab -> NavEntry(key) { HomeTabRoot() }
                is CartTab -> NavEntry(key) { CartTabRoot() }
                is GridTab -> NavEntry(key) { GridTabRoot() }
                else -> error("Unknown HomeTabKey: $key")
            }
        }
    )
}

@Composable
private fun HomeTabRoot() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Home tab")
    }
}

@Composable
private fun CartTabRoot() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Cart tab")
    }
}

@Composable
private fun GridTabRoot() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Grid tab")
    }
}


