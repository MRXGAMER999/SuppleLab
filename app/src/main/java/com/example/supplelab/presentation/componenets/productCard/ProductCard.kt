package com.example.supplelab.presentation.componenets.productCard

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.supplelab.domain.model.Product
import com.example.supplelab.domain.model.ProductCategory
import com.example.supplelab.ui.theme.BorderIdle
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.RobotoCondensedFont
import com.example.supplelab.ui.theme.SurfaceLighter
import com.example.supplelab.ui.theme.TextPrimary
import com.example.supplelab.ui.theme.TextSecondary
import com.example.supplelab.util.Constants.ALPHA_HALF

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: Product,
    onClick: (String) -> Unit
) {
    // Memoize image request to avoid recreating on each recomposition
    val context = LocalContext.current
    val imageRequest = remember(product.thumbnail) {
        ImageRequest.Builder(context)
            .data(product.thumbnail)
            .crossfade(true)
            .build()
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(128.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                color = BorderIdle,
                shape = RoundedCornerShape(12.dp)
            )
            .background(SurfaceLighter)
            .clickable { onClick(product.id) }
    ) {
        AsyncImage(
            modifier = Modifier
                .width(120.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = BorderIdle,
                    shape = RoundedCornerShape(12.dp)
                ),
            model = imageRequest,
            contentDescription = "Product Image",
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = product.title,
                fontSize = FontSize.MEDIUM,
                fontFamily = RobotoCondensedFont,
                color = TextPrimary,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(ALPHA_HALF),
                text = product.description,
                fontSize = FontSize.REGULAR,
                lineHeight = FontSize.REGULAR * 1.3,
                color = TextPrimary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedContent (
                    targetState = product.category
                ){ category ->
                    if (ProductCategory.valueOf(category) == ProductCategory.Accessories){
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(14.dp),
                                painter = painterResource(id = R.drawable.weight),
                                contentDescription = "Weight Icon",
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${product.weight}g",
                                fontSize = FontSize.EXTRA_SMALL,
                                color = TextPrimary,
                            )
                        }
                    }
                }
                Text(
                    text = "$${product.price}",
                    fontSize = FontSize.EXTRA_REGULAR,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}