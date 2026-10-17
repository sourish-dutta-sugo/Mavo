package com.mavo.app.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UiDataCacheTest {
    private val isEmpty: (List<String>) -> Boolean = List<String>::isEmpty

    @Test
    fun returnsLoadingBeforeFirstSnapshotThenReplaysContent() {
        val cache = UiDataCache<List<String>>()
        val key = UiDataKey(dataset = "parties", financialYearCode = "2025-26")

        assertEquals(UiLoadState.Loading, cache.currentState(key, isEmpty))
        assertEquals(
            UiLoadState.Content(listOf("Retailer")),
            cache.update(key, listOf("Retailer"), isEmpty)
        )
        assertEquals(
            UiLoadState.Content(listOf("Retailer")),
            cache.currentState(key, isEmpty)
        )
    }

    @Test
    fun replacesCachedSnapshotOnEveryRoomValue() {
        val cache = UiDataCache<List<String>>()
        val key = UiDataKey(dataset = "products", financialYearCode = "2025-26")

        cache.update(key, listOf("Old"), isEmpty)
        cache.update(key, listOf("New"), isEmpty)

        assertEquals(
            UiLoadState.Content(listOf("New")),
            cache.currentState(key, isEmpty)
        )
    }

    @Test
    fun keepsFinancialYearSnapshotsIsolated() {
        val cache = UiDataCache<List<String>>()
        val currentYear = UiDataKey("vouchers", "2025-26")
        val priorYear = UiDataKey("vouchers", "2024-25")

        cache.update(currentYear, listOf("Current"), isEmpty)

        assertEquals(UiLoadState.Loading, cache.currentState(priorYear, isEmpty))
        cache.update(priorYear, listOf("Prior"), isEmpty)
        assertEquals(
            UiLoadState.Content(listOf("Current")),
            cache.currentState(currentYear, isEmpty)
        )
        assertEquals(
            UiLoadState.Content(listOf("Prior")),
            cache.currentState(priorYear, isEmpty)
        )
    }

    @Test
    fun distinguishesConfirmedEmptyFromLoading() {
        val cache = UiDataCache<List<String>>()
        val key = UiDataKey("expenses", "2025-26")

        assertEquals(UiLoadState.Loading, cache.currentState(key, isEmpty))
        assertEquals(UiLoadState.Empty(emptyList<String>()), cache.update(key, emptyList(), isEmpty))
    }

    @Test
    fun cachesNullProfileAsConfirmedEmpty() {
        val cache = UiDataCache<String?>()
        val key = UiDataKey(dataset = "profile", financialYearCode = null)
        val isEmpty: (String?) -> Boolean = { it == null }

        assertEquals(UiLoadState.Loading, cache.currentState(key, isEmpty))
        assertEquals(UiLoadState.Empty<String?>(null), cache.update(key, null, isEmpty))
        val error = cache.error(key, IllegalStateException("Read failed"))
        assertTrue(error.hasCachedValue)
        assertEquals(null, error.cachedValue)
    }

    @Test
    fun preservesCachedContentWhenRoomObservationFails() {
        val cache = UiDataCache<List<String>>()
        val key = UiDataKey("ledger", "2025-26")
        val cached = listOf("Opening balance")
        val failure = IllegalStateException("Read failed")

        cache.update(key, cached, isEmpty)
        val error = cache.error(key, failure)

        assertEquals(failure, error.cause)
        assertEquals(cached, error.cachedValue)
        assertTrue(error.hasCachedValue)
    }

    @Test
    fun exposesColdReadFailureAndAcceptsRetryValue() {
        val cache = UiDataCache<List<String>>()
        val key = UiDataKey("incomes", "2025-26")
        val failure = IllegalStateException("Read failed")

        val error = cache.error(key, failure)
        assertEquals(failure, error.cause)
        assertFalse(error.hasCachedValue)

        cache.update(key, listOf("Retry succeeded"), isEmpty)
        assertEquals(
            UiLoadState.Content(listOf("Retry succeeded")),
            cache.currentState(key, isEmpty)
        )
    }
}
