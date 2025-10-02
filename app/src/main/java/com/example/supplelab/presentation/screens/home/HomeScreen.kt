package com.example.supplelab.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.supplelab.R
import com.example.supplelab.navigation.HomeTabsNavContent
import com.example.supplelab.ui.theme.IconPrimary
import com.example.supplelab.ui.theme.IconSecondary
import com.example.supplelab.ui.theme.SurfaceLighter

// Bottom bar model

data class BottomNavItem(
    val title: String,
    val icon: Painter,
    val selectedIconColor: Color,
    val unselectedIconColor: Color,
    val hasNews: Boolean,
)

@Composable
fun HomeScreen() {
    // Define bottom items
    val items = listOf(
        BottomNavItem(
            title = "Home",
            icon = painterResource(R.drawable.home),
            selectedIconColor = IconSecondary,
            unselectedIconColor = IconPrimary,
            hasNews = false
        ),
        BottomNavItem(
            title = "Cart",
            icon = painterResource(R.drawable.shopping_cart),
            selectedIconColor = IconSecondary,
            unselectedIconColor = IconPrimary,
            hasNews = false
        ),
        BottomNavItem(
            title = "Grid",
            icon = painterResource(R.drawable.grid),
            selectedIconColor = IconSecondary,
            unselectedIconColor = IconPrimary,
            hasNews = false
        )
    )

    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.background(SurfaceLighter)
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            // Switch active tab. Best practice: keep each tab's own back stack.
                            selectedItemIndex = index
                        },
                        icon = {
                            BadgedBox(
                                badge = { if (item.hasNews) Badge() }
                            ) {
                                Icon(
                                    painter = item.icon,
                                    contentDescription = item.title,
                                    tint = if (index == selectedItemIndex) item.selectedIconColor else item.unselectedIconColor
                                )
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            HomeTabsNavContent(selectedIndex = selectedItemIndex)
        }
    }
}