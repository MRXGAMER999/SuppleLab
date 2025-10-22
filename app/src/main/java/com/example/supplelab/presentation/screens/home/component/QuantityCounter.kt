package com.example.supplelab.presentation.screens.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.supplelab.R
import com.example.supplelab.domain.model.QuantityCounterSize
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.IconPrimary
import com.example.supplelab.ui.theme.SurfaceBrand
import com.example.supplelab.ui.theme.SurfaceLighter
import com.example.supplelab.ui.theme.TextPrimary


@Composable
fun QuantityCounter(
    modifier: Modifier = Modifier,
    size: QuantityCounterSize,
    value: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(size.spacing)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SurfaceBrand)
                .clickable { onDecrement() }
                .padding(size.padding),
            contentAlignment = Alignment.Center
        ){
            Icon(
                modifier = Modifier.size(14.dp),
                painter = painterResource(R.drawable.minus),
                contentDescription = "Minus icon",
                tint = IconPrimary
            )
        }
        Box(modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceLighter)
            .padding(size.padding),
            contentAlignment = Alignment.Center){
            Text(
                text = "+$value",
                fontSize = FontSize.SMALL,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SurfaceBrand)
                .clickable { onIncrement() }
                .padding(size.padding),
            contentAlignment = Alignment.Center
        ){
            Icon(
                modifier = Modifier.size(14.dp),
                painter = painterResource(R.drawable.plus),
                contentDescription = "plus icon",
                tint = IconPrimary
            )
        }
    }
}