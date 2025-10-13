package com.example.supplelab.presentation.screens.manageProduct

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.supplelab.R
import com.example.supplelab.domain.model.ProductCategory
import com.example.supplelab.presentation.componenets.AlertTextField
import com.example.supplelab.presentation.componenets.CustomTextField
import com.example.supplelab.presentation.componenets.PrimaryButton
import com.example.supplelab.presentation.componenets.TopNotification
import com.example.supplelab.presentation.componenets.dialog.CategoriesDialog
import com.example.supplelab.ui.theme.BebasNeueFont
import com.example.supplelab.ui.theme.BorderIdle
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.IconPrimary
import com.example.supplelab.ui.theme.Surface
import com.example.supplelab.ui.theme.SurfaceLighter
import com.example.supplelab.ui.theme.TextPrimary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProductScreen(
    id: String?,
    onNavigationIconClicked: () -> Unit
) {
    val viewModel = koinViewModel<ManageProductViewModel>()
    var showCategoriesDialog by remember { mutableStateOf(false) }
    val screenState = viewModel.screenState
    val isFormValid = viewModel.isFormValid

    // Notification state
    var showNotification by remember { mutableStateOf(false) }
    var notificationMessage by remember { mutableStateOf("") }
    var notificationIsSuccess by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = showCategoriesDialog
    ) {
        CategoriesDialog(
            category = screenState.category,
            onDismiss = { showCategoriesDialog = false },
            onConfirmClick = {
                viewModel.updateCategory(it)
                showCategoriesDialog = false
            }
        )
    }
    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (id == null) "New Product"
                        else "Edit Product",
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

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    scrolledContainerColor = Surface,
                    navigationIconContentColor = IconPrimary,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = IconPrimary
                )
            )
        },

        ) { paddingValues ->
        // Wrap content in a Box so we can overlay the TopNotification at the top
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(
                        bottom = 24.dp,
                        top = 12.dp
                    ),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = BorderIdle,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(SurfaceLighter)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.plus),
                            contentDescription = "Add Icon",
                            tint = IconPrimary
                        )
                    }
                    CustomTextField(
                        value = screenState.title,
                        onValueChange = viewModel::updateTitle,
                        placeholder = "Title"
                    )
                    CustomTextField(
                        modifier = Modifier.height(168.dp),
                        value = screenState.description,
                        onValueChange = viewModel::updateDescription,
                        placeholder = "Description",
                        expanded = true
                    )
                    AlertTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = screenState.category.title,
                        onClick = {
                            showCategoriesDialog = true
                        }
                    )
                    CustomTextField(
                        value = "${screenState.weight ?: ""}",
                        onValueChange = { viewModel.updateWeight(it.toIntOrNull() ?: 0)},
                        placeholder = "Weight (Optional)",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                    CustomTextField(
                        value = screenState.flavors,
                        onValueChange = viewModel::updateFlavors,
                        placeholder = "Flavors (Optional)"
                    )
                    CustomTextField(
                        value = "${screenState.price}",
                        onValueChange = { value ->
                            if(value.isEmpty() || value.toDoubleOrNull() != null){
                                viewModel.updatePrice(value.toDoubleOrNull() ?: 0.0)
                            }
                        },
                        placeholder = "Price"
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                PrimaryButton(
                    text = if (id == null) "Add New Product"
                    else "Update Product",
                    icon = if (id == null) R.drawable.plus
                    else R.drawable.check ,
                    enabled = isFormValid,
                    onClick = {
                        viewModel.createNewProduct(
                            onSuccess = {
                                notificationMessage = if (id == null) "Product created successfully" else "Product updated successfully"
                                notificationIsSuccess = true
                                showNotification = true
                            },
                            onError = { error ->
                                notificationMessage = error
                                notificationIsSuccess = false
                                showNotification = true
                            }
                        )
                    }
                )
            }

            TopNotification(
                visible = showNotification,
                message = notificationMessage,
                isSuccess = notificationIsSuccess,
                onDismiss = { showNotification = false },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}