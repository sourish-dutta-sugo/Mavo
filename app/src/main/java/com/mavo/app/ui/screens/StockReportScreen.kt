package com.mavo.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.data.Product
import com.mavo.app.data.Utils
import com.mavo.app.services.ExportStorageManager
import com.mavo.app.services.ExportTarget
import com.mavo.app.ui.components.ChoiceChipRow
import com.mavo.app.ui.components.CircleIconButton
import com.mavo.app.ui.components.ScreenBorder
import com.mavo.app.ui.components.SearchField
import com.mavo.app.ui.components.SectionLabel
import com.mavo.app.ui.components.ZbCard
import com.mavo.app.ui.theme.AppColors
import java.util.Locale

private fun qtyLabel(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()

// ponytail: lakh shorthand only above 1 L — upgrade path: full currency formatter in Utils.
private fun stockValueLabel(value: Double): String =
    if (value >= 100_000) String.format(Locale.ENGLISH, "₹%.2f L", value / 100_000.0)
    else Utils.formatIndianCurrency(value)

@Composable
fun StockReportScreen(products: List<Product>) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("ALL") }
    var searchVisible by remember { mutableStateOf(false) }

    val filteredProducts = remember(products, searchQuery, activeFilter) {
        products.filter { product ->
            val matchesSearch = product.name.contains(searchQuery, true) || product.hsnCode.contains(searchQuery, true)
            val matchesFilter = when (activeFilter) {
                "LOW" -> product.enableStockAlert && product.currentStock > 0.0 && product.currentStock <= product.lowStockThreshold
                "OUT" -> product.currentStock <= 0.0
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    val stockValue = remember(products) { products.sumOf { it.currentStock * it.purchaseRate } }
    val lowOutCount = remember(products) {
        products.count { it.currentStock <= 0.0 || (it.enableStockAlert && it.currentStock <= it.lowStockThreshold) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.screenBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Stock Summary",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            CircleIconButton(
                icon = Icons.Default.Search,
                contentDescription = "Search",
                onClick = { searchVisible = !searchVisible }
            )
        }

        if (searchVisible) {
            Spacer(modifier = Modifier.height(12.dp))
            SearchField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 16.dp),
                placeholder = "Search product or HSN"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(label = "SKUs", value = products.size.toString(), modifier = Modifier.weight(1f))
            StatCard(label = "Stock Value", value = stockValueLabel(stockValue), modifier = Modifier.weight(1f))
            StatCard(label = "Low / Out", value = lowOutCount.toString(), modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = ScreenBorder)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChoiceChipRow(
                options = listOf("All", "Low Stock", "Out of Stock"),
                selectedIndex = when (activeFilter) {
                    "LOW" -> 1
                    "OUT" -> 2
                    else -> 0
                },
                onSelect = { activeFilter = listOf("ALL", "LOW", "OUT")[it] }
            )
        }

        SectionLabel(
            text = "Stock list",
            modifier = Modifier.padding(horizontal = 16.dp),
            action = {
                TextButton(onClick = {
                    val rows = buildString {
                        append("name,hsn,current_stock,unit,low_stock_threshold,status\n")
                        filteredProducts.forEach { product ->
                            val status = when {
                                product.currentStock <= 0.0 -> "Out of Stock"
                                product.enableStockAlert && product.currentStock <= product.lowStockThreshold -> "Low Stock"
                                else -> "In Stock"
                            }
                            append(
                                listOf(
                                    product.name,
                                    product.hsnCode,
                                    product.currentStock.toString(),
                                    product.stockUnit.ifBlank { product.unit },
                                    product.lowStockThreshold.toString(),
                                    status
                                ).joinToString(",")
                            )
                            append('\n')
                        }
                    }
                    val result = ExportStorageManager.exportBytes(
                        context = context,
                        bytes = rows.toByteArray(),
                        displayName = "Mavo_Stock_Report.csv",
                        mimeType = "text/csv",
                        target = ExportTarget.Reports
                    )
                    Toast.makeText(context, "Saved to ${result.locationLabel}", Toast.LENGTH_LONG).show()
                }) {
                    Text("Export CSV")
                }
            }
        )

        ZbCard(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = 0
        ) {
            if (filteredProducts.isEmpty()) {
                Text(
                    text = "No products match.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp,
                    color = AppColors.textSecondary
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    itemsIndexed(filteredProducts) { index, product ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppColors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${qtyLabel(product.currentStock)} ${product.stockUnit.ifBlank { product.unit }} · " +
                                        Utils.formatIndianCurrency(product.currentStock * product.purchaseRate),
                                    fontSize = 13.sp,
                                    color = AppColors.textSecondary
                                )
                            }
                            StatusPill(product)
                        }
                        if (index < filteredProducts.lastIndex) {
                            HorizontalDivider(color = ScreenBorder)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    ZbCard(modifier = modifier, contentPadding = 14) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = AppColors.textSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun StatusPill(product: Product) {
    val out = product.currentStock <= 0.0
    val low = !out && product.enableStockAlert && product.currentStock <= product.lowStockThreshold
    val label = when {
        out -> "Out"
        low -> "Low"
        else -> "In stock"
    }
    val textColor = when {
        out -> AppColors.textOnPrimary
        low -> AppColors.textPrimary
        else -> AppColors.textSecondary
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (out) AppColors.primary else Color.Transparent)
            .border(
                1.dp,
                if (out || low) AppColors.primary else ScreenBorder,
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (out || low) FontWeight.SemiBold else FontWeight.Medium,
            color = textColor
        )
    }
}
