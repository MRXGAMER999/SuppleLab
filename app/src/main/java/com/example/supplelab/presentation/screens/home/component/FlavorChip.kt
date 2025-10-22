package com.example.supplelab.presentation.screens.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.supplelab.ui.theme.BorderIdle
import com.example.supplelab.ui.theme.BorderSecondary
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.Surface
import com.example.supplelab.ui.theme.SurfaceLighter
import com.example.supplelab.ui.theme.TextPrimary
import com.example.supplelab.ui.theme.TextSecondary

@Composable
fun FlavorChip(
    flavor: String,
    isSelected: Boolean
){
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .border(
                width = 1.dp,
                color = if(isSelected) BorderSecondary else BorderIdle,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = flavor,
            fontSize = FontSize.SMALL,
            color = if(isSelected) TextSecondary else TextPrimary,
            fontWeight = FontWeight.Medium
        )
    }

}