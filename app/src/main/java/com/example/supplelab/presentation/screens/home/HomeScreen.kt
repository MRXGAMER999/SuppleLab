package com.example.supplelab.presentation.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.example.supplelab.navigation.HomeTabsNavContent
import com.example.supplelab.presentation.profile.CustomDrawerState
import com.example.supplelab.presentation.screens.home.component.CustomDrawer
import com.example.supplelab.presentation.screens.home.component.HomeBottomBar
import com.example.supplelab.presentation.screens.home.component.HomeTopBar
import com.example.supplelab.ui.theme.Surface
import com.example.supplelab.ui.theme.SurfaceLighter
import com.example.supplelab.presentation.profile.isOpened
import com.example.supplelab.presentation.profile.opposite
import com.example.supplelab.util.Constants.ALPHA_DISABLED


@Composable
fun HomeScreen() {
    val windowInfo = LocalWindowInfo.current
    val screenWidth = with(LocalDensity.current) {
        windowInfo.containerSize.width.toDp()
    }
    var drawerState by remember { mutableStateOf(CustomDrawerState.Closed)}
    val offsetValue = screenWidth / 1.5f
    val animateOffset by animateDpAsState(
        targetValue = if(drawerState.isOpened()) offsetValue else 0.dp
    )
    val animatedBackground by animateColorAsState(
        targetValue = if (drawerState.isOpened()) SurfaceLighter else Surface
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (drawerState.isOpened()) 0.9f else 1f
    )
    val animatedRadius by animateDpAsState(
        targetValue = if (drawerState.isOpened()) 20.dp else 0.dp
    )


    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBackground)
            .systemBarsPadding()
    ) {
        CustomDrawer(
            onProfileClick = { },
            onContactUsClick = { },
            onSignOutClick = { },
            onAdminPanelClick = { }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(animatedRadius))
                .offset(x = animateOffset)
                .scale(animatedScale)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(size = animatedRadius),
                    ambientColor = Color.Black.copy(alpha = ALPHA_DISABLED),
                    spotColor = Color.Black.copy(alpha = ALPHA_DISABLED)
                )
        ) {
            Scaffold(
                containerColor = Surface,
                topBar = {
                    HomeTopBar(
                        selectedItemIndex,
                        onNavigationIconClicked = { drawerState = drawerState.opposite() },
                        isDrawerOpened = drawerState.isOpened()
                    )
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
    }
}