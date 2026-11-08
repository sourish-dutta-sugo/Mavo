package com.mavo.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.ui.AppNavigationDestination
import com.mavo.app.ui.theme.AppColors
import com.mavo.app.ui.theme.BrandColors
import com.mavo.app.ui.theme.Surface

/**
 * Clean Mavo Navigation Bar.
 * Follows 60:30:10 rule:
 * 60% Peach Paper base, 30% Aubergine Ink structure & text, 10% Mango Punch accent.
 * Never white text on Mango Punch (Aubergine on Mango 6.9:1).
 */
@Composable
fun AdaptiveNavigationBar(
    destinations: List<AppNavigationDestination>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onNewSale: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface.paperBackground)
            .padding(horizontal = 0.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Surface.card)
                .border(1.dp, Surface.cardBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            destinations.forEach { destination ->
                val selected = currentRoute == destination.route
                val pillBg by animateColorAsState(
                    targetValue = if (selected) BrandColors.peachDarker else Color.Transparent,
                    label = "nav_pill_bg"
                )
                val iconColor by animateColorAsState(
                    targetValue = if (selected) BrandColors.mangoPunch else AppColors.textSecondary,
                    label = "nav_icon_color"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(pillBg)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!selected) onNavigate(destination.route)
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
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
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = destination.label,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) AppColors.textPrimary else AppColors.textSecondary
                        )
                    }
                }
            }

            // Quick Add Floating Button with 10% Mango Punch accent & Aubergine Ink text/icon
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(BrandColors.mangoPunch)
                    .clickable { onNewSale() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Voucher",
                        tint = BrandColors.aubergineInk,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Bill",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandColors.aubergineInk
                    )
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
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(88.dp)
            .background(Surface.card)
            .border(width = 1.dp, color = Surface.cardBorder)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FloatingActionButton(
            onClick = onNewSale,
            containerColor = BrandColors.mangoPunch,
            contentColor = BrandColors.aubergineInk,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.size(52.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Voucher", tint = BrandColors.aubergineInk)
        }

        Spacer(modifier = Modifier.height(8.dp))

        destinations.forEach { destination ->
            val selected = currentRoute == destination.route
            val pillBg by animateColorAsState(
                targetValue = if (selected) BrandColors.peachDarker else Color.Transparent,
                label = "rail_pill_bg"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(pillBg)
                    .clickable { if (!selected) onNavigate(destination.route) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label,
                        tint = if (selected) BrandColors.mangoPunch else AppColors.textSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = destination.label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) AppColors.textPrimary else AppColors.textSecondary
                    )
                }
            }
        }
    }
}
