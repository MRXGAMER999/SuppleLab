package com.example.supplelab.presentation.screens.home.category.category_search

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.supplelab.R
import com.example.supplelab.domain.model.ProductCategory
import com.example.supplelab.presentation.componenets.InfoCard
import com.example.supplelab.presentation.componenets.productCard.ProductCard
import com.example.supplelab.presentation.profile.LoadingCard
import com.example.supplelab.ui.theme.BebasNeueFont
import com.example.supplelab.ui.theme.BorderIdle
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.IconPrimary
import com.example.supplelab.ui.theme.Surface
import com.example.supplelab.ui.theme.SurfaceLighter
import com.example.supplelab.ui.theme.TextPrimary
import com.example.supplelab.util.DisplayResult
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySearchScreen(
    onNavigationIconClicked: () -> Unit,
    category: ProductCategory
){
    val viewModel : CategorySearchViewModel = koinViewModel()
    val products by viewModel.products.collectAsState()

    LaunchedEffect(category) {
        viewModel.loadProductsByCategory(category)
    }
    Scaffold (
        containerColor = Surface,
        topBar = {
            AnimatedContent(
                targetState = false,
                label = "Search Bar Animation"
            ) { visible ->
                if (visible){
//                    SearchBar(
//                        modifier = Modifier
//                            .padding(horizontal = 12.dp)
//                            .fillMaxWidth(),
//                        query = searchQuery,
//                        onQueryChange = viewModel::updateSearchQuery,
//                        onSearch = {},
//                        active = false,
//                        onActiveChange = {},
//                        placeholder = {
//                            Text(
//                                text = "Search Products...",
//                                fontSize = FontSize.REGULAR,
//                                color = TextPrimary
//                            )
//                        },
//                        trailingIcon = {
//                            IconButton(onClick = {
//                                if (searchQuery.isNotEmpty()){
//                                    viewModel.updateSearchQuery("")
//                                } else{
//                                    searchBarVisible = false
//                                }
//                            }) {
//                                Icon(
//                                    painter = painterResource(R.drawable.close),
//                                    contentDescription = "Close Icon",
//                                    tint = IconPrimary
//                                )
//                            }
//                        },
//                        colors = SearchBarDefaults.colors(
//                            containerColor = SurfaceLighter,
//                            dividerColor = BorderIdle
//                        ),
//                        content = {}
//                    )
                } else {
                    TopAppBar(
                        title = {
                            Text(
                                text = category.title,
                                fontFamily = BebasNeueFont(),
                                fontSize = FontSize.LARGE,
                                color = TextPrimary
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                onNavigationIconClicked()
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.back_arrow),
                                    contentDescription = "Menu Icon",
                                    tint = IconPrimary
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                //searchBarVisible = true
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.search),
                                    contentDescription = "Search Icon",
                                    tint = IconPrimary
                                )
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
            }
        }
    ){ paddingValues ->
        products.DisplayResult(
            onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
            onSuccess = { categoryProducts ->
                if (categoryProducts.isNotEmpty()){
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(12.dp)
                    ) {
                        items(
                            count = categoryProducts.size,
                            key = { index -> categoryProducts[index].id }
                        ) { index ->
                            val product = categoryProducts[index]
                            ProductCard(
                                product = product,
                                onClick = { productId ->
                                    // TODO: Navigate to product details
                                }
                            )

                        }
                    }
                } else {
                    InfoCard(
                        icon = R.drawable.cat,
                        title = "Nothing Here",
                        subtitle = "No products found in ${category.title} category."
                    )
                }
            },
            onError = { Message ->
                InfoCard(
                    icon = R.drawable.cat,
                    title = "Oops!",
                    subtitle = Message
                )
            }
        )
    }
}