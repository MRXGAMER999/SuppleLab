package com.example.supplelab.presentation.screens.home.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.supplelab.R
import com.example.supplelab.presentation.componenets.InfoCard
import com.example.supplelab.presentation.profile.LoadingCard
import com.example.supplelab.presentation.screens.home.component.CartItemCard
import com.example.supplelab.util.DisplayResult
import com.example.supplelab.util.RequestState
import org.koin.androidx.compose.koinViewModel

@Composable
fun CartScreen(){
    val viewModel : CartScreenViewModel = koinViewModel()
    val cartItemsWithProducts by viewModel.cartItemsWithProducts.collectAsState(initial = RequestState.Loading)

    cartItemsWithProducts.DisplayResult(
        onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
        onSuccess = {data ->
            if(data.isNotEmpty()){
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = data,
                        key = { it.first.id }
                    ) { pair ->
                        CartItemCard(
                            cartItem = pair.first,
                            product = pair.second,
                            onDecrementClick = {

                            },
                            onIncrementClick = {

                            },
                            onRemoveClick = {}
                        )
                    }
                }
            } else {
                InfoCard(
                    icon = R.drawable.shopping_cart_image,
                    title = "Empty Cart",
                    subtitle = "Check some of our products"
                )
            }
        },
        onError = { message ->
            InfoCard(
                icon = R.drawable.shopping_cart_image,
                title = "Oops!",
                subtitle = message
            )

        },
    )
}