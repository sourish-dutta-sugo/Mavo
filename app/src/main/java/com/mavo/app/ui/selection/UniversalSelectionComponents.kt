package com.mavo.app.ui.selection

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mavo.app.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalSelectionTopAppBar(
    controller: UniversalSelectionController,
    visibleItemCount: Int,
    onClose: () -> Unit,
    onSelectAll: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (!controller.isSelectionActive) return

    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = "${controller.selectedCount} selected",
                color = AppColors.textPrimary,
                maxLines = 1
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close selection")
            }
        },
        actions = {
            if (controller.selectedCount == 1 && onEdit != null) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppColors.textPrimary)
                }
            }
            if (onDelete != null && controller.selectedCount > 0) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AppColors.error)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.cardBg)
    )
}

@Composable
fun UniversalSelectionIndicator(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    selectedColor: Color = AppColors.primary,
    contentColor: Color = Color.White
) {
    val animatedBackground by animateColorAsState(
        targetValue = if (isSelected) selectedColor else Color.Transparent,
        animationSpec = tween(220),
        label = "selection_indicator_background"
    )
    val animatedBorder by animateColorAsState(
        targetValue = if (isSelected) selectedColor else AppColors.border,
        animationSpec = tween(220),
        label = "selection_indicator_border"
    )

    Box(
        modifier = modifier
            .size(24.dp)
            .border(2.dp, animatedBorder, CircleShape)
            .background(animatedBackground, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
        }
    }
}
