package com.example.supplelab.presentation.screens.manageProduct

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
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class ManageProductState(
    val id: String = Uuid.random().toHexString(),
    val title: String = "",
    val description: String = "",
    val thumbnail: String = "thumbnail image",
    val category: ProductCategory = ProductCategory.Protein,
    val flavors: String = "",
    val weight: Int? = null,
    val price: Double = 0.0,
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
                updateDescription(product.description)
                updateThumbnail(product.thumbnail)
                updateThumbnailUploaderState(RequestState.Success(Unit))
                updateCategory(ProductCategory.valueOf(product.category))
                updateFlavors(product.flavors?.joinToString(",") ?: "")
                updateWeight(product.weight)
                updatePrice(product.price)
                screenState = screenState.copy(id = product.id)
            }
        }
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
                onSuccess()
                updateThumbnailUploaderState(RequestState.Success(Unit))
                updateThumbnail(downloadUrl)
            } catch (e: Exception) {
                updateThumbnailUploaderState(
                    RequestState.Error(
                        e.message ?: "An unexpected error occurred"
                    )
                )
            }
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
                    updateThumbnail("")
                    updateThumbnailUploaderState(RequestState.Idle)
                    onSuccess()
                },
                onError = onError
            )
        }
    }
}