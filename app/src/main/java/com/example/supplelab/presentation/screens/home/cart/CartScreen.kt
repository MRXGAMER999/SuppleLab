package com.example.supplelab.presentation.screens.home.cart

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.supplelab.R
import com.example.supplelab.presentation.componenets.InfoCard
import com.example.supplelab.presentation.componenets.TopNotification
import com.example.supplelab.presentation.profile.LoadingCard
import com.example.supplelab.presentation.screens.home.component.CartItemCard
import com.example.supplelab.util.DisplayResult
import com.example.supplelab.util.RequestState
import org.koin.androidx.compose.koinViewModel

@Composable
fun CartScreen(){
    val viewModel : CartScreenViewModel = koinViewModel()
    val cartItemsWithProducts by viewModel.cartItemsWithProducts.collectAsState(initial = RequestState.Loading)

    var showNotification by remember { mutableStateOf(false) }
    var notificationMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
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
                                onDecrementClick = { quantity ->
                                    viewModel.updateCartItemQuantity(
                                        id = pair.first.id,
                                        quantity = quantity,
                                        onSuccess = {

                                        },
                                        onError = { message ->
                                            showNotification = true
                                            notificationMessage = message
                                            isSuccess = false
                                        }

                                    )
                                },
                                onIncrementClick = { quantity ->
                                    viewModel.updateCartItemQuantity(
                                        id = pair.first.id,
                                        quantity = quantity,
                                        onSuccess = {

                                        },
                                        onError = { message ->
                                            showNotification = true
                                            notificationMessage = message
                                            isSuccess = false
                                        }

                                    )
                                },
                                onRemoveClick = {
                                    viewModel.removeItemFromCart(
                                        id = pair.first.id,
                                        onSuccess = {
                                            showNotification = true
                                            notificationMessage = "Item removed from cart"
                                            isSuccess = true
                                        },
                                        onError = {message ->
                                            showNotification = true
                                            notificationMessage = message
                                            isSuccess = false
                                        }
                                    )
                                }
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
            transitionSpec = fadeIn() togetherWith  fadeOut()
        )

        TopNotification(
            visible = showNotification,
            message = notificationMessage,
            isSuccess = isSuccess,
            onDismiss = { showNotification = false },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}