package com.example.supplelab.presentation.screens.home.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.IconWhite
import com.example.supplelab.ui.theme.RobotoCondensedFont
import com.example.supplelab.ui.theme.TextBrand
import com.example.supplelab.ui.theme.TextWhite
import com.example.supplelab.util.Constants.ALPHA_HALF
import com.example.supplelab.util.Constants.ALPHA_ZERO

@Composable
fun MainProductCard(
    modifier: Modifier = Modifier,
    product: Product,
    isLarge: Boolean = false,
    onProductClick: (String) -> Unit
) {
    // Only create infinite animations for large (centered) cards to save CPU
    val infiniteTransition = if (isLarge) rememberInfiniteTransition() else null
    val animatedScale by infiniteTransition?.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse
        )
    ) ?: remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }

    val animatedRotation by infiniteTransition?.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    ) ?: remember { androidx.compose.runtime.mutableFloatStateOf(0f) }

    // Memoize image request to avoid recreating on each recomposition
    val context = LocalContext.current
    val imageRequest = remember(product.thumbnail) {
        ImageRequest.Builder(context)
            .data(product.thumbnail)
            .crossfade(true)
            .build()
    }

    // Build stable modifier to avoid unnecessary recomposition
    val imageModifier = remember(isLarge, animatedScale, animatedRotation) {
        Modifier
            .fillMaxSize()
            .animateContentSize()
            .then(
                if(isLarge) Modifier
                    .scale(animatedScale)
                    .rotate(animatedRotation)
                else Modifier
            )
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .clickable {onProductClick(product.id)}
    ){
        AsyncImage(
            modifier = imageModifier,
            model = imageRequest,
            contentDescription = "Product Image",
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black,
                            Color.Black.copy(ALPHA_ZERO)
                        ),
                        startY = Float.POSITIVE_INFINITY,
                        endY = 0.0f
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = product.title,
                fontSize = FontSize.EXTRA_MEDIUM,
                color = TextWhite,
                fontFamily = RobotoCondensedFont,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.description,
                fontSize = FontSize.REGULAR,
                lineHeight = FontSize.REGULAR * 1.3f,
                color = TextWhite.copy(ALPHA_HALF),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
                                    tint = IconWhite
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${product.weight}g",
                                    fontSize = FontSize.EXTRA_SMALL,
                                    color = TextWhite,
                                )
                            }
                        }
                    }
                    Text(
                        text = "$${product.price}",
                        fontSize = FontSize.EXTRA_REGULAR,
                        color = TextBrand,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}