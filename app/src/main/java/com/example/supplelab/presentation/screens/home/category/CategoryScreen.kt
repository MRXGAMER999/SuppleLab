package com.example.supplelab.presentation.screens.home.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.supplelab.domain.model.ProductCategory
import com.example.supplelab.presentation.screens.home.component.CategoryCard

@Composable
fun CategoryScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProductCategory.entries.forEach { category ->
            CategoryCard(
                category = category,
                onClick = { /* TODO: Handle category click */ }
            )
        }
    }
}