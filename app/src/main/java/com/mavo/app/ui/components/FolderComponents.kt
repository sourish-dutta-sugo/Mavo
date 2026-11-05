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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.ui.theme.AppColors
import com.mavo.app.ui.theme.CafePalette
import com.mavo.app.ui.theme.Surface

/**
 * Physical Folder Tab component that evokes an actual business binder / filing folder tab.
 */
@Composable
fun FolderTab(
    title: String,
    icon: ImageVector? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int? = null
) {
    val tabBg by animateColorAsState(
        targetValue = if (isSelected) Surface.folderTabActive else Surface.folderTabInactive,
        label = "folder_tab_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) AppColors.textPrimary else AppColors.textSecondary,
        label = "folder_tab_text"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(tabBg)
            .border(
                width = 1.dp,
                color = if (isSelected) Surface.folderBorder else Surface.borderLight,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) AppColors.primary else AppColors.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = title,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
            if (badgeCount != null && badgeCount > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .background(CafePalette.caramelAccent, RoundedCornerShape(10.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Real Dossier / Sub-Folder Card container with soft coffee cafe borders and subtle shadows.
 */
@Composable
fun FolderDossier(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface.card)
            .border(1.dp, Surface.cardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textPrimary
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = AppColors.textSecondary
                    )
                }
            }
            trailingContent?.invoke()
        }
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

/**
 * Minimalist Sub-file row resembling a ledger line or document record.
 */
@Composable
fun SubFileRow(
    title: String,
    subtitle: String,
    trailingText: String? = null,
    trailingTag: String? = null,
    tagColor: Color = AppColors.primary,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Surface.paperBackground)
            .border(0.5.dp, Surface.border, RoundedCornerShape(10.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.textPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = AppColors.textSecondary
            )
        }
        if (trailingTag != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(tagColor.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = trailingTag,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = tagColor
                )
            }
        }
        if (trailingText != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = trailingText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary
            )
        }
    }
}

