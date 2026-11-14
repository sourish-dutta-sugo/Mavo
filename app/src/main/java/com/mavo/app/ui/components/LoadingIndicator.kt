package com.mavo.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mavo.app.ui.theme.AppColors
import com.mavo.app.ui.theme.DimensionTokens
import com.mavo.app.ui.theme.LayoutTokens
import com.mavo.app.ui.theme.MavoTheme
import com.mavo.app.ui.theme.ShapeTokens

object LoadingDefaults {
    val placeholderColor: Color
        @Composable get() = AppColors.shimmerBg

    val highlightColor: Color
        @Composable get() = AppColors.shimmerBg.copy(alpha = 0.75f)

    val shape: Shape = ShapeTokens.input
    val avatarShape: Shape = CircleShape
    val lineHeight: Dp = LayoutTokens.large
    val textSpacing: Dp = LayoutTokens.medium
    val tileSpacing: Dp = LayoutTokens.medium
    val tileCornerRadius: Dp = ShapeTokens.cardRadius
    val cardCornerRadius: Dp = ShapeTokens.cardRadius
}

@Composable
fun LoadingPlaceholder(
    modifier: Modifier = Modifier,
    shape: Shape = LoadingDefaults.shape,
    color: Color = LoadingDefaults.placeholderColor,
    highlightColor: Color = LoadingDefaults.highlightColor,
    animate: Boolean = true
) {
    val brush = if (animate) {
        loadingShimmerBrush(color, highlightColor)
    } else {
        Brush.verticalGradient(listOf(color, color))
    }

    Box(
        modifier = modifier.background(brush = brush, shape = shape)
    )
}

@Composable
private fun loadingShimmerBrush(
    baseColor: Color,
    highlightColor: Color
): Brush {
    val transition = rememberInfiniteTransition()
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val startX = -200f + 400f * progress
    val endX = startX + 250f
    return Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(startX, 0f),
        end = Offset(endX, 0f)
    )
}

@Composable
fun LoadingText(
    modifier: Modifier = Modifier,
    width: Dp = Dp.Unspecified,
    height: Dp = LoadingDefaults.lineHeight,
    shape: Shape = ShapeTokens.chip,
    animate: Boolean = true
) {
    LoadingPlaceholder(
        modifier = if (width != Dp.Unspecified) {
            modifier.width(width).height(height)
        } else {
            modifier
                .fillMaxWidth()
                .height(height)
        },
        shape = shape,
        animate = animate
    )
}

@Composable
fun LoadingAvatar(
    modifier: Modifier = Modifier,
    size: Dp = DimensionTokens.avatarMedium,
    animate: Boolean = true
) {
    LoadingPlaceholder(
        modifier = modifier.width(size).height(size),
        shape = LoadingDefaults.avatarShape,
        animate = animate
    )
}

@Composable
fun LoadingRoundedPlaceholder(
    modifier: Modifier = Modifier,
    width: Dp = Dp.Unspecified,
    height: Dp = LayoutTokens.large,
    cornerRadius: Dp = ShapeTokens.xl,
    animate: Boolean = true
) {
    LoadingPlaceholder(
        modifier = if (width != Dp.Unspecified) {
            modifier.width(width).height(height)
        } else {
            modifier
                .fillMaxWidth()
                .height(height)
        },
        shape = RoundedCornerShape(cornerRadius),
        animate = animate
    )
}

@Composable
fun LoadingCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ShapeTokens.card,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(LayoutTokens.cardPadding), content = content)
    }
}

@Composable
fun LoadingListItem(
    modifier: Modifier = Modifier,
    animate: Boolean = true,
    lineCount: Int = 2
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = LayoutTokens.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LoadingAvatar(animate = animate)
        Spacer(modifier = Modifier.width(LayoutTokens.medium))
        Column(modifier = Modifier.fillMaxWidth()) {
            repeat(lineCount) { index ->
                LoadingText(
                    modifier = Modifier.fillMaxWidth(if (index == 0) 0.65f else 0.5f),
                    animate = animate
                )
                if (index < lineCount - 1) {
                    Spacer(modifier = Modifier.height(LayoutTokens.small))
                }
            }
        }
    }
}

@Composable
fun LoadingTile(
    modifier: Modifier = Modifier,
    animate: Boolean = true
) {
    LoadingCard(modifier = modifier) {
        LoadingText(height = LayoutTokens.large, animate = animate)
        Spacer(modifier = Modifier.height(LayoutTokens.medium))
        LoadingText(width = 120.dp, height = LayoutTokens.large, animate = animate)
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    MavoTheme {
        Column(modifier = Modifier.padding(LayoutTokens.large)) {
            LoadingText(height = LayoutTokens.xl, animate = false)
            Spacer(modifier = Modifier.height(LayoutTokens.large))
            LoadingListItem(animate = false)
            Spacer(modifier = Modifier.height(LayoutTokens.large))
            LoadingTile(animate = false)
        }
    }
}
