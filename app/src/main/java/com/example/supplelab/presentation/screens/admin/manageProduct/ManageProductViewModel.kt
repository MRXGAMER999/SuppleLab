package com.example.supplelab.presentation.screens.admin.manageProduct

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supplelab.domain.model.Product
import com.example.supplelab.domain.model.ProductCategory
import com.example.supplelab.domain.repository.AdminRepository
import com.example.supplelab.util.RequestState
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
data class ManageProductState(
    val id: String = Uuid.random().toHexString(),
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val title: String = "",
    val description: String = "",
    val thumbnail: String = "thumbnail image",
    val category: ProductCategory = ProductCategory.Protein,
    val flavors: String = "",
    val weight: Int? = null,
    val price: Double = 0.0,
    val isExistingProduct: Boolean = false,
    val isNewProduct: Boolean = false,
    val isPopular: Boolean = false,
    val isDiscounted: Boolean = false
)

class ManageProductViewModel(
    private val adminRepository: AdminRepository,
) : ViewModel() {
    var screenState by mutableStateOf(ManageProductState())
        private set

    var thumbnailUploaderState: RequestState<Unit> by mutableStateOf(RequestState.Idle)
        private set

    val isFormValid: Boolean
        get() = screenState.title.isNotEmpty() &&
                screenState.description.isNotEmpty() &&
                screenState.thumbnail.isNotEmpty() &&
                screenState.price != 0.0

    fun loadProduct(id: String) {
        viewModelScope.launch {
            val selectedProduct = adminRepository.readProductById(id)
            if (selectedProduct.isSuccess()) {
                val product = selectedProduct.getSuccessData()
                updateTitle(product.title)
                updateCreatedAt(product.createdAt)
                updateDescription(product.description)
                updateThumbnail(product.thumbnail)
                if (product.thumbnail.isNotEmpty()) {
                    updateThumbnailUploaderState(RequestState.Success(Unit))
                } else {
                    updateThumbnailUploaderState(RequestState.Idle)
                }
                updateCategory(ProductCategory.valueOf(product.category))
                updateFlavors(product.flavors?.joinToString(",") ?: "")
                updateWeight(product.weight)
                updatePrice(product.price)
                updateNew(product.isNew)
                updatePopular(product.isPopular)
                updateIsDiscounted(product.isDiscounted)
                screenState = screenState.copy(id = product.id, isExistingProduct = true)
            }
        }
    }

    fun resetState() {
        screenState = ManageProductState()
        thumbnailUploaderState = RequestState.Idle
    }
    fun updateCreatedAt(value: Long) {
        screenState = screenState.copy(createdAt = value)
    }

    fun updateTitle(value: String) {
        screenState = screenState.copy(title = value)
    }

    fun updateDescription(value: String) {
        screenState = screenState.copy(description = value)
    }

    fun updateThumbnail(value: String) {
        screenState = screenState.copy(thumbnail = value)
    }

    fun updateThumbnailUploaderState(value: RequestState<Unit>) {
        thumbnailUploaderState = value
    }

    fun updateCategory(value: ProductCategory) {
        screenState = screenState.copy(category = value)
    }

    fun updateFlavors(value: String) {
        screenState = screenState.copy(flavors = value)
    }

    fun updateWeight(value: Int?) {
        screenState = screenState.copy(weight = value)
    }

    fun updatePrice(value: Double) {
        screenState = screenState.copy(price = value)
    }
    fun updateNew(value: Boolean) {
        screenState = screenState.copy(isNewProduct = value)
    }
    fun updatePopular(value: Boolean) {
        screenState = screenState.copy(isPopular = value)
    }
    fun updateIsDiscounted(value: Boolean) {
        screenState = screenState.copy(isDiscounted = value)
    }

    fun createNewProduct(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            adminRepository.createNewProduct(
                product = Product(
                    id = screenState.id,
                    title = screenState.title,
                    description = screenState.description,
                    thumbnail = screenState.thumbnail,
                    category = screenState.category.name,
                    flavors = screenState.flavors.split(","),
                    weight = screenState.weight,
                    price = screenState.price,
                    isNew = screenState.isNewProduct,
                    isPopular = screenState.isPopular,
                    isDiscounted = screenState.isDiscounted
                ),
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    fun uploadThumbnailToStorage(
        uri: Uri?,
        onSuccess: () -> Unit
    ) {
        if (uri == null) {
            updateThumbnailUploaderState(RequestState.Error("File is null. Error in selecting the image"))
            return
        }
        updateThumbnailUploaderState(RequestState.Loading)
        viewModelScope.launch {
            try {
                val downloadUrl = adminRepository.uploadImageToStorage(uri)
                if (downloadUrl.isNullOrEmpty()) {
                    throw Exception("Failed to retrieve download URL after the upload.")
                }
                // Only update Firestore if this is an existing product
                if (screenState.isExistingProduct) {
                    adminRepository.updateProductThumbnail(
                        productId = screenState.id,
                        imageUrl = downloadUrl,
                        onSuccess = {
                            onSuccess()
                            updateThumbnailUploaderState(RequestState.Success(Unit))
                            updateThumbnail(downloadUrl)
                        },
                        onError = { message ->
                            updateThumbnailUploaderState(RequestState.Error(message))
                        }
                    )
                } else {
                    // For new products, just update the local state
                    onSuccess()
                    updateThumbnailUploaderState(RequestState.Success(Unit))
                    updateThumbnail(downloadUrl)
                }
            } catch (e: Exception) {
                updateThumbnailUploaderState(
                    RequestState.Error(
                        e.message ?: "An unexpected error occurred"
                    )
                )
            }
        }
    }
    fun updateProduct(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ){
        if(isFormValid){
            viewModelScope.launch {
                adminRepository.updateProduct(
                    product = Product(
                        id = screenState.id,
                        createdAt = screenState.createdAt,
                        title = screenState.title,
                        description = screenState.description,
                        thumbnail = screenState.thumbnail,
                        category = screenState.category.name,
                        flavors = screenState.flavors.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() },
                        weight = screenState.weight,
                        price = screenState.price,
                        isNew = screenState.isNewProduct,
                        isPopular = screenState.isPopular,
                        isDiscounted = screenState.isDiscounted
                    ),
                    onSuccess = onSuccess,
                    onError = onError
                )
            }

        } else {
            onError("Please fill in all required fields.")
        }

    }

    fun deleteThumbnailFromStorage(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            adminRepository.deleteImageFromStorage(
                imageUrl = screenState.thumbnail,
                onSuccess = {
                    // Handle both existing and non-existing products
                    if (screenState.isExistingProduct) {
                        viewModelScope.launch {
                            adminRepository.updateProductThumbnail(
                                productId = screenState.id,
                                imageUrl = "",
                                onSuccess = {
                                    updateThumbnail("")
                                    onSuccess()
                                    updateThumbnailUploaderState(RequestState.Idle)
                                },
                                onError = { message ->
                                    updateThumbnailUploaderState(RequestState.Error(message))
                                }
                            )
                        }
                    } else {
                        // For non-existing products, just update the local state
                        updateThumbnail("")
                        onSuccess()
                        updateThumbnailUploaderState(RequestState.Idle)
                    }
                },
                onError = onError
            )
        }
    }
    fun deleteProduct(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        if(screenState.isExistingProduct){
            viewModelScope.launch {
                adminRepository.deleteProduct(
                    productId = screenState.id,
                    onSuccess = {
                        deleteThumbnailFromStorage(
                            onSuccess = {},
                            onError = { }
                        )
                        onSuccess()
                    },
                    onError = { message -> onError(message) }
                )
            }
        }

    }
}