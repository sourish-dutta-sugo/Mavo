package com.mavo.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mavo.app.ui.theme.BrandColors

/**
 * Mavo Loop motif.
 * Lowercase 'o' rounded imperfect loop motif.
 * Sizes: 16dp, 24dp, 40dp.
 * Stroke: 2dp.
 * Used on hero areas, onboarding, empty states, splash screens, section headers.
 * Never placed behind dense tables or financial forms.
 * Never animated as infinite spinner.
 */
@Composable
fun MavoLoop(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    strokeWidth: Dp = 2.dp,
    color: Color = BrandColors.aubergineInk,
    isOpen: Boolean = false,
    animateOnAppear: Boolean = false
) {
    val progress = remember { Animatable(if (animateOnAppear) 0f else 1f) }

    if (animateOnAppear) {
        LaunchedEffect(Unit) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 220)
            )
        }
    }

    Canvas(
        modifier = modifier.size(size)
    ) {
        val stroke = strokeWidth.toPx()
        val halfStroke = stroke / 2f
        val ovalWidth = this.size.width - stroke
        val ovalHeight = this.size.height - stroke

        // Imperfect rounded oval loop
        val sweepAngle = if (isOpen) 300f * progress.value else 360f * progress.value
        drawArc(
            color = color,
            startAngle = if (isOpen) 30f else 0f,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(halfStroke, halfStroke),
            size = Size(ovalWidth, ovalHeight),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

