package com.mavo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.data.Utils
import com.mavo.app.ui.components.PrimaryButton
import com.mavo.app.ui.components.SecondaryButton
import com.mavo.app.ui.components.ZbCard
import com.mavo.app.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherDeleteSheet(
    visible: Boolean,
    count: Int,
    totalValue: Double,
    ledgerEntryCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppColors.cardBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AppColors.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = AppColors.textOnPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (count == 1) "Delete this voucher?" else "Delete $count vouchers?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This will permanently remove the selected vouchers and reverse their ledger postings. This action cannot be undone.",
                fontSize = 14.sp,
                color = AppColors.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            ZbCard(modifier = Modifier.fillMaxWidth()) {
                DeleteStatRow(label = "Vouchers", value = count.toString())
                HorizontalDivider(color = AppColors.border)
                DeleteStatRow(label = "Ledger entries reversed", value = ledgerEntryCount.toString())
                HorizontalDivider(color = AppColors.border)
                DeleteStatRow(label = "Total value", value = Utils.formatIndianCurrency(totalValue))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryButton(label = "Cancel", onClick = onDismiss, modifier = Modifier.weight(1f))
                PrimaryButton(label = "Delete", onClick = onConfirm, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DeleteStatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp, color = AppColors.textSecondary)
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.textPrimary
        )
    }
}
