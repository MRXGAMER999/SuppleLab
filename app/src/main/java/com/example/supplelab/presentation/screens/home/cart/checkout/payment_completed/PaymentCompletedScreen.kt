package com.example.supplelab.presentation.screens.home.cart.checkout.payment_completed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.supplelab.R
import com.example.supplelab.presentation.componenets.InfoCard
import com.example.supplelab.presentation.componenets.PrimaryButton
import com.example.supplelab.ui.theme.Surface

@Composable
fun PaymentCompletedScreen(
    isSuccess: Boolean?,
    error: String?,
    navigateBack: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .systemBarsPadding()
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            InfoCard(
                title = if(isSuccess != null) "Success!" else "Oops!",
                subtitle = if (isSuccess != null) "Your order is on the way." else error
                    ?: "Something went wrong. Please try again.",
                icon = if (isSuccess == true) R.drawable.checkmark_image else R.drawable.cat
            )
        }
        PrimaryButton(
            text = "Go back",
            icon = R.drawable.right_arrow,
            onClick = navigateBack,
        )
    }
}