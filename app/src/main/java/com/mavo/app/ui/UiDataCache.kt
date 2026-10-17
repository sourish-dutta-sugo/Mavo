package com.mavo.app.ui

data class UiDataKey(
    val dataset: String,
    val financialYearCode: String?
)

sealed interface UiLoadState<out T> {
    data object Loading : UiLoadState<Nothing>
    data class Content<T>(val value: T) : UiLoadState<T>
    data class Empty<T>(val value: T) : UiLoadState<T>
    data class Error<T>(
        val cause: Throwable,
        val cachedValue: T?,
        val hasCachedValue: Boolean
    ) : UiLoadState<T>
}

class UiDataCache<T> {
    private data class Snapshot<T>(val value: T)

    private val lock = Any()
    private val snapshots = mutableMapOf<UiDataKey, Snapshot<T>>()

    fun currentState(
        key: UiDataKey,
        isEmpty: (T) -> Boolean
    ): UiLoadState<T> = synchronized(lock) {
        val snapshot = snapshots[key] ?: return@synchronized UiLoadState.Loading
        if (isEmpty(snapshot.value)) UiLoadState.Empty(snapshot.value)
        else UiLoadState.Content(snapshot.value)
    }

    fun update(
        key: UiDataKey,
        value: T,
        isEmpty: (T) -> Boolean
    ): UiLoadState<T> = synchronized(lock) {
        snapshots[key] = Snapshot(value)
        if (isEmpty(value)) UiLoadState.Empty(value) else UiLoadState.Content(value)
    }

    fun error(key: UiDataKey, cause: Throwable): UiLoadState.Error<T> = synchronized(lock) {
        val snapshot = snapshots[key]
        UiLoadState.Error(
            cause = cause,
            cachedValue = snapshot?.value,
            hasCachedValue = snapshot != null
        )
    }
}
