package com.example.supplelab.presentation.screens.home.products_overview

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.supplelab.R
import com.example.supplelab.presentation.componenets.InfoCard
import com.example.supplelab.presentation.componenets.productCard.ProductCard
import com.example.supplelab.presentation.profile.LoadingCard
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.TextPrimary
import com.example.supplelab.util.Constants.ALPHA_HALF
import com.example.supplelab.util.DisplayResult
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductOverviewScreen() {
    val viewModel: ProductOverviewViewModel = koinViewModel()
    val products = viewModel.products.collectAsState()

    products.value.DisplayResult(
        onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },

        onSuccess = { productList ->
            AnimatedContent(
                targetState = productList
            ) { products ->
                if (products.isNotEmpty()){
                    Column {
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
                                items = products.sortedBy { it.createdAt }.take(3),
                                key = { it.id}
                            ) { product ->
                                ProductCard(
                                    product = product,
                                    modifier = Modifier.fillMaxSize(),
                                    onClick = { }
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