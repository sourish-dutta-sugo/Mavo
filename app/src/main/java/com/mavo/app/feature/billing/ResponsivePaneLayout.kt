package com.mavo.app.feature.billing

import androidx.compose.ui.layout.Layout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.mavo.app.ui.theme.LayoutTokens

internal fun usesSplitPane(widthSizeClass: WindowWidthSizeClass): Boolean =
    widthSizeClass == WindowWidthSizeClass.Expanded

@Composable
internal fun ResponsivePaneLayout(
    widthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    productContent: @Composable () -> Unit,
    cartContent: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = {
            productContent()
            cartContent()
        }
    ) { measurables, constraints ->
        val isExpanded = usesSplitPane(widthSizeClass)
        val gap = if (isExpanded) LayoutTokens.paneGap else LayoutTokens.compactPaneGap
        val safeGap = gap.roundToPx().coerceAtMost(constraints.maxWidth)

        if (isExpanded) {
            val paneWidth = ((constraints.maxWidth - safeGap) / 2).coerceAtLeast(0)
            val paneConstraints = constraints.copy(
                minWidth = 0,
                maxWidth = paneWidth,
                minHeight = 0
            )
            val placeables = measurables.map { it.measure(paneConstraints) }
            val height = placeables.maxOfOrNull { it.height } ?: 0
            layout(constraints.maxWidth, height) {
                placeables[0].placeRelative(0, 0)
                placeables[1].placeRelative(paneWidth + safeGap, 0)
            }
        } else {
            val paneHeight = ((constraints.maxHeight - safeGap) / 2).coerceAtLeast(0)
            val paneConstraints = constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxHeight = paneHeight
            )
            val placeables = measurables.map { it.measure(paneConstraints) }
            val width = placeables.maxOfOrNull { it.width } ?: 0
            layout(width, paneHeight * 2 + safeGap) {
                placeables[0].placeRelative(0, 0)
                placeables[1].placeRelative(0, paneHeight + safeGap)
            }
        }
    }
}
