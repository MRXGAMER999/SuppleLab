package com.example.supplelab.presentation.screens.admin.manageProduct

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.supplelab.R
import com.example.supplelab.domain.model.ProductCategory
import com.example.supplelab.presentation.componenets.AlertTextField
import com.example.supplelab.presentation.componenets.CustomTextField
import com.example.supplelab.presentation.componenets.ErrorCard
import com.example.supplelab.presentation.componenets.PrimaryButton
import com.example.supplelab.presentation.componenets.TopNotification
import com.example.supplelab.presentation.componenets.dialog.CategoriesDialog
import com.example.supplelab.presentation.profile.LoadingCard
import com.example.supplelab.ui.theme.BebasNeueFont
import com.example.supplelab.ui.theme.BorderIdle
import com.example.supplelab.ui.theme.ButtonPrimary
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.IconPrimary
import com.example.supplelab.ui.theme.Surface
import com.example.supplelab.ui.theme.SurfaceDarker
import com.example.supplelab.ui.theme.SurfaceLighter
import com.example.supplelab.ui.theme.SurfaceSecondary
import com.example.supplelab.ui.theme.TextPrimary
import com.example.supplelab.ui.theme.TextSecondary
import com.example.supplelab.util.DisplayResult
import com.example.supplelab.util.PhotoPicker
import com.example.supplelab.util.RequestState
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProductScreen(
    id: String?,
    onNavigationIconClicked: () -> Unit
) {
    // Notification state
    var showNotification by remember { mutableStateOf(false) }
    var notificationMessage by remember { mutableStateOf("") }
    var notificationIsSuccess by remember { mutableStateOf(true) }
    val viewModel = koinViewModel<ManageProductViewModel>()


    LaunchedEffect(id) {
        if (id != null) {
            viewModel.loadProduct(id)
        } else {
            viewModel.resetState()
        }
    }

    var showCategoriesDialog by remember { mutableStateOf(false) }
    var dropDownMenuOpened by remember { mutableStateOf(false) }

    val screenState = viewModel.screenState
    val isFormValid = viewModel.isFormValid
    val thumbnailUploaderState = viewModel.thumbnailUploaderState
    val photoPicker = koinInject<PhotoPicker>()
    photoPicker.InitializePhotoPicker(
        onImageSelect = { uri ->
            viewModel.uploadThumbnailToStorage(
                uri = uri,
                onSuccess = {
                    notificationMessage = "Thumbnail uploaded successfully"
                    notificationIsSuccess = true
                    showNotification = true
                }
            )
        }
    )



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

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Surface,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (id == null) "New Product"
                            else "Edit Product",
                            fontFamily = BebasNeueFont,
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
                                contentDescription = "Back Arrow Icon",
                                tint = IconPrimary

                            )
                        }
                    },
                    actions = {
                        id.takeIf { it != null }?.let {
                            Box{
                                IconButton(onClick = { dropDownMenuOpened = true }) {
                                    Icon(
                                        painter = painterResource(R.drawable.vertical_menu),
                                        contentDescription = "Vertical Menu Icon",
                                        tint = IconPrimary
                                    )
                                }
                                DropdownMenu(
                                    containerColor = Surface,
                                    expanded = dropDownMenuOpened,
                                    onDismissRequest = { dropDownMenuOpened = false }
                                ) {
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                modifier = Modifier.size(14.dp),
                                                painter = painterResource(R.drawable.delete),
                                                contentDescription = "Delete Icon",
                                                tint = IconPrimary
                                            )
                                        },
                                        text = { Text(text = "Delete", color = TextPrimary) },
                                        onClick = {
                                            dropDownMenuOpened = false
                                            viewModel.deleteProduct(
                                                onSuccess = onNavigationIconClicked,
                                                onError = {message ->
                                                    notificationMessage = message
                                                    notificationIsSuccess = false
                                                    showNotification = true
                                                }
                                            )
                                        },
                                    )
                                }
                            }
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
                                .clickable(
                                    enabled = thumbnailUploaderState.isIdle()
                                ) {
                                    photoPicker.open()

                                },
                            contentAlignment = Alignment.Center
                        ){
                            thumbnailUploaderState.DisplayResult(
                                onIdle = {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        painter = painterResource(R.drawable.plus),
                                        contentDescription = "Plus Icon",
                                        tint = IconPrimary
                                    )
                                },
                                onLoading = {
                                    LoadingCard(modifier = Modifier.fillMaxSize())
                                },
                                onSuccess = {
                                    val context = LocalContext.current
                                    var imageLoadState by remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }
                                    
                                    // Memoize image request to avoid recreating on each recomposition
                                    val imageRequest = remember(screenState.thumbnail) {
                                        ImageRequest.Builder(context)
                                            .data(screenState.thumbnail)
                                            .crossfade(true)
                                            .build()
                                    }
                                    
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.TopEnd) {
                                        AsyncImage(
                                            modifier = Modifier.fillMaxSize(),
                                            model = imageRequest,
                                            contentDescription = "Product Thumbnail",
                                            contentScale = ContentScale.Crop,
                                            onState = { state ->
                                                imageLoadState = state
                                                when (state) {
                                                    is AsyncImagePainter.State.Error -> {
                                                        Log.e("ManageProduct", "Image load error: ${state.result.throwable.message}")
                                                        Log.e("ManageProduct", "Image URL: ${screenState.thumbnail}")
                                                    }
                                                    is AsyncImagePainter.State.Success -> {
                                                        Log.d("ManageProduct", "Image loaded successfully: ${screenState.thumbnail}")
                                                    }
                                                    else -> {}
                                                }
                                            }
                                        )
                                        Box(
                                            modifier = Modifier

                                                .padding(
                                                    top =12.dp,
                                                    end = 12.dp
                                                )
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(ButtonPrimary)
                                                .clickable {
                                                    viewModel.deleteThumbnailFromStorage(
                                                        onSuccess = {
                                                            notificationMessage = "Thumbnail deleted successfully"
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
                                                .padding(12.dp),
                                            contentAlignment = Alignment.Center

                                        ){
                                            Icon(
                                                modifier = Modifier.size(24.dp),
                                                painter = painterResource(R.drawable.delete),
                                                contentDescription = "Delete Icon",
                                            )
                                        }
                                    }


                                    // Show error overlay if image failed to load
                                    if (imageLoadState is AsyncImagePainter.State.Error) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                modifier = Modifier.size(48.dp),
                                                painter = painterResource(R.drawable.alert_triangle),
                                                contentDescription = "Error Icon",
                                                tint = TextSecondary
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Failed to load image",
                                                fontSize = FontSize.SMALL,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                },
                                onError = {message ->
                                    Column(modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        ErrorCard(message = message)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        TextButton(
                                            onClick = {
                                                viewModel.updateThumbnailUploaderState(RequestState.Idle)
                                            },
                                            colors = ButtonDefaults.textButtonColors(
                                                containerColor = Color.Transparent,
                                                contentColor = TextSecondary
                                            )
                                        ) {
                                            Text(
                                                text = "Retry",
                                                fontSize = FontSize.SMALL,
                                                color = TextPrimary
                                            )
                                        }

                                    }
                                },
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
                        AnimatedVisibility(
                            visible = screenState.category != ProductCategory.Accessories
                        ) {
                            Column {
                                CustomTextField(
                                    value = "${screenState.weight ?: ""}",
                                    onValueChange = { viewModel.updateWeight(it.toIntOrNull() ?: 0)},
                                    placeholder = "Weight",
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                CustomTextField(
                                    value = screenState.flavors,
                                    onValueChange = viewModel::updateFlavors,
                                    placeholder = "Flavors"
                                )
                            }
                        }

                        CustomTextField(
                            value = "${screenState.price}",
                            onValueChange = { value ->
                                if(value.isEmpty() || value.toDoubleOrNull() != null){
                                    viewModel.updatePrice(value.toDoubleOrNull() ?: 0.0)
                                }
                            },
                            placeholder = "Price"
                        )
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 12.dp),
                                    text = "New",
                                    fontSize = FontSize.REGULAR,
                                    color = TextPrimary
                                )
                                Switch(
                                    checked = screenState.isNewProduct,
                                    onCheckedChange = viewModel::updateNew,
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = SurfaceSecondary,
                                        uncheckedTrackColor = SurfaceDarker,
                                        checkedThumbColor = Surface,
                                        uncheckedThumbColor = Surface,
                                        checkedBorderColor = SurfaceSecondary,
                                        uncheckedBorderColor = SurfaceDarker
                                ))

                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 12.dp),
                                    text = "Popular",
                                    fontSize = FontSize.REGULAR,
                                    color = TextPrimary
                                )
                                Switch(
                                    checked = screenState.isPopular,
                                    onCheckedChange = viewModel::updatePopular,
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = SurfaceSecondary,
                                        uncheckedTrackColor = SurfaceDarker,
                                        checkedThumbColor = Surface,
                                        uncheckedThumbColor = Surface,
                                        checkedBorderColor = SurfaceSecondary,
                                        uncheckedBorderColor = SurfaceDarker
                                    ))

                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 12.dp),
                                    text = "Discounted",
                                    fontSize = FontSize.REGULAR,
                                    color = TextPrimary
                                )
                                Switch(
                                    checked = screenState.isDiscounted,
                                    onCheckedChange = viewModel::updateIsDiscounted,
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = SurfaceSecondary,
                                        uncheckedTrackColor = SurfaceDarker,
                                        checkedThumbColor = Surface,
                                        uncheckedThumbColor = Surface,
                                        checkedBorderColor = SurfaceSecondary,
                                        uncheckedBorderColor = SurfaceDarker
                                    ))

                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    PrimaryButton(
                        text = if (id == null) "Add New Product"
                        else "Update Product",
                        icon = if (id == null) R.drawable.plus
                        else R.drawable.check ,
                        enabled = isFormValid,
                        onClick = {
                            if(id == null){
                                viewModel.createNewProduct(
                                    onSuccess = {
                                        notificationMessage = "Product created successfully"
                                        notificationIsSuccess = true
                                        showNotification = true
                                    },
                                    onError = { error ->
                                        notificationMessage = error
                                        notificationIsSuccess = false
                                        showNotification = true
                                    }
                                )
                            } else {
                                viewModel.updateProduct(
                                    onSuccess = {
                                        notificationMessage = "Product updated successfully"
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
                        }
                    )
                }
            }
        }

        // Top notification banner
        TopNotification(
            visible = showNotification,
            message = notificationMessage,
            isSuccess = notificationIsSuccess,
            onDismiss = { showNotification = false },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}