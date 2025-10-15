package com.example.supplelab.presentation.screens.admin


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.supplelab.R
import com.example.supplelab.presentation.componenets.InfoCard
import com.example.supplelab.presentation.componenets.productCard.ProductCard
import com.example.supplelab.presentation.profile.LoadingCard
import com.example.supplelab.ui.theme.BebasNeueFont
import com.example.supplelab.ui.theme.ButtonPrimary
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.IconPrimary
import com.example.supplelab.ui.theme.Surface
import com.example.supplelab.ui.theme.TextPrimary
import com.example.supplelab.util.DisplayResult
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onNavigationIconClicked: () -> Unit,
    onNavigationToManageProduct: (String?) -> Unit
) {
    val viewModel : AdminPanelViewModel = koinViewModel()
    val products = viewModel.products.collectAsState()
    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ADMIN PANEL",
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onNavigationToManageProduct(null)
                },
                containerColor = ButtonPrimary,
                contentColor = IconPrimary,
                content = {
                    Icon(
                        painter = painterResource(R.drawable.plus),
                        contentDescription = "Add Icon",
                        tint = IconPrimary
                    )
                }
            )
        }
    ) { paddingValues ->
        products.value.DisplayResult(
            modifier = Modifier.padding(paddingValues),
            onLoading ={ LoadingCard(modifier = Modifier.fillMaxSize())},
            onSuccess = { lastProducts ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = lastProducts,
                        key = {it.id}
                    ) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onNavigationToManageProduct(product.id) }
                        )
                    }
                }
            },
            onError = {message ->
                InfoCard(
                    icon = R.drawable.cat,
                    title = "Oops!",
                    subtitle = message,
                )
            }
        )
    }
}