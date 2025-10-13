package com.example.supplelab.presentation.componenets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.supplelab.R
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.SurfaceBrand
import com.example.supplelab.ui.theme.SurfaceError
import com.example.supplelab.ui.theme.TextPrimary
import com.example.supplelab.ui.theme.TextWhite
import kotlinx.coroutines.delay

@Composable
fun TopNotification(
    visible: Boolean,
    message: String,
    isSuccess: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    durationMillis: Long = 2000L,
    color: Color = if (isSuccess) SurfaceBrand else SurfaceError
) {
    LaunchedEffect(visible) {
        if (visible) {
            delay(durationMillis)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .systemBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(if (isSuccess) R.drawable.check else R.drawable.close),
                contentDescription = null,
                tint = if (isSuccess) TextPrimary else TextWhite
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                fontSize = FontSize.REGULAR,
                color = if (isSuccess) TextPrimary else TextWhite
            )
        }
    }
}

