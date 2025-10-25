package com.example.supplelab.presentation.screens.home.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.supplelab.R
import com.example.supplelab.domain.model.Customer
import com.example.supplelab.ui.theme.IconPrimary
import com.example.supplelab.ui.theme.IconSecondary
import com.example.supplelab.ui.theme.SurfaceDarker
import com.example.supplelab.ui.theme.SurfaceLighter
import com.example.supplelab.util.Constants.ALPHA_DISABLED
import com.example.supplelab.util.Constants.ALPHA_HALF
import com.example.supplelab.util.RequestState

data class BottomNavItem(
    val title: String,
    val icon: Painter,
    val selectedIconColor: Color,
    val unselectedIconColor: Color,
    val hasNews: Boolean,
)


@Composable
fun HomeBottomBar(
    selectedItemIndex: Int,
    onSelectedItemIndexChange: (Int) -> Unit,
    customer: RequestState<Customer>
){
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
    NavigationBar(
        modifier = Modifier
            .padding(
                horizontal = 12.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
        ,
        containerColor = SurfaceLighter
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedItemIndex == index
            val animatedTint by animateColorAsState(
                targetValue = if (isSelected) item.selectedIconColor else item.unselectedIconColor,
                label = "icon_tint_animation"
            )

            NavigationBarItem(
                modifier = Modifier,
                selected = isSelected,
                onClick = {
                    onSelectedItemIndexChange(index)
                },
                icon = {
                        Box(contentAlignment = Alignment.TopEnd){
                            Icon(
                                painter = item.icon,
                                contentDescription = item.title,
                                tint = animatedTint
                            )
                            if (index == 1){
                                AnimatedContent(
                                    targetState =  customer
                                ) { customerState ->
                                    if (customerState.isSuccess() && customerState.getSuccessData().cart.isNotEmpty()){
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .offset(x = 4.dp, y = (-4).dp)
                                                .clip(CircleShape)
                                                .background(IconSecondary)
                                        )
                                    }
                                }
                            }
                        }

                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = item.selectedIconColor,
                    unselectedIconColor = item.unselectedIconColor,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

//@Preview(showBackground = true, name = "Bottom Bar Light")
//@Composable
//private fun HomeBottomBarPreview() {
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(SurfaceLighter),
//            contentAlignment = Alignment.BottomCenter
//        ) {
//            var selectedIndex by remember { mutableStateOf(0) }
//
//            HomeBottomBar(
//                selectedItemIndex = selectedIndex,
//                onSelectedItemIndexChange = { selectedIndex = it }
//            )
//        }
//
//}