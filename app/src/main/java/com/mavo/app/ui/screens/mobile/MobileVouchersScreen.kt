package com.mavo.app.ui.screens.mobile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.mavo.app.ui.animation.premiumFabEntrance
import com.mavo.app.ui.animation.pressScale
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.data.Utils
import com.mavo.app.data.Voucher
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.UiLoadState
import com.mavo.app.ui.animation.premiumCombinedClickable
import com.mavo.app.ui.components.ChipRow
import com.mavo.app.ui.components.EmptyState
import com.mavo.app.ui.components.ScreenBorder
import com.mavo.app.ui.components.SearchField
import com.mavo.app.ui.components.SegmentedTabs
import com.mavo.app.ui.components.UiLoadStateNotice
import com.mavo.app.ui.components.blocksContent
import com.mavo.app.ui.components.valueOrEmpty
import com.mavo.app.ui.selection.UniversalSelectionController
import com.mavo.app.ui.selection.UniversalSelectionIndicator
import com.mavo.app.ui.screens.common.VoucherFilterState
import com.mavo.app.ui.screens.common.applyVoucherFilters
import com.mavo.app.ui.screens.common.applyVoucherSorting
import com.mavo.app.ui.screens.common.voucherBadgeColor
import com.mavo.app.ui.screens.common.voucherTypeLabel
import com.mavo.app.ui.theme.AppColors
import com.mavo.app.ui.screens.VoucherDeleteSheet
import kotlinx.coroutines.launch

/**
 * Full Mobile screen view for Vouchers screen.
 * Single-column Scaffold layout, 16dp padding, mobile list rows.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MobileVouchersScreen(
    viewModel: AppViewModel,
    navigateToNewVoucher: (String?) -> Unit,
    navigateToVoucherDetail: (String) -> Unit
) {
    val vouchersReadState by viewModel.vouchersUiState.collectAsState()
    val partiesReadState by viewModel.partiesUiState.collectAsState()
    val ledgerEntriesReadState by viewModel.ledgerEntriesUiState.collectAsState()
    val vouchers = vouchersReadState.valueOrEmpty()
    val parties = partiesReadState.valueOrEmpty()
    val ledgerEntries = ledgerEntriesReadState.valueOrEmpty()
    val cachedVoucherError = (vouchersReadState as? UiLoadState.Error<*>)?.hasCachedValue == true
    val context = LocalContext.current
    val partyNameById by remember(parties) {
        derivedStateOf { parties.associate { it.id to it.name } }
    }

    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    var appliedFilterState by remember { mutableStateOf(VoucherFilterState()) }
    var pendingFilterState by remember { mutableStateOf(VoucherFilterState()) }
    var appliedSortOption by remember { mutableStateOf(SORT_DEFAULT) }
    var pendingSortOption by remember { mutableStateOf(SORT_DEFAULT) }
    var activeDatePicker by remember { mutableStateOf<String?>(null) }
    val selectionController = remember { UniversalSelectionController() }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var deleteConfirmVoucherIds by remember { mutableStateOf<List<String>>(emptyList()) }
    var statusTab by remember { mutableStateOf(0) }
    val profileReadState by viewModel.profileUiState.collectAsState()
    val profile = when (val state = profileReadState) {
        is UiLoadState.Content -> state.value
        is UiLoadState.Empty -> state.value
        is UiLoadState.Error -> state.cachedValue
        UiLoadState.Loading -> null
    }

    val filteredVouchers by remember(vouchers, searchQuery, appliedFilterState, partyNameById) {
        derivedStateOf {
            applyVoucherFilters(vouchers, searchQuery, appliedFilterState, partyNameById)
        }
    }

    val displayedVouchers by remember(filteredVouchers, appliedSortOption, partyNameById, statusTab) {
        derivedStateOf {
            val byStatus = when (statusTab) {
                1 -> filteredVouchers.filter { it.status == "POSTED" }
                2 -> filteredVouchers.filter { it.status == "DRAFT" }
                else -> filteredVouchers
            }
            applyVoucherSorting(byStatus, appliedSortOption, partyNameById)
        }
    }

    var selectedVoucherId by remember { mutableStateOf<String?>(null) }
    val mobileVoucherListState = rememberLazyListState()

    LaunchedEffect(displayedVouchers, selectedVoucherId) {
        val selectedStillVisible = selectedVoucherId != null && displayedVouchers.any { it.id == selectedVoucherId }
        if (!selectedStillVisible) {
            selectedVoucherId = displayedVouchers.firstOrNull()?.id
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Filter vouchers", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    TextButton(onClick = {
                        pendingFilterState = VoucherFilterState()
                        appliedFilterState = VoucherFilterState()
                        showFilterSheet = false
                    }) { Text("Clear All") }
                }

                Text("Voucher Type", fontWeight = FontWeight.SemiBold, color = AppColors.textSecondary)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    voucherTypeFilterOptions.forEach { type ->
                        FilterChip(
                            selected = pendingFilterState.type == type || (pendingFilterState.type == null && type == "ALL"),
                            onClick = {
                                pendingFilterState = pendingFilterState.copy(type = if (type == "ALL") null else type)
                            },
                            label = { Text(voucherTypeLabel(type)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AppColors.primary,
                                selectedLabelColor = AppColors.textOnPrimary
                            )
                        )
                    }
                }

                Text("Date", fontWeight = FontWeight.SemiBold, color = AppColors.textSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { activeDatePicker = "start" }) {
                        Text(pendingFilterState.startDate?.let { Utils.formatDate(it) } ?: "Start date")
                    }
                    OutlinedButton(onClick = { activeDatePicker = "end" }) {
                        Text(pendingFilterState.endDate?.let { Utils.formatDate(it) } ?: "End date")
                    }
                }

                Text("Party", fontWeight = FontWeight.SemiBold, color = AppColors.textSecondary)
                OutlinedTextField(
                    value = pendingFilterState.partyName.orEmpty(),
                    onValueChange = { pendingFilterState = pendingFilterState.copy(partyName = it) },
                    label = { Text("Party name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Payment Status", fontWeight = FontWeight.SemiBold, color = AppColors.textSecondary)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    voucherPaymentStatusOptions.forEach { (value, label) ->
                        FilterChip(
                            selected = pendingFilterState.paymentStatus == value,
                            onClick = { pendingFilterState = pendingFilterState.copy(paymentStatus = if (pendingFilterState.paymentStatus == value) null else value) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AppColors.primary,
                                selectedLabelColor = AppColors.textOnPrimary
                            )
                        )
                    }
                }

                Text("Amount", fontWeight = FontWeight.SemiBold, color = AppColors.textSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pendingFilterState.minAmount?.toString().orEmpty(),
                        onValueChange = { value -> pendingFilterState = pendingFilterState.copy(minAmount = value.toDoubleOrNull()) },
                        label = { Text("Min") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = pendingFilterState.maxAmount?.toString().orEmpty(),
                        onValueChange = { value -> pendingFilterState = pendingFilterState.copy(maxAmount = value.toDoubleOrNull()) },
                        label = { Text("Max") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Reference", fontWeight = FontWeight.SemiBold, color = AppColors.textSecondary)
                OutlinedTextField(
                    value = pendingFilterState.reference.orEmpty(),
                    onValueChange = { pendingFilterState = pendingFilterState.copy(reference = it) },
                    label = { Text("Reference no.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { showFilterSheet = false }) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        appliedFilterState = pendingFilterState
                        showFilterSheet = false
                    }) { Text("Apply") }
                }
            }
        }
    }

    if (showSortSheet) {
        ModalBottomSheet(onDismissRequest = { showSortSheet = false }) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Sort vouchers", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                voucherSortOptions.forEach { option ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { pendingSortOption = option.value },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(option.label)
                        if (pendingSortOption == option.value) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = AppColors.primary)
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { showSortSheet = false }) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        appliedSortOption = pendingSortOption
                        showSortSheet = false
                    }) { Text("Apply") }
                }
            }
        }
    }

    if (activeDatePicker != null) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = if (activeDatePicker == "start") pendingFilterState.startDate ?: System.currentTimeMillis() else pendingFilterState.endDate ?: System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { activeDatePicker = null },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = pickerState.selectedDateMillis
                    pendingFilterState = if (activeDatePicker == "start") {
                        pendingFilterState.copy(startDate = selectedDate)
                    } else {
                        pendingFilterState.copy(endDate = selectedDate)
                    }
                    activeDatePicker = null
                }) { Text("Done") }
            },
            dismissButton = {
                TextButton(onClick = { activeDatePicker = null }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState, showModeToggle = false)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (selectionController.isSelectionActive) {
                VoucherSelectionBar(
                    controller = selectionController,
                    visibleItemCount = displayedVouchers.size,
                    onClose = { selectionController.exitSelection() },
                    onSelectAll = { selectionController.toggleSelectAll(displayedVouchers.map { it.id }) },
                    onExport = {
                        val ids = selectionController.selectedIdsSnapshot()
                        val rows = buildString {
                            append("voucher_no,type,party,date,amount,status\n")
                            vouchers.filter { it.id in ids }.forEach { v ->
                                append(
                                    listOf(
                                        v.voucherNo,
                                        v.type,
                                        v.partyId?.let { pid -> partyNameById[pid] } ?: "Cash / Bank Account",
                                        voucherShortDate(v.date),
                                        v.netAmount.toString(),
                                        v.status
                                    ).joinToString(",")
                                )
                                append('\n')
                            }
                        }
                    },
                    onDelete = { deleteConfirmVoucherIds = selectionController.selectedIdsSnapshot().toList() }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToNewVoucher(null) },
                containerColor = AppColors.primary,
                contentColor = AppColors.textOnPrimary,
                modifier = Modifier
                    .premiumFabEntrance()
                    .pressScale()
                    .testTag("add_voucher_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Voucher")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(innerPadding)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.cardBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vouchers",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "FY ${profile?.fyLabel?.takeIf { it.isNotBlank() } ?: viewModel.financialYear.value}",
                        fontSize = 15.sp,
                        color = AppColors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.cardBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SearchField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Search by no., party, amount",
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("voucher_search_bar")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SegmentedTabs(
                        options = listOf("All", "Posted", "Drafts"),
                        selectedIndex = statusTab,
                        onSelect = { statusTab = it },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ChipRow(
                        options = listOf("All", "Sale", "Purchase", "Receipt", "Payment", "Journal"),
                        selectedIndex = voucherChipIndex(appliedFilterState.type),
                        onSelect = { index ->
                            val type = voucherChipTypes[index]
                            appliedFilterState = appliedFilterState.copy(type = type)
                            pendingFilterState = pendingFilterState.copy(type = type)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                pendingFilterState = appliedFilterState
                                showFilterSheet = true
                            }
                        ) {
                            BadgedBox(
                                badge = {
                                    if (appliedFilterState.activeFilterCount > 0) {
                                        Badge(
                                            containerColor = AppColors.primary,
                                            contentColor = AppColors.textOnPrimary,
                                            modifier = Modifier.size(8.dp)
                                        ) {}
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filter",
                                    tint = AppColors.textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Filter",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.textPrimary
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                pendingSortOption = appliedSortOption
                                showSortSheet = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Sort",
                                tint = AppColors.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (appliedSortOption == SORT_DEFAULT) {
                                    "Sort \u00B7 Date"
                                } else {
                                    "Sort \u00B7 ${voucherSortLabel(appliedSortOption)}"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.textPrimary
                            )
                        }
                    }

                    if (appliedFilterState.activeFilterCount > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            appliedFilterState.toActiveChips().forEach { chip ->
                                InputChip(
                                    selected = false,
                                    onClick = { appliedFilterState = appliedFilterState.removeFilter(chip.key) },
                                    label = { Text(chip.label, fontSize = 12.sp) },
                                    trailingIcon = {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                )
                            }
                            AssistChip(
                                onClick = {
                                    appliedFilterState = VoucherFilterState()
                                    pendingFilterState = VoucherFilterState()
                                },
                                label = { Text("Clear All", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = ScreenBorder)
            Spacer(modifier = Modifier.height(12.dp))
            UiLoadStateNotice(partiesReadState, viewModel::retryFailedReads)
            UiLoadStateNotice(ledgerEntriesReadState, viewModel::retryFailedReads)
            if (cachedVoucherError) {
                UiLoadStateNotice(vouchersReadState, viewModel::retryFailedReads)
            }

            if (vouchersReadState.blocksContent()) {
                UiLoadStateNotice(
                    state = vouchersReadState,
                    onRetry = viewModel::retryFailedReads,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
            } else if (vouchers.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        title = "No vouchers yet",
                        message = "Tap + to create your first voucher"
                    )
                }
            } else if (displayedVouchers.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.cardBg)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = AppColors.textTertiary,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No vouchers match your filters.",
                                color = AppColors.textPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Adjust the filters or clear them to restore the full list.",
                                color = AppColors.textSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                            if (appliedFilterState.activeFilterCount > 0 || searchQuery.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                TextButton(onClick = {
                                    searchQuery = ""
                                    appliedFilterState = VoucherFilterState()
                                    pendingFilterState = VoucherFilterState()
                                }) { Text("Clear Filters") }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppColors.cardBg)
                ) {
                    LazyColumn(
                        state = mobileVoucherListState,
                        contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(displayedVouchers, key = { it.id }) { voucher ->
                            VoucherListRow(
                                voucher = voucher,
                                partyName = voucher.partyId?.let { partyNameById[it] } ?: "Cash / Bank Account",
                                selected = selectionController.isSelected(voucher.id),
                                selectionActive = selectionController.isSelectionActive,
                                onClick = {
                                    if (selectionController.isSelectionActive) {
                                        selectionController.toggleSelection(voucher.id)
                                    } else {
                                        navigateToVoucherDetail(voucher.id)
                                    }
                                },
                                onLongClick = { selectionController.enterSelection(voucher.id) },
                                isLast = voucher.id == displayedVouchers.lastOrNull()?.id
                            )
                        }
                    }
                }
            }
        }
    }

    VoucherDeleteSheet(
        visible = deleteConfirmVoucherIds.isNotEmpty(),
        count = deleteConfirmVoucherIds.size,
        totalValue = vouchers.filter { it.id in deleteConfirmVoucherIds }.sumOf { it.netAmount },
        ledgerEntryCount = ledgerEntries.count { it.voucherId in deleteConfirmVoucherIds },
        onConfirm = {
            val ids = deleteConfirmVoucherIds
            val deleteCount = ids.size
            ids.forEach { id -> viewModel.deleteVoucher(id) }
            selectionController.exitSelection()
            deleteConfirmVoucherIds = emptyList()
            coroutineScope.launch {
                snackbarHostState.showSnackbar(if (deleteCount == 1) "Voucher deleted." else "$deleteCount vouchers deleted.")
            }
        },
        onDismiss = { deleteConfirmVoucherIds = emptyList() }
    )
}

private val voucherChipTypes = listOf<String?>(null, "SALE", "PURCHASE", "RECEIPT", "PAYMENT", "JOURNAL")

private fun voucherChipIndex(type: String?): Int =
    voucherChipTypes.indexOf(type).coerceAtLeast(0)

private fun voucherShortDate(timestamp: Long): String =
    java.text.SimpleDateFormat("dd MMM", java.util.Locale.ENGLISH)
        .format(java.util.Date(timestamp))

// ponytail: mockup tiles Archive + Share skipped — no archive column / multi-PDF bundle exists.
// upgrade path: status = "ARCHIVED" migration + combined share sheet.
@Composable
private fun VoucherSelectionBar(
    controller: UniversalSelectionController,
    visibleItemCount: Int,
    onClose: () -> Unit,
    onSelectAll: () -> Unit,
    onExport: () -> Unit,
    onDelete: () -> Unit
) {
    if (!controller.isSelectionActive) return
    var menuOpen by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close selection",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${controller.selectedCount} selected",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            Box {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .clickable { menuOpen = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "More selection options",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = {
                            Text(if (controller.isAllSelected(visibleItemCount)) "Deselect all" else "Select all")
                        },
                        onClick = {
                            menuOpen = false
                            onSelectAll()
                        }
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SelectionTile(
                icon = Icons.Default.Download,
                label = "Export",
                onClick = onExport,
                modifier = Modifier.weight(1f)
            )
            SelectionTile(
                icon = Icons.Default.Delete,
                label = "Delete",
                onClick = onDelete,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SelectionTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun VoucherListRow(
    voucher: Voucher,
    partyName: String,
    selected: Boolean,
    selectionActive: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isLast: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selected) AppColors.primary.copy(alpha = 0.06f) else Color.Transparent)
            .premiumCombinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectionActive) {
                    UniversalSelectionIndicator(isSelected = selected)
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = partyName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (voucher.status == "DRAFT") {
                    Spacer(modifier = Modifier.width(8.dp))
                    DashedBadge(text = "DRAFT")
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = Utils.formatIndianCurrency(voucher.netAmount),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${voucherTypeLabel(voucher.type)} \u00B7 ${voucher.voucherNo} \u00B7 ${voucherShortDate(voucher.date)}",
            fontSize = 14.sp,
            color = AppColors.textSecondary
        )
        if (!isLast) {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = ScreenBorder, thickness = 1.dp)
        }
    }
}

@Composable
private fun DashedBadge(text: String) {
    Box {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                color = AppColors.textTertiary,
                cornerRadius = CornerRadius(size.height / 2f),
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
                )
            )
        }
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
            color = AppColors.textSecondary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// These types use financial rows or one amount instead of inventory product rows.
private val itemlessVoucherTypes = setOf("RECEIPT", "PAYMENT", "INCOME", "EXPENSE", "JOURNAL", "PETTY_CASH", "INQUIRY")

private val voucherTypeFilterOptions = listOf(
    "ALL",
    "SALE",
    "PURCHASE",
    "QUOTATION",
    "DELIVERY_CHALLAN",
    "SALES_ORDER",
    "PURCHASE_ORDER",
    "GOODS_RECEIPT_NOTE",
    "CREDIT_NOTE",
    "DEBIT_NOTE",
    "RECEIPT",
    "PAYMENT",
    "INCOME",
    "EXPENSE",
    "JOURNAL",
    "INQUIRY",
    "PROFORMA",
    "MATERIAL_NOTE",
    "REJECTION_NOTE",
    "PETTY_CASH",
    "SALE_RETURN",
    "PURCHASE_RETURN",
    "BILLS_RECEIVABLE",
    "BILLS_PAYABLE"
)

private const val SORT_DEFAULT = "DEFAULT"
private const val SORT_NEWEST_FIRST = "NEWEST_FIRST"
private const val SORT_OLDEST_FIRST = "OLDEST_FIRST"
private const val SORT_HIGHEST_AMOUNT = "HIGHEST_AMOUNT"
private const val SORT_LOWEST_AMOUNT = "LOWEST_AMOUNT"
private const val SORT_VOUCHER_NUMBER = "VOUCHER_NUMBER"
private const val SORT_PARTY_NAME_AZ = "PARTY_NAME_AZ"
private const val SORT_PARTY_NAME_ZA = "PARTY_NAME_ZA"

private val voucherPaymentStatusOptions = listOf(
    "ANY" to "Any status",
    "PAID" to "Paid",
    "PARTIAL" to "Partially paid",
    "UNPAID" to "Unpaid"
)

private val voucherSortOptions = listOf(
    com.mavo.app.ui.screens.common.VoucherSortOption(SORT_DEFAULT, "Default order"),
    com.mavo.app.ui.screens.common.VoucherSortOption(SORT_NEWEST_FIRST, "Newest First"),
    com.mavo.app.ui.screens.common.VoucherSortOption(SORT_OLDEST_FIRST, "Oldest First"),
    com.mavo.app.ui.screens.common.VoucherSortOption(SORT_HIGHEST_AMOUNT, "Highest Amount"),
    com.mavo.app.ui.screens.common.VoucherSortOption(SORT_LOWEST_AMOUNT, "Lowest Amount"),
    com.mavo.app.ui.screens.common.VoucherSortOption(SORT_VOUCHER_NUMBER, "Voucher Number"),
    com.mavo.app.ui.screens.common.VoucherSortOption(SORT_PARTY_NAME_AZ, "Party Name (A–Z)"),
    com.mavo.app.ui.screens.common.VoucherSortOption(SORT_PARTY_NAME_ZA, "Party Name (Z–A)")
)

private fun voucherSortLabel(sortOption: String): String =
    voucherSortOptions.firstOrNull { it.value == sortOption }?.label ?: "Default order"

private data class VoucherFilterChip(val key: String, val label: String)

private fun VoucherFilterState.toActiveChips(): List<VoucherFilterChip> {
    val chips = mutableListOf<VoucherFilterChip>()
    type?.takeIf { it.isNotBlank() && it != "ALL" }?.let {
        chips += VoucherFilterChip("type", voucherTypeLabel(it))
    }
    paymentStatus?.takeIf { it.isNotBlank() && it != "ANY" }?.let {
        chips += VoucherFilterChip("paymentStatus", voucherPaymentStatusLabel(it))
    }
    partyName?.takeIf { it.isNotBlank() }?.let {
        chips += VoucherFilterChip("partyName", it)
    }
    minAmount?.takeIf { it > 0.0 }?.let {
        chips += VoucherFilterChip("minAmount", "Min: ₹${"%.0f".format(it)}")
    }
    maxAmount?.takeIf { it > 0.0 }?.let {
        chips += VoucherFilterChip("maxAmount", "Max: ₹${"%.0f".format(it)}")
    }
    reference?.takeIf { it.isNotBlank() }?.let {
        chips += VoucherFilterChip("reference", "Ref: $it")
    }
    if (startDate != null || endDate != null) {
        val label = when {
            startDate != null && endDate != null -> "Date: ${Utils.formatDate(startDate)} – ${Utils.formatDate(endDate)}"
            startDate != null -> "From: ${Utils.formatDate(startDate)}"
            endDate != null -> "To: ${Utils.formatDate(endDate)}"
            else -> "Date range"
        }
        chips += VoucherFilterChip("dateRange", label)
    }
    return chips
}

private fun VoucherFilterState.removeFilter(key: String): VoucherFilterState = when (key) {
    "type" -> copy(type = null)
    "paymentStatus" -> copy(paymentStatus = null)
    "partyName" -> copy(partyName = null)
    "minAmount" -> copy(minAmount = null)
    "maxAmount" -> copy(maxAmount = null)
    "reference" -> copy(reference = null)
    "dateRange" -> copy(startDate = null, endDate = null)
    else -> this
}

private fun voucherPaymentStatusLabel(status: String?): String =
    voucherPaymentStatusOptions.firstOrNull { it.first == status }?.second ?: "Any status"
