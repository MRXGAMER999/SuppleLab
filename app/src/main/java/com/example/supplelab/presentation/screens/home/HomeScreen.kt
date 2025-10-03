package com.example.supplelab.presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.supplelab.navigation.HomeTabsNavContent
import com.example.supplelab.presentation.screens.home.component.HomeBottomBar
import com.example.supplelab.presentation.screens.home.component.HomeTopBar
import com.example.supplelab.ui.theme.Surface


@Composable
fun HomeScreen() {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    Scaffold(
        containerColor = Surface,
        topBar = {
            HomeTopBar(selectedItemIndex)
        },
        bottomBar = {
            HomeBottomBar(
                selectedItemIndex = selectedItemIndex,
                onSelectedItemIndexChange = { selectedItemIndex = it }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            HomeTabsNavContent(selectedIndex = selectedItemIndex)
        }
    }
}