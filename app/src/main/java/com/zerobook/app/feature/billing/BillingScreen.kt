package com.zerobook.app.feature.billing

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.windowsizeclass.*
import com.zerobook.app.data.Product
import com.zerobook.app.data.Utils
import com.zerobook.app.data.Voucher
import com.zerobook.app.data.VoucherItem
import com.zerobook.app.ui.AppViewModel
import com.zerobook.app.ui.animation.m3PulseHighlight
import com.zerobook.app.ui.animation.m3SpringPress
import com.zerobook.app.ui.animation.premiumClickable
import com.zerobook.app.ui.theme.LocalAppTheme
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun BillingScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    val theme = LocalAppTheme.current
    val products by viewModel.products.collectAsState()
    val parties by viewModel.parties.collectAsState()
    val cart = remember { mutableStateListOf<VoucherItem>() }
    var selectedUnitFilter by remember { mutableStateOf("ALL") }
    var walkInCustomer by remember { mutableStateOf(true) }
    var selectedPartyId by remember { mutableStateOf<String?>(null) }
    var paymentMode by remember { mutableStateOf("CASH") }
    var search by remember { mutableStateOf("") }
    val activity = LocalContext.current as Activity
    val windowSizeClass = calculateWindowSizeClass(activity)

    val filteredProducts = remember(products, selectedUnitFilter, search) {
        products.filter {
            (selectedUnitFilter == "ALL" || it.unit == selectedUnitFilter) &&
                it.name.contains(search, true)
        }
    }
    val total = cart.sumOf { it.totalAmount }

    Scaffold(
        containerColor = theme.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = theme.accentPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = theme.accentPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                "Express Counter Billing",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = theme.textPrimary
                            )
                            Text(
                                "Material 3 POS Terminal",
                                fontSize = 11.sp,
                                color = theme.textTertiary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.m3SpringPress()
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = theme.textPrimary
                        )
                    }
                },
                actions = {
                    if (cart.isNotEmpty()) {
                        AssistChip(
                            onClick = { cart.clear() },
                            label = { Text("Clear Cart (${cart.sumOf { it.qty.toInt() }})", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = theme.backgroundTertiary,
                                labelColor = theme.textSecondary
                            ),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .m3SpringPress()
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = theme.backgroundSecondary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Customer Type & Party Selection Section
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = theme.backgroundSecondary),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = theme.accentPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                if (walkInCustomer) "Walk-in Customer" else "Select Customer / Party",
                                fontWeight = FontWeight.SemiBold,
                                color = theme.textPrimary,
                                fontSize = 14.sp
                            )
                        }
                        Switch(
                            checked = walkInCustomer,
                            onCheckedChange = {
                                walkInCustomer = it
                                if (it) selectedPartyId = null
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = theme.accentPrimary,
                                uncheckedBorderColor = theme.textTertiary
                            )
                        )
                    }

                    AnimatedVisibility(
                        visible = !walkInCustomer,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            parties.filter { it.type == "CUSTOMER" || it.type == "BOTH" }.take(8).forEach { party ->
                                val isSelected = selectedPartyId == party.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedPartyId = if (isSelected) null else party.id },
                                    label = { Text(party.name, fontSize = 12.sp) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = theme.accentPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = theme.backgroundTertiary,
                                        labelColor = theme.textPrimary
                                    ),
                                    modifier = Modifier.m3SpringPress()
                                )
                            }
                        }
                    }
                }
            }

            // Products Grid vs Cart Summary
            ResponsivePaneLayout(
                widthSizeClass = windowSizeClass.widthSizeClass,
                modifier = Modifier.weight(1f),
                productContent = {
                // Product Selection Column
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        placeholder = { Text("Search items...", color = theme.textTertiary, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = theme.accentPrimary) },
                        trailingIcon = if (search.isNotEmpty()) {
                            {
                                IconButton(onClick = { search = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = theme.textTertiary)
                                }
                            }
                        } else null,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = theme.accentPrimary,
                            unfocusedBorderColor = theme.backgroundTertiary,
                            focusedContainerColor = theme.backgroundSecondary,
                            unfocusedContainerColor = theme.backgroundSecondary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Unit Filter Chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("ALL" to "All")
                            .plus(products.map { it.unit }.distinct().take(3).map { it to it })
                            .forEach { (value, label) ->
                                val isSelected = selectedUnitFilter == value
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedUnitFilter = value },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = theme.accentPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = theme.backgroundSecondary,
                                        labelColor = theme.textSecondary
                                    ),
                                    modifier = Modifier.m3SpringPress()
                                )
                            }
                    }

                    // Products list
                    filteredProducts.forEach { product ->
                        ElevatedCard(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = theme.backgroundSecondary),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .premiumClickable {
                                    val existing = cart.indexOfFirst { it.productId == product.id }
                                    if (existing >= 0) {
                                        val current = cart[existing]
                                        cart[existing] = current.copy(
                                            qty = current.qty + 1,
                                            taxableAmount = (current.qty + 1) * current.rate,
                                            totalAmount = ((current.qty + 1) * current.rate) + current.cgstAmount + current.sgstAmount + current.igstAmount
                                        )
                                    } else {
                                        cart.add(product.toBillingItem())
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        product.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = theme.textPrimary
                                    )
                                    Text(
                                        "Stock: ${product.currentStock} ${product.unit}",
                                        fontSize = 11.sp,
                                        color = theme.textTertiary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        Utils.formatIndianCurrency(product.saleRate),
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 14.sp,
                                        color = theme.accentPrimary
                                    )
                                    Surface(
                                        shape = CircleShape,
                                        color = theme.accentPrimary.copy(alpha = 0.1f),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Add",
                                            tint = theme.accentPrimary,
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                },
                cartContent = {

                // Cart & Payment Column
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = theme.accentPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                "Current Cart",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = theme.textPrimary
                            )
                        }
                        if (cart.isNotEmpty()) {
                            Surface(
                                shape = CircleShape,
                                color = theme.accentPrimary,
                                modifier = Modifier.m3PulseHighlight(active = cart.isNotEmpty())
                            ) {
                                Text(
                                    "${cart.sumOf { it.qty.toInt() }} items",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            }
                    }

                    // Cart Items List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .animateContentSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(cart, key = { it.id }) { item ->
                            OutlinedCard(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.outlinedCardColors(containerColor = theme.backgroundSecondary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            item.productName,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = theme.textPrimary
                                        )
                                        Text(
                                            "${item.qty.toInt()} x ${Utils.formatIndianCurrency(item.rate)}",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = theme.textSecondary
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                val newQty = item.qty - 1
                                                if (newQty <= 0.0) {
                                                    cart.remove(item)
                                                } else {
                                                    val index = cart.indexOfFirst { it.id == item.id }
                                                    cart[index] = item.copy(
                                                        qty = newQty,
                                                        taxableAmount = newQty * item.rate,
                                                        totalAmount = newQty * item.rate
                                                    )
                                                }
                                            },
                                            modifier = Modifier
                                                .size(28.dp)
                                                .m3SpringPress()
                                        ) {
                                            Icon(
                                                Icons.Default.Remove,
                                                contentDescription = "Reduce",
                                                tint = theme.accentPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        Text(
                                            Utils.formatIndianCurrency(item.totalAmount),
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 13.sp,
                                            color = theme.textPrimary,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )

                                        IconButton(
                                            onClick = {
                                                val index = cart.indexOfFirst { it.id == item.id }
                                                val newQty = item.qty + 1
                                                cart[index] = item.copy(
                                                    qty = newQty,
                                                    taxableAmount = newQty * item.rate,
                                                    totalAmount = newQty * item.rate
                                                )
                                            },
                                            modifier = Modifier
                                                .size(28.dp)
                                                .m3SpringPress()
                                        ) {
                                            Icon(
                                                Icons.Default.Add,
                                                contentDescription = "Increase",
                                                tint = theme.accentPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Payment Mode Selector
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("CASH" to "Cash", "UPI" to "UPI QR", "CARD" to "Bank/Card").forEach { (mode, label) ->
                            val isSelected = paymentMode == (if (mode == "CARD") "BANK" else mode)
                            FilterChip(
                                selected = isSelected,
                                onClick = { paymentMode = if (mode == "CARD") "BANK" else mode },
                                label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = theme.accentPrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = theme.backgroundSecondary,
                                    labelColor = theme.textPrimary
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .m3SpringPress()
                            )
                        }
                    }

                    // Total Amount Display
                    ElevatedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = theme.accentLight
                        ),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .m3PulseHighlight(active = cart.isNotEmpty())
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "NET PAYABLE TOTAL",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.accentPrimary
                                )
                                Text(
                                    "Incl. GST Taxes",
                                    fontSize = 10.sp,
                                    color = theme.textTertiary
                                )
                            }
                            Text(
                                Utils.formatIndianCurrency(total),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = theme.accentPrimary
                            )
                        }
                    }

                    // Final Submit Button
                    Button(
                        onClick = {
                            if (cart.isEmpty()) {
                                Toast.makeText(viewModel.getApplication(), "Please add items to cart", Toast.LENGTH_SHORT).show()
                            } else {
                                val now = System.currentTimeMillis()
                                CoroutineScope(Dispatchers.Main).launch {
                                    val voucherNo = viewModel.generateNextVoucherNo("SALE", now)
                                    viewModel.saveVoucher(
                                        voucher = Voucher(
                                            id = UUID.randomUUID().toString(),
                                            voucherNo = voucherNo,
                                            type = "SALE",
                                            date = now,
                                            partyId = selectedPartyId,
                                            narration = "Counter POS sale",
                                            taxableAmount = cart.sumOf { it.taxableAmount },
                                            cgst = cart.sumOf { it.cgstAmount },
                                            sgst = cart.sumOf { it.sgstAmount },
                                            igst = cart.sumOf { it.igstAmount },
                                            roundOff = 0.0,
                                            netAmount = total,
                                            paymentMode = paymentMode,
                                            chequeNo = null,
                                            chequeDate = null,
                                            bankName = null,
                                            isIgst = false,
                                            status = "POSTED"
                                        ),
                                        items = cart.toList(),
                                        partyName = parties.find { it.id == selectedPartyId }?.name
                                    ) {
                                        cart.clear()
                                        Toast.makeText(viewModel.getApplication(), "Counter sale invoice saved!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = theme.accentPrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .m3SpringPress()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.PointOfSale,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "GENERATE BILL",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        )
    }
}

}

private fun Product.toBillingItem(): VoucherItem = VoucherItem(
    id = UUID.randomUUID().toString(),
    voucherId = "",
    productId = id,
    productName = name,
    hsnCode = hsnCode,
    qty = 1.0,
    unit = unit,
    rate = saleRate,
    discount = 0.0,
    discountType = "AMOUNT",
    taxableAmount = saleRate,
    gstRate = gstRate,
    cgstAmount = (saleRate * gstRate / 100.0) / 2.0,
    sgstAmount = (saleRate * gstRate / 100.0) / 2.0,
    igstAmount = 0.0,
    totalAmount = saleRate + (saleRate * gstRate / 100.0)
)
