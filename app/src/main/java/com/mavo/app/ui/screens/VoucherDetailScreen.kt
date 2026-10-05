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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.data.Utils
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.components.CircleIconButton
import com.mavo.app.ui.components.EmptyState
import com.mavo.app.ui.components.ScreenBorder
import com.mavo.app.ui.components.ZbCard
import com.mavo.app.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ponytail: Share / PDF / Print open InvoiceScreen which already hosts those actions.
// upgrade path: inline share/pdf pipeline behind a shared action callback.
@Composable
fun VoucherDetailScreen(
    viewModel: AppViewModel,
    voucherId: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onOpenInvoice: () -> Unit
) {
    val vouchers by viewModel.vouchers.collectAsState()
    val parties by viewModel.parties.collectAsState()
    val ledgerEntries by viewModel.ledgerEntries.collectAsState()
    val items by viewModel.getItemsForVoucher(voucherId).collectAsState(initial = emptyList())
    var showDeleteSheet by remember { mutableStateOf(false) }

    val voucher = vouchers.firstOrNull { it.id == voucherId }

    if (voucher == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.screenBg)
        ) {
            DetailHeader(title = "", onBack = onBack, showMenu = false, onDelete = {})
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    title = "Voucher not found",
                    message = "It may have been deleted."
                )
            }
        }
        return
    }

    val party = voucher.partyId?.let { pid -> parties.firstOrNull { it.id == pid } }

    Scaffold(
        containerColor = AppColors.screenBg,
        topBar = {
            DetailHeader(
                title = voucherTypeLabel(voucher.type),
                onBack = onBack,
                showMenu = true,
                onDelete = { showDeleteSheet = true }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(AppColors.cardBg)) {
                HorizontalDivider(color = ScreenBorder)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DetailAction(icon = Icons.Outlined.Edit, label = "Edit", onClick = onEdit, modifier = Modifier.weight(1f))
                    DetailAction(icon = Icons.Default.Share, label = "Share", onClick = onOpenInvoice, modifier = Modifier.weight(1f))
                    DetailAction(icon = Icons.Default.PictureAsPdf, label = "PDF", onClick = onOpenInvoice, modifier = Modifier.weight(1f))
                    DetailAction(icon = Icons.Default.Print, label = "Print", onClick = onOpenInvoice, modifier = Modifier.weight(1f))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "${voucher.voucherNo} \u00B7 ${fullDate(voucher.date)}",
                fontSize = 14.sp,
                color = AppColors.textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = party?.name ?: "Cash / Bank Account",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary,
                lineHeight = 36.sp
            )
            if (!party?.gstin.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "GSTIN ${party?.gstin} \u00B7 ${party?.state.orEmpty().ifBlank { party?.city.orEmpty() }}",
                    fontSize = 14.sp,
                    color = AppColors.textSecondary
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "TOTAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp,
                        color = AppColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Utils.formatIndianCurrency(voucher.netAmount),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )
                }
                StatusPill(status = voucher.status)
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = ScreenBorder)
            Spacer(modifier = Modifier.height(16.dp))

            if (items.isNotEmpty()) {
                ZbCard(modifier = Modifier.fillMaxWidth(), contentPadding = 0) {
                    items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.productName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppColors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${qtyLabel(item.qty)} \u00D7 ${Utils.formatIndianCurrency(item.rate)}",
                                    fontSize = 14.sp,
                                    color = AppColors.textSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = Utils.formatIndianCurrency(item.taxableAmount),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.textPrimary
                            )
                        }
                        if (index != items.lastIndex) {
                            HorizontalDivider(color = ScreenBorder)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            ZbCard(modifier = Modifier.fillMaxWidth()) {
                DetailTotalRow(
                    label = "Subtotal",
                    value = Utils.formatIndianCurrency(voucher.taxableAmount)
                )
                HorizontalDivider(color = ScreenBorder)
                DetailTotalRow(
                    label = "Tax (${if (voucher.isIgst) "IGST" else "CGST + SGST"})",
                    value = Utils.formatIndianCurrency(voucher.cgst + voucher.sgst + voucher.igst)
                )
                HorizontalDivider(color = ScreenBorder)
                DetailTotalRow(
                    label = "Round Off",
                    value = Utils.formatIndianCurrency(voucher.roundOff)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    VoucherDeleteSheet(
        visible = showDeleteSheet,
        count = 1,
        totalValue = voucher.netAmount,
        ledgerEntryCount = ledgerEntries.count { it.voucherId == voucher.id },
        onConfirm = {
            viewModel.deleteVoucher(voucher.id)
            onBack()
        },
        onDismiss = { showDeleteSheet = false }
    )
}

@Composable
private fun DetailHeader(
    title: String,
    onBack: () -> Unit,
    showMenu: Boolean,
    onDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.cardBg)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            onClick = onBack
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
            color = AppColors.textSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
        if (showMenu) {
            Box {
                CircleIconButton(
                    icon = Icons.Default.MoreHoriz,
                    contentDescription = "More options",
                    onClick = { menuOpen = true }
                )
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text("Delete", color = AppColors.error) },
                        onClick = {
                            menuOpen = false
                            onDelete()
                        }
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.width(44.dp))
        }
    }
}

@Composable
private fun StatusPill(status: String) {
    Box(
        modifier = Modifier
            .border(1.dp, ScreenBorder, RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = if (status == "DRAFT") "Draft" else "Posted",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.textPrimary
        )
    }
}

@Composable
private fun DetailAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, ScreenBorder, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = AppColors.textPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, fontSize = 12.sp, color = AppColors.textSecondary)
    }
}

@Composable
private fun DetailTotalRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
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

private fun qtyLabel(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()

private fun fullDate(timestamp: Long): String =
    SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(Date(timestamp))
