package com.example.supplelab.presentation.screens.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.supplelab.domain.model.CartItem
import com.example.supplelab.domain.model.Product
import com.example.supplelab.domain.model.QuantityCounterSize
import com.example.supplelab.ui.theme.BorderIdle
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.IconPrimary
import com.example.supplelab.ui.theme.RobotoCondensedFont
import com.example.supplelab.ui.theme.Surface
import com.example.supplelab.ui.theme.SurfaceLighter
import com.example.supplelab.ui.theme.TextPrimary
import com.example.supplelab.ui.theme.TextSecondary

@Composable
fun CartItemCard(
    modifier: Modifier = Modifier,
    product: Product,
    cartItem: CartItem,
    onIncrementClick: (Int) -> Unit,
    onDecrementClick: (Int) -> Unit,
    onRemoveClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceLighter)
    ) {
        AsyncImage(
            modifier = Modifier
                .width(120.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = BorderIdle,
                    shape = RoundedCornerShape(12.dp)
                ),
            model = ImageRequest.Builder(LocalContext.current)
                .data(product.thumbnail)
                .crossfade(true)
                .build(),
            contentDescription = "Product Image",
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = product.title,
                    fontFamily = RobotoCondensedFont,
                    fontSize = FontSize.MEDIUM,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Surface)
                        .border(
                            width = 1.dp,
                            color = BorderIdle,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable{ onRemoveClick() }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.delete),
                        contentDescription = "Delete Icon",
                        tint = IconPrimary
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$${product.price}",
                    fontSize = FontSize.EXTRA_REGULAR,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    maxLines = 1,
                )
                QuantityCounter(
                    size = QuantityCounterSize.Small,
                    value = cartItem.quantity,
                    onDecrement = onDecrementClick,
                    onIncrement = onIncrementClick
                )
            }

        }
    }

}