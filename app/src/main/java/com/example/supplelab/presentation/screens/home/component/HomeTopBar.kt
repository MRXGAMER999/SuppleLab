package com.example.supplelab.presentation.screens.home.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.supplelab.R
import com.example.supplelab.ui.theme.BebasNeueFont
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.IconPrimary
import com.example.supplelab.ui.theme.Surface
import com.example.supplelab.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    selectedIndex: Int,
    onNavigationIconClicked: () -> Unit,
    actionsContent: (@Composable () -> Unit),
    isDrawerOpened: Boolean
) {
    val titles = listOf("HOME", "CARD", "CATEGORIES")
    CenterAlignedTopAppBar(
        title = {
            AnimatedContent(
                targetState = selectedIndex
            ) { selectedIndex ->
                Text(
                    text = titles.getOrElse(selectedIndex) { "Home" },
                    fontFamily = BebasNeueFont,
                    fontSize = FontSize.LARGE,
                    color = TextPrimary
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                onNavigationIconClicked()
            }) {
                Icon(
                    painter = painterResource(
                        if (isDrawerOpened) R.drawable.close else R.drawable.menu
                    ),
                    contentDescription = "Menu Icon",
                    tint = IconPrimary
                )
            }
        },
        actions = {
            AnimatedVisibility(
                visible = selectedIndex == 1
            ) {
                actionsContent()
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Surface,
            scrolledContainerColor = Surface,
            navigationIconContentColor = IconPrimary,
            titleContentColor = TextPrimary,
            actionIconContentColor = IconPrimary
        )
    )
}