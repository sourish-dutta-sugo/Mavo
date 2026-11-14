package com.mavo.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mavo.app.ui.AppNavigationDestination
import com.mavo.app.ui.theme.AppColors
import com.mavo.app.ui.theme.BrandColors
import com.mavo.app.ui.theme.DimensionTokens
import com.mavo.app.ui.theme.IconTokens
import com.mavo.app.ui.theme.LayoutTokens
import com.mavo.app.ui.theme.ShapeTokens
import com.mavo.app.ui.theme.Surface
import com.mavo.app.ui.theme.TextSizeTokens

fun Modifier.topHeaderBlurEffect(
    baseColor: Color = Surface.card,
    blurRadius: Dp = 8.dp
): Modifier = this
    .blur(radius = blurRadius)
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                baseColor,
                baseColor.copy(alpha = 0.88f),
                baseColor.copy(alpha = 0.45f),
                Color.Transparent
            )
        )
    )

fun Modifier.bottomHeaderBlurEffect(
    baseColor: Color = Surface.card,
    blurRadius: Dp = 8.dp
): Modifier = this
    .blur(radius = blurRadius)
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                baseColor.copy(alpha = 0.45f),
                baseColor.copy(alpha = 0.88f),
                baseColor
            )
        )
    )

fun Modifier.horizontalStartHeaderBlurEffect(
    baseColor: Color = Surface.card,
    blurRadius: Dp = 8.dp
): Modifier = this
    .blur(radius = blurRadius)
    .background(
        brush = Brush.horizontalGradient(
            colors = listOf(
                baseColor,
                baseColor.copy(alpha = 0.88f),
                baseColor.copy(alpha = 0.45f),
                Color.Transparent
            )
        )
    )

fun Modifier.horizontalEndHeaderBlurEffect(
    baseColor: Color = Surface.card,
    blurRadius: Dp = 8.dp
): Modifier = this
    .blur(radius = blurRadius)
    .background(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                baseColor.copy(alpha = 0.45f),
                baseColor.copy(alpha = 0.88f),
                baseColor
            )
        )
    )

@Composable
fun TopHeaderCard(
    modifier: Modifier = Modifier,
    baseColor: Color = Surface.card,
    blurHeight: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        content()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(blurHeight)
                .align(Alignment.BottomCenter)
                .bottomHeaderBlurEffect(baseColor = baseColor, blurRadius = 8.dp)
        )
    }
}

@Composable
fun BottomHeaderCard(
    modifier: Modifier = Modifier,
    baseColor: Color = Surface.card,
    blurHeight: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        content()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(blurHeight)
                .align(Alignment.TopCenter)
                .topHeaderBlurEffect(baseColor = baseColor, blurRadius = 8.dp)
        )
    }
}

@Composable
fun HorizontalStartHeaderCard(
    modifier: Modifier = Modifier,
    baseColor: Color = Surface.card,
    blurWidth: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        content()
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(blurWidth)
                .align(Alignment.CenterEnd)
                .horizontalEndHeaderBlurEffect(baseColor = baseColor, blurRadius = 8.dp)
        )
    }
}

/**
 * Clean Mavo Navigation Bar: Floating Pill Design with 24dp bottom spacing,
 * single translating selection indicator, and smooth button scale motion.
 */
@Composable
fun AdaptiveNavigationBar(
    destinations: List<AppNavigationDestination>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNewSale: () -> Unit = {}
) {
    if (destinations.isEmpty()) return
    val selectedIndex = remember(currentRoute, destinations) {
        destinations.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(Surface.card)
                .border(DimensionTokens.borderThin, Surface.cardBorder, CircleShape)
                .padding(horizontal = LayoutTokens.small, vertical = LayoutTokens.small)
        ) {
            val count = destinations.size
            val itemWidth = this.maxWidth / count
            val indicatorOffsetHorizontal by animateDpAsState(
                targetValue = itemWidth * selectedIndex,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "nav_indicator_offset"
            )

            // Single translating selection indicator pill
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffsetHorizontal)
                    .width(itemWidth)
                    .height(52.dp)
                    .padding(horizontal = LayoutTokens.micro)
                    .clip(CircleShape)
                    .background(BrandColors.peachDarker)
            )

            // Navigation items layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                destinations.forEachIndexed { index, destination ->
                    val selected = index == selectedIndex
                    val scale by animateFloatAsState(
                        targetValue = if (selected) 1.08f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "nav_item_scale"
                    )
                    val iconColor by animateColorAsState(
                        targetValue = if (selected) BrandColors.mangoPunch else AppColors.textSecondary,
                        animationSpec = tween(durationMillis = 200),
                        label = "nav_icon_color"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (selected) AppColors.textPrimary else AppColors.textSecondary,
                        animationSpec = tween(durationMillis = 200),
                        label = "nav_text_color"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (!selected) onNavigate(destination.route)
                            }
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                                tint = iconColor,
                                modifier = Modifier.size(IconTokens.medium)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = destination.label,
                                fontSize = TextSizeTokens.caption,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdaptiveNavigationRail(
    destinations: List<AppNavigationDestination>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onNewSale: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (destinations.isEmpty()) return
    val selectedIndex = remember(currentRoute, destinations) {
        destinations.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
    }

    HorizontalStartHeaderCard(
        modifier = modifier.fillMaxHeight(),
        baseColor = Surface.card,
        blurWidth = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(DimensionTokens.railWidth)
                .background(Surface.card)
                .border(width = DimensionTokens.borderThin, color = Surface.cardBorder)
                .padding(vertical = LayoutTokens.large, horizontal = LayoutTokens.small),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(LayoutTokens.large)
        ) {
            FloatingActionButton(
                onClick = onNewSale,
                containerColor = BrandColors.mangoPunch,
                contentColor = BrandColors.aubergineInk,
                shape = ShapeTokens.card,
                modifier = Modifier.size(DimensionTokens.fabSize)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Voucher", tint = BrandColors.aubergineInk)
            }

            Spacer(modifier = Modifier.height(LayoutTokens.small))

            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {
                val itemHeight = 56.dp
                val spacing = LayoutTokens.large
                val indicatorOffsetVertical by animateDpAsState(
                    targetValue = (itemHeight + spacing) * selectedIndex,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "rail_indicator_offset"
                )

                // Single translating selection indicator pill
                Box(
                    modifier = Modifier
                        .offset(y = indicatorOffsetVertical)
                        .fillMaxWidth()
                        .height(itemHeight)
                        .clip(RoundedCornerShape(ShapeTokens.navItem))
                        .background(BrandColors.peachDarker)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    destinations.forEachIndexed { index, destination ->
                        val selected = index == selectedIndex
                        val scale by animateFloatAsState(
                            targetValue = if (selected) 1.08f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "rail_item_scale"
                        )
                        val iconColor by animateColorAsState(
                            targetValue = if (selected) BrandColors.mangoPunch else AppColors.textSecondary,
                            animationSpec = tween(durationMillis = 200),
                            label = "rail_icon_color"
                        )
                        val textColor by animateColorAsState(
                            targetValue = if (selected) AppColors.textPrimary else AppColors.textSecondary,
                            animationSpec = tween(durationMillis = 200),
                            label = "rail_text_color"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeight)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    if (!selected) onNavigate(destination.route)
                                }
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.label,
                                    tint = iconColor,
                                    modifier = Modifier.size(IconTokens.medium)
                                )
                                Spacer(modifier = Modifier.height(LayoutTokens.micro))
                                Text(
                                    text = destination.label,
                                    fontSize = TextSizeTokens.caption,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
