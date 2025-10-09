package com.example.supplelab.domain.model

import androidx.compose.ui.graphics.Color
import com.example.supplelab.ui.theme.CategoryBlue
import com.example.supplelab.ui.theme.CategoryGreen
import com.example.supplelab.ui.theme.CategoryPurple
import com.example.supplelab.ui.theme.CategoryRed
import com.example.supplelab.ui.theme.CategoryYellow
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val title: String,
    val description: String,
    val thumbnail: Double,
    val category: String,
    val flavors: List<String>? = null,
    val weight: Int? = null,
    val price: Double,
    val isPopular: Boolean = false,
    val isDiscounted: Boolean = false,
    val isNew: Boolean = false,
)

enum class ProductCategory(
    val title: String,
    val color: Color
) {
    Protein(
        title = "Protein",
        color = CategoryYellow
    ),
    Creatine(
        title = "Creatine",
        color = CategoryBlue
    ),
    PerWorkout(
        title = "Pre-Workout",
        color = CategoryGreen
    ),
    Gainers(
        title = "Gainer",
        color = CategoryPurple
    ),
    Accessories(
        title = "Accessories",
        color = CategoryRed
    )
}
