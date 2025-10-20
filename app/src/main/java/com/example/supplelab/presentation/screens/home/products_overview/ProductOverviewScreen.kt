package com.example.supplelab.presentation.screens.home.products_overview

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.supplelab.R
import com.example.supplelab.presentation.componenets.InfoCard
import com.example.supplelab.presentation.componenets.productCard.ProductCard
import com.example.supplelab.presentation.profile.LoadingCard
import com.example.supplelab.presentation.screens.home.component.MainProductCard
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.TextPrimary
import com.example.supplelab.util.Constants.ALPHA_HALF
import com.example.supplelab.util.DisplayResult
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductOverviewScreen(
    navigateToDetails:(String) -> Unit
) {
    val viewModel: ProductOverviewViewModel = koinViewModel()
    val products by viewModel.products.collectAsState()
    val listState = rememberLazyListState()
    val centeredIndex: Int? by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                val itemCenter = item.offset + item.size / 2
                kotlin.math.abs(itemCenter - viewportCenter)
            }?.index
        }
    }


    products.DisplayResult(
        onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },

        onSuccess = { productState ->
            AnimatedContent(
                targetState = productState
            ) { state ->
                if (state.newProducts.isNotEmpty() || state.discountedProducts.isNotEmpty()){
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            state = listState,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            itemsIndexed(
                                items = state.newProducts,
                                key = { index, item -> item.id}
                            ) { index , product ->
                                val isLarge = index == centeredIndex
                                val animatedScale by animateFloatAsState(
                                    targetValue = if(isLarge) 0.9f else 0.8f,
                                    animationSpec = tween(300)
                                )
                                MainProductCard(
                                    modifier = Modifier
                                        .scale(animatedScale)
                                        .height(300.dp)
                                        .fillParentMaxWidth(0.6f),
                                    product = product,
                                    isLarge = isLarge,
                                    onProductClick = {navigateToDetails(it)}
                                )

                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(ALPHA_HALF),
                            text = "Discounted Products",
                            fontSize = FontSize.EXTRA_REGULAR,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyColumn(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalArrangement = spacedBy(12.dp),
                        ) {
                            items(
                                items = state.discountedProducts,
                                key = { it.id}
                            ) { product ->
                                ProductCard(
                                    product = product,
                                    modifier = Modifier.fillMaxSize(),
                                    onClick = {navigateToDetails(it)}
                                )
                            }
                        }
                    }
                } else{
                    InfoCard(
                        icon = R.drawable.cat,
                        title = "Nothing here!",
                        subtitle = "Products not found",
                    )
                }
            }
        },
        onError = { errorMessage ->
            InfoCard(
                modifier = Modifier.fillMaxSize(),
                icon = R.drawable.cat,
                title = "Oops!",
                subtitle = errorMessage
            )
        },

    )

}