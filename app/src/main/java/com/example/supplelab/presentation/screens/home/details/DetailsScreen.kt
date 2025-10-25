package com.example.supplelab.presentation.screens.home.details

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.supplelab.R
import com.example.supplelab.domain.model.ProductCategory
import com.example.supplelab.domain.model.QuantityCounterSize
import com.example.supplelab.presentation.componenets.InfoCard
import com.example.supplelab.presentation.componenets.PrimaryButton
import com.example.supplelab.presentation.componenets.TopNotification
import com.example.supplelab.presentation.profile.LoadingCard
import com.example.supplelab.presentation.screens.home.component.FlavorChip
import com.example.supplelab.presentation.screens.home.component.QuantityCounter
import com.example.supplelab.ui.theme.BebasNeueFont
import com.example.supplelab.ui.theme.BorderIdle
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.IconPrimary
import com.example.supplelab.ui.theme.RobotoCondensedFont
import com.example.supplelab.ui.theme.Surface
import com.example.supplelab.ui.theme.SurfaceLighter
import com.example.supplelab.ui.theme.TextPrimary
import com.example.supplelab.ui.theme.TextSecondary
import com.example.supplelab.util.DisplayResult
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    onNavigationIconClicked:() -> Unit,
    id: String
){
    val viewModel : DetailsScreenViewModel = koinViewModel()
    val product by viewModel.product.collectAsState()
    val quantity = viewModel.quantity
    val selectedFlavor = viewModel.selectedFlavor
    
    var showNotification by remember { mutableStateOf(false) }
    var notificationMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        viewModel.loadProduct(id)
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            containerColor = Surface,
            topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Details",
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
                    QuantityCounter(
                        size = QuantityCounterSize.Large,
                        value = quantity,
                        onDecrement = viewModel::updateQuantity,
                        onIncrement = viewModel::updateQuantity
                    )
                    Spacer(modifier = Modifier.width(16.dp))
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
    ){ paddingValues ->
        product.DisplayResult(
            onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
            onSuccess = { selectedProduct ->
                Column {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp)
                            .padding(top = 12.dp)
                    ) {
                        // Memoize image request to avoid recreating on each recomposition
                        val context = LocalContext.current
                        val imageRequest = remember(selectedProduct.thumbnail) {
                            ImageRequest.Builder(context)
                                .data(selectedProduct.thumbnail)
                                .crossfade(true)
                                .build()
                        }
                        
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 1.dp,
                                    color = BorderIdle,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            model = imageRequest,
                            contentDescription = "Product Image",
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AnimatedContent (
                        targetState = selectedProduct.category
                    ){ category ->
                        if (ProductCategory.valueOf(category) == ProductCategory.Accessories){
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.size(14.dp),
                                    painter = painterResource(id = R.drawable.weight),
                                    contentDescription = "Weight Icon",
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${selectedProduct.weight}g",
                                    fontSize = FontSize.REGULAR,
                                    color = TextPrimary,
                                )
                            }
                        }
                    }
                    Text(
                        text = "$${selectedProduct.price}",
                        fontSize = FontSize.MEDIUM,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = selectedProduct.title,
                            fontSize = FontSize.MEDIUM,
                            fontWeight = FontWeight.Medium,
                            fontFamily = RobotoCondensedFont(),
                            color = TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = selectedProduct.description,
                            fontSize = FontSize.REGULAR,
                            lineHeight = FontSize.REGULAR * 1.3,
                            color = TextPrimary,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .background(if (selectedProduct.flavors?.isNotEmpty() == true) SurfaceLighter
                            else Surface)
                            .padding(24.dp)
                    ) {
                        if(selectedProduct.flavors?.isNotEmpty() == true){
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                selectedProduct.flavors.forEach { flavor ->
                                    FlavorChip(
                                        flavor = flavor,
                                        isSelected = selectedFlavor == flavor,
                                        onClick = {viewModel.updateFlavor(flavor)}
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))

                        }
                        PrimaryButton(
                            icon = R.drawable.shopping_cart,
                            text = "Add to Cart",
                            enabled = if (selectedProduct.flavors?.isNotEmpty() == true) selectedFlavor != null
                            else true,
                            onClick = {
                                viewModel.addItemToCard(
                                    onSuccess = {
                                        isSuccess = true
                                        notificationMessage = "Item added to cart successfully"
                                        showNotification = true
                                    },
                                    onError = { message ->
                                        isSuccess = false
                                        notificationMessage = message
                                        showNotification = true
                                    }
                                )
                            }
                        )
                    }
                }
            },
            onError = { message ->
                InfoCard(
                    icon = R.drawable.cat,
                    title = "Oops!",
                    subtitle = message
                )
            }
        )
    }

        // Top notification banner
        TopNotification(
            visible = showNotification,
            message = notificationMessage,
            isSuccess = isSuccess,
            onDismiss = { showNotification = false },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

}