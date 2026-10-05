package com.mavo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.data.Product
import com.mavo.app.ui.components.EmptyState
import com.mavo.app.ui.components.PrimaryButton
import com.mavo.app.ui.components.ScreenBorder
import com.mavo.app.ui.components.SecondaryButton
import com.mavo.app.ui.components.WizardHeader
import com.mavo.app.ui.components.WizardTitle
import com.mavo.app.ui.components.ZbCard
import com.mavo.app.ui.theme.AppColors

@Composable
fun LowStockScreen(
    products: List<Product>,
    onBack: () -> Unit,
    onReorder: () -> Unit
) {
    // ponytail: "Reorder" / "Create purchase order" both open an empty Purchase voucher — no line-item prefill;
    // upgrade path: extend VoucherPrefillRequest with productIds.
    val items = remember(products) {
        products
            .filter { it.currentStock <= 0.0 || it.isLowStockAlertTriggered() }
            .sortedBy { it.currentStock }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.screenBg)
    ) {
        WizardHeader(
            stepLabel = "Inventory alerts",
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = AppColors.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${items.size} items need attention",
                    fontSize = 15.sp,
                    color = AppColors.textSecondary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            WizardTitle(
                text = "Low & Out of Stock",
                subtitle = "Reorder before your next dispatch cycle."
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = ScreenBorder)
            Spacer(modifier = Modifier.height(16.dp))

            if (items.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.Inventory2,
                    title = "Stock looks healthy",
                    message = "No products are low or out of stock right now."
                )
            }

            items.forEach { product ->
                ZbCard(contentPadding = 14) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppColors.sectionHeaderBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Inventory2,
                                contentDescription = null,
                                tint = AppColors.textSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Stock ${qtyLabel(product.currentStock)} · Reorder at ${qtyLabel(product.lowStockThreshold)}",
                                fontSize = 13.sp,
                                color = AppColors.textSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(AppColors.cardBg)
                                .border(1.dp, ScreenBorder, RoundedCornerShape(18.dp))
                                .clickable(onClick = onReorder)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Reorder",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.textPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Column(modifier = Modifier.fillMaxWidth().background(AppColors.cardBg)) {
            HorizontalDivider(color = ScreenBorder)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryButton(
                    label = "Dismiss",
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    label = "Create purchase order",
                    onClick = onReorder,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private fun qtyLabel(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
