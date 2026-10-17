package com.mavo.app.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mavo.app.data.AdditionalCharge
import com.mavo.app.data.AppPreferences
import com.mavo.app.data.AppDatabase
import com.mavo.app.data.AppRepository
import com.mavo.app.data.BankCashTransaction
import com.mavo.app.data.BillReceivable
import com.mavo.app.data.BusinessProfile
import com.mavo.app.data.Expense
import com.mavo.app.data.FinancialYearAuditLog
import com.mavo.app.data.FinancialYearUtils
import com.mavo.app.data.LedgerAccount
import com.mavo.app.data.LedgerEntry
import com.mavo.app.data.JournalLine
import com.mavo.app.data.Party
import com.mavo.app.data.Product
import com.mavo.app.data.ReceiptAllocation
import com.mavo.app.data.Voucher
import com.mavo.app.data.VoucherItem
import com.mavo.app.data.VoucherSaveExtras
import com.mavo.app.data.EmailReminderScheduler
import com.mavo.app.data.Income
import com.mavo.app.services.ExportStorageManager
import com.mavo.app.services.ExportTarget
import com.mavo.app.services.InvoiceGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDate

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AppViewModel(application: Application) : AndroidViewModel(application) {
    data class VoucherPrefillRequest(
        val voucherType: String,
        val partyId: String?,
        val invoiceId: String?,
        val amount: Double?,
        val sourceVoucherId: String? = null
    )

    sealed class DbInitState {
        data object Loading : DbInitState()
        data object Success : DbInitState()
        data class Error(val message: String) : DbInitState()
    }

    val dbInitState = MutableStateFlow<DbInitState>(DbInitState.Loading)

    private val repository: AppRepository

    val profile: StateFlow<BusinessProfile?>
    val profileUiState: StateFlow<UiLoadState<BusinessProfile?>>
    val availableFinancialYears: StateFlow<List<String>>
    val availableFinancialYearsUiState: StateFlow<UiLoadState<List<String>>>
    val parties: StateFlow<List<Party>>
    val partiesUiState: StateFlow<UiLoadState<List<Party>>>
    val products: StateFlow<List<Product>>
    val productsUiState: StateFlow<UiLoadState<List<Product>>>
    val vouchers: StateFlow<List<Voucher>>
    val vouchersUiState: StateFlow<UiLoadState<List<Voucher>>>
    val ledgerEntries: StateFlow<List<LedgerEntry>>
    val ledgerEntriesUiState: StateFlow<UiLoadState<List<LedgerEntry>>>
    val transactions: StateFlow<List<BankCashTransaction>>
    val transactionsUiState: StateFlow<UiLoadState<List<BankCashTransaction>>>
    val receiptAllocations: StateFlow<List<ReceiptAllocation>>
    val receiptAllocationsUiState: StateFlow<UiLoadState<List<ReceiptAllocation>>>
    val ledgerAccounts: StateFlow<List<LedgerAccount>>
    val ledgerAccountsUiState: StateFlow<UiLoadState<List<LedgerAccount>>>
    val billsReceivable: StateFlow<List<BillReceivable>>
    val billsReceivableUiState: StateFlow<UiLoadState<List<BillReceivable>>>
    val expenses: StateFlow<List<Expense>>
    val expensesUiState: StateFlow<UiLoadState<List<Expense>>>
    val incomes: StateFlow<List<Income>>
    val incomesUiState: StateFlow<UiLoadState<List<Income>>>

    private val retryGeneration = MutableStateFlow(0)
    private val profileCache = UiDataCache<BusinessProfile?>()
    private val financialYearsCache = UiDataCache<List<String>>()
    private val partiesCache = UiDataCache<List<Party>>()
    private val productsCache = UiDataCache<List<Product>>()
    private val vouchersCache = UiDataCache<List<Voucher>>()
    private val ledgerEntriesCache = UiDataCache<List<LedgerEntry>>()
    private val transactionsCache = UiDataCache<List<BankCashTransaction>>()
    private val receiptAllocationsCache = UiDataCache<List<ReceiptAllocation>>()
    private val ledgerAccountsCache = UiDataCache<List<LedgerAccount>>()
    private val billsReceivableCache = UiDataCache<List<BillReceivable>>()
    private val expensesCache = UiDataCache<List<Expense>>()
    private val incomesCache = UiDataCache<List<Income>>()

    val isSetupCompleted = MutableStateFlow(false)
    val setupStatusResolved = MutableStateFlow(false)
    val financialYear = MutableStateFlow(FinancialYearUtils.currentFinancialYearCode())
    val voucherPrefillRequest = MutableStateFlow<VoucherPrefillRequest?>(null)

    init {
        val tempRepo = try {
            val db = AppDatabase.getDatabase(application)
            db.openHelper.writableDatabase
            dbInitState.value = DbInitState.Success
            AppRepository(db)
        } catch (e: Exception) {
            e.printStackTrace()
            dbInitState.value = DbInitState.Error(e.localizedMessage ?: "Failed to initialize SQLite Database.")
            AppRepository(AppDatabase.getDatabase(application))
        }
        repository = tempRepo

        profileUiState = cachedReadState(
            dataset = "profile",
            keyFlow = flowOf<String?>(null),
            source = { repository.profile },
            cache = profileCache,
            isEmpty = { it == null }
        )
        profile = profileUiState.asCompatibilityState(null)

        availableFinancialYearsUiState = cachedReadState(
            dataset = "financial-years",
            keyFlow = flowOf<String?>(null),
            source = { repository.observeAvailableFinancialYearCodes() },
            cache = financialYearsCache,
            isEmpty = { it.isEmpty() }
        )
        availableFinancialYears = availableFinancialYearsUiState.asCompatibilityState(
            FinancialYearUtils.buildGeneratedYears()
        )

        partiesUiState = cachedReadState(
            "parties", financialYear.asCacheKeyFlow(),
            { repository.observeParties(checkNotNull(it)) }, partiesCache
        ) { it.isEmpty() }
        parties = partiesUiState.asCompatibilityState(emptyList())
        productsUiState = cachedReadState(
            "products", financialYear.asCacheKeyFlow(),
            { repository.observeProducts(checkNotNull(it)) }, productsCache
        ) { it.isEmpty() }
        products = productsUiState.asCompatibilityState(emptyList())
        vouchersUiState = cachedReadState(
            "vouchers", financialYear.asCacheKeyFlow(),
            { repository.observeVouchers(checkNotNull(it)) }, vouchersCache
        ) { it.isEmpty() }
        vouchers = vouchersUiState.asCompatibilityState(emptyList())
        ledgerEntriesUiState = cachedReadState(
            "ledger-entries", financialYear.asCacheKeyFlow(),
            { repository.observeLedgerEntries(checkNotNull(it)) }, ledgerEntriesCache
        ) { it.isEmpty() }
        ledgerEntries = ledgerEntriesUiState.asCompatibilityState(emptyList())
        transactionsUiState = cachedReadState(
            "transactions", financialYear.asCacheKeyFlow(),
            { repository.observeTransactions(checkNotNull(it)) }, transactionsCache
        ) { it.isEmpty() }
        transactions = transactionsUiState.asCompatibilityState(emptyList())
        receiptAllocationsUiState = cachedReadState(
            "receipt-allocations", financialYear.asCacheKeyFlow(),
            { repository.observeReceiptAllocations(checkNotNull(it)) }, receiptAllocationsCache
        ) { it.isEmpty() }
        receiptAllocations = receiptAllocationsUiState.asCompatibilityState(emptyList())
        ledgerAccountsUiState = cachedReadState(
            "ledger-accounts", financialYear.asCacheKeyFlow(),
            { repository.observeLedgerAccounts(checkNotNull(it)) }, ledgerAccountsCache
        ) { it.isEmpty() }
        ledgerAccounts = ledgerAccountsUiState.asCompatibilityState(emptyList())
        billsReceivableUiState = cachedReadState(
            "bills-receivable", financialYear.asCacheKeyFlow(),
            { repository.observeBillsReceivable(checkNotNull(it)) }, billsReceivableCache
        ) { it.isEmpty() }
        billsReceivable = billsReceivableUiState.asCompatibilityState(emptyList())
        expensesUiState = cachedReadState(
            "expenses", financialYear.asCacheKeyFlow(),
            { repository.observeExpenses(checkNotNull(it)) }, expensesCache
        ) { it.isEmpty() }
        expenses = expensesUiState.asCompatibilityState(emptyList())
        incomesUiState = cachedReadState(
            "incomes", financialYear.asCacheKeyFlow(),
            { repository.observeIncomes(checkNotNull(it)) }, incomesCache
        ) { it.isEmpty() }
        incomes = incomesUiState.asCompatibilityState(emptyList())

        if (dbInitState.value is DbInitState.Success) {
            viewModelScope.launch {
                repository.seedLedgersIfEmpty()
                repository.backfillLedgerEntryPartyIds()
                repository.ensureFinancialYearExists(financialYear.value)
            }

            viewModelScope.launch {
                profileUiState.collect { state ->
                    if (state == UiLoadState.Loading ||
                        state is UiLoadState.Error && !state.hasCachedValue
                    ) {
                        return@collect
                    }
                    val prof = when (state) {
                        is UiLoadState.Content -> state.value
                        is UiLoadState.Empty -> state.value
                        is UiLoadState.Error -> state.cachedValue
                        UiLoadState.Loading -> null
                    }
                    setupStatusResolved.value = true
                    isSetupCompleted.value = prof != null
                    val resolvedFy = prof?.fyLabel?.takeIf { it.isNotBlank() }
                        ?: FinancialYearUtils.currentFinancialYearCode()
                    financialYear.value = resolvedFy
                    repository.ensureFinancialYearExists(resolvedFy)
                }
            }
        }
    }

    private fun <T> cachedReadState(
        dataset: String,
        keyFlow: Flow<String?>,
        source: (String?) -> Flow<T>,
        cache: UiDataCache<T>,
        isEmpty: (T) -> Boolean
    ): StateFlow<UiLoadState<T>> =
        combine(keyFlow, retryGeneration) { financialYearCode, _ -> financialYearCode }
            .flatMapLatest { financialYearCode ->
                val key = UiDataKey(dataset, financialYearCode)
                flow { emitAll(source(financialYearCode)) }
                    .map { value -> cache.update(key, value, isEmpty) }
                    .onStart { emit(cache.currentState(key, isEmpty)) }
                    .catch { cause -> emit(cache.error(key, cause)) }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiLoadState.Loading
            )

    private fun <T> StateFlow<UiLoadState<T>>.asCompatibilityState(
        initialValue: T
    ): StateFlow<T> =
        map { state ->
            when (state) {
                is UiLoadState.Content -> state.value
                is UiLoadState.Empty -> state.value
                is UiLoadState.Error ->
                    if (state.hasCachedValue) state.cachedValue ?: initialValue else initialValue
                UiLoadState.Loading -> initialValue
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = initialValue
        )

    fun retryFailedReads() {
        retryGeneration.value += 1
    }

    fun insertAllocation(allocation: ReceiptAllocation) {
        viewModelScope.launch {
            repository.insertAllocation(allocation.copy(financialYearCode = financialYear.value))
        }
    }

    fun insertLedgerAccount(account: LedgerAccount) {
        viewModelScope.launch {
            repository.insertLedgerAccount(account, financialYear.value)
        }
    }

    fun deleteLedgerAccount(id: String) {
        viewModelScope.launch {
            repository.deleteLedgerAccount(id)
        }
    }

    fun saveProfile(profile: BusinessProfile, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.insertProfile(profile.copy(fyLabel = financialYear.value))
            syncBusinessProfileTerms(profile.termsAndConditions)
            setupStatusResolved.value = true
            isSetupCompleted.value = true
            onSuccess()
        }
    }

    fun updateProfile(profile: BusinessProfile, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.insertProfile(profile)
            syncBusinessProfileTerms(profile.termsAndConditions)
            setupStatusResolved.value = true
            onSuccess()
        }
    }

    fun switchFinancialYear(financialYearCode: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.ensureFinancialYearExists(financialYearCode)
            financialYear.value = financialYearCode
            profile.value?.let { repository.insertProfile(it.copy(fyLabel = financialYearCode)) }
            onSuccess()
        }
    }

    fun saveParty(party: Party, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.insertParty(party, financialYear.value)
            onSuccess()
        }
    }

    fun updateParty(party: Party, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.updateParty(party, financialYear.value)
            onSuccess()
        }
    }

    fun scheduleDueReminder(voucherId: String, creditDueDate: String) {
        EmailReminderScheduler.scheduleDueReminder(
            context = getApplication(),
            voucherId = voucherId,
            partyEmail = "",
            creditDueDate = creditDueDate
        )
    }

    fun getPartyById(partyId: String): Party? = parties.value.find { it.id == partyId }

    fun deleteParty(partyId: String) {
        viewModelScope.launch {
            repository.deleteParty(partyId)
        }
    }

    fun saveProduct(product: Product, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.insertProduct(product, financialYear.value)
            onSuccess()
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }

    fun saveExpense(expense: Expense, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.insertExpense(expense.copy(fyLabel = financialYear.value))
            onSuccess()
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            repository.deleteExpense(expenseId)
        }
    }

    fun saveIncome(income: Income, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.insertIncome(income.copy(fyLabel = financialYear.value))
            onSuccess()
        }
    }

    fun deleteIncome(incomeId: String) {
        viewModelScope.launch {
            repository.deleteIncome(incomeId)
        }
    }

    suspend fun generateNextVoucherNo(type: String, timestamp: Long): String =
        repository.generateNextVoucherNo(type, timestamp)

    suspend fun getVoucherSaveExtras(voucherId: String): VoucherSaveExtras =
        repository.getVoucherSaveExtras(voucherId)

    suspend fun getJournalLinesForVoucher(voucherId: String) =
        repository.getJournalLinesForVoucher(voucherId)

    fun saveVoucher(
        voucher: Voucher,
        items: List<VoucherItem>,
        partyName: String?,
        extras: VoucherSaveExtras = VoucherSaveExtras(),
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.saveAndPostVoucher(
                voucher = voucher.copy(financialYearCode = financialYear.value),
                items = items.map { it.copy(financialYearCode = financialYear.value) },
                partyName = partyName,
                extras = extras
            )
            onSuccess()
        }
    }

    fun saveJournalVoucher(
        voucher: Voucher,
        lines: List<JournalLine>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.saveJournalVoucher(
                voucher = voucher.copy(financialYearCode = financialYear.value),
                lines = lines
            )
            onSuccess()
        }
    }

    fun closeFinancialYear(
        sourceFinancialYearCode: String,
        targetFinancialYearCode: String = FinancialYearUtils.nextFinancialYear(sourceFinancialYearCode),
        lockSourceYear: Boolean,
        onSuccess: (AppRepository.FinancialYearCloseResult) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            runCatching {
                repository.closeFinancialYear(
                    sourceFinancialYearCode = sourceFinancialYearCode,
                    targetFinancialYearCode = targetFinancialYearCode,
                    lockSourceYear = lockSourceYear
                )
            }.onSuccess(onSuccess).onFailure(onError)
        }
    }

    fun deleteVoucher(voucherId: String) {
        viewModelScope.launch {
            repository.deleteVoucher(voucherId)
        }
    }

    fun getVoucherById(voucherId: String): Voucher? = vouchers.value.find { it.id == voucherId }

    fun getItemsForVoucher(voucherId: String) = repository.getItemsForVoucher(voucherId)

    suspend fun getInvoiceRenderBundle(voucherId: String): InvoiceGenerator.InvoiceRenderBundle? =
        InvoiceGenerator.buildRenderBundleWithTypeAware(getApplication(), voucherId)

    suspend fun getSaleVoucherReferenceFields(voucherId: String): Pair<String, String> {
        val db = AppDatabase.getDatabase(getApplication())
        val cursor = db.openHelper.readableDatabase.query(
            """
            SELECT COALESCE(NULLIF(reference_no, ''), NULLIF(referenceNo, ''), ''),
                   COALESCE(NULLIF(other_references, ''), '')
            FROM vouchers
            WHERE id = ?
            """.trimIndent(),
            arrayOf(voucherId)
        )
        cursor.use {
            return if (it.moveToFirst()) {
                it.getString(0).orEmpty() to it.getString(1).orEmpty()
            } else {
                "" to ""
            }
        }
    }

    fun syncSaleVoucherReferenceFields(voucherId: String, referenceNo: String, otherReferences: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(getApplication())
            db.openHelper.writableDatabase.execSQL(
                """
                UPDATE vouchers
                SET reference_no = ?,
                    other_references = ?
                WHERE id = ?
                """.trimIndent(),
                arrayOf(referenceNo, otherReferences, voucherId)
            )
        }
    }

    private suspend fun syncBusinessProfileTerms(termsAndConditions: String) {
        val db = AppDatabase.getDatabase(getApplication())
        db.openHelper.writableDatabase.execSQL(
            "UPDATE business_profile SET terms_and_conditions = ? WHERE id = 1",
            arrayOf(termsAndConditions)
        )
    }

    fun saveTransaction(tx: BankCashTransaction, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.saveBankCashTransaction(tx.copy(financialYearCode = financialYear.value))
            onSuccess()
        }
    }

    fun deleteTransaction(txId: String) {
        viewModelScope.launch {
            repository.deleteTransaction(txId)
        }
    }

    fun setVoucherPrefillRequest(request: VoucherPrefillRequest?) {
        voucherPrefillRequest.value = request
    }

    fun loadSampleData() {
        viewModelScope.launch {
            repository.insertSampleData()
        }
    }

    fun backupDatabase(context: Context): ExportStorageManager.ExportResult? {
        return try {
            val dbFile = context.getDatabasePath("Mavo.db").let { if (it.exists()) it else context.getDatabasePath("ZeroBook.db") }
            if (dbFile.exists()) {
                ExportStorageManager.exportFile(
                    context = context,
                    sourceFile = dbFile,
                    displayName = "Mavo_Backup_${System.currentTimeMillis()}.db",
                    mimeType = "application/octet-stream",
                    target = ExportTarget.Backups
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun restoreDatabase(context: Context, backupFile: File): Boolean {
        return try {
            val dbFile = context.getDatabasePath("Mavo.db")
            if (dbFile.exists()) dbFile.delete()
            val oldDbFile = context.getDatabasePath("ZeroBook.db")
            if (oldDbFile.exists()) oldDbFile.delete()
            FileInputStream(backupFile).use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun auditLogsForYear(financialYearCode: String): Flow<List<FinancialYearAuditLog>> =
        AppDatabase.getDatabase(getApplication()).financialYearAuditLogDao().getLogsForYear(financialYearCode)

    fun fetchPinCodeDetails(pincode: String, onResult: (city: String?, state: String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = java.net.URL("https://api.postalpincode.in/pincode/$pincode")
                val connection = (url.openConnection() as java.net.HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 5000
                    readTimeout = 5000
                }
                val responseText = try {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } finally {
                    connection.disconnect()
                }
                val districtRegex = """"District"\s*:\s*"([^"]+)"""".toRegex()
                val stateRegex = """"State"\s*:\s*"([^"]+)"""".toRegex()

                val district = districtRegex.find(responseText)?.groupValues?.get(1)
                val state = stateRegex.find(responseText)?.groupValues?.get(1)

                withContext(Dispatchers.Main) {
                    onResult(district, state)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(null, null)
                }
            }
        }
    }

    fun fetchIfscDetails(ifsc: String, onResult: (bankName: String?, branchName: String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = java.net.URL("https://ifsc.razorpay.com/$ifsc")
                val connection = (url.openConnection() as java.net.HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 5000
                    readTimeout = 5000
                }
                val responseText = try {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } finally {
                    connection.disconnect()
                }
                val bankRegex = """"BANK"\s*:\s*"([^"]+)"""".toRegex()
                val branchRegex = """"BRANCH"\s*:\s*"([^"]+)"""".toRegex()
                val bank = bankRegex.find(responseText)?.groupValues?.get(1)
                val branch = branchRegex.find(responseText)?.groupValues?.get(1)

                withContext(Dispatchers.Main) {
                    onResult(bank, branch)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(null, null)
                }
            }
        }
    }

    fun fetchGstinDetails(gstin: String, onResult: (tradeName: String?, legalName: String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = java.net.URL("https://api.copreco.com/gstin/$gstin")
                val connection = (url.openConnection() as java.net.HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 5000
                    readTimeout = 5000
                }
                val responseText = try {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } finally {
                    connection.disconnect()
                }
                val tradeNameRegex = """"tradeNam"\s*:\s*"([^"]+)"""".toRegex()
                val legalNameRegex = """"lgnm"\s*:\s*"([^"]+)"""".toRegex()
                val legalNameAltRegex = """"legalName"\s*:\s*"([^"]+)"""".toRegex()

                val trade = tradeNameRegex.find(responseText)?.groupValues?.get(1)
                val legal = legalNameRegex.find(responseText)?.groupValues?.get(1)
                    ?: legalNameAltRegex.find(responseText)?.groupValues?.get(1)

                withContext(Dispatchers.Main) {
                    onResult(trade, legal)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(null, null)
                }
            }
        }
    }

    suspend fun autoAdvanceFinancialYearIfNeeded(context: Context): String? {
        val today = LocalDate.now()
        val lastCheckedDate = AppPreferences.getFyLastCheckedDate(context)
        if (lastCheckedDate == today) return null

        val activeProfile = profile.value ?: repository.getProfileSync()
        val currentFyCode = activeProfile?.fyLabel?.ifBlank { FinancialYearUtils.currentFinancialYearCode() }
            ?: FinancialYearUtils.currentFinancialYearCode()
        val fyEndDate = FinancialYearUtils.endDateFor(currentFyCode)
        val updatedLabel = if (today.isAfter(fyEndDate)) {
            val newFyCode = FinancialYearUtils.financialYearCodeFor(today)
            if (newFyCode != currentFyCode) {
                repository.ensureFinancialYearExists(newFyCode)
                financialYear.value = newFyCode
                activeProfile?.let { repository.insertProfile(it.copy(fyLabel = newFyCode)) }
                newFyCode
            } else {
                null
            }
        } else {
            null
        }

        AppPreferences.setFyLastCheckedDate(context, today)
        return updatedLabel
    }
}

private fun MutableStateFlow<String>.asCacheKeyFlow(): Flow<String?> = map { it }
