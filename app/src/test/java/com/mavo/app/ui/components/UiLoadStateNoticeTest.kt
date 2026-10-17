package com.mavo.app.ui.components

import com.mavo.app.ui.UiLoadState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UiLoadStateNoticeTest {
    @Test
    fun onlyColdLoadingAndColdErrorsBlockContent() {
        assertTrue(UiLoadState.Loading.blocksContent())
        assertTrue(
            UiLoadState.Error(
                cause = IllegalStateException("read failed"),
                cachedValue = null,
                hasCachedValue = false
            ).blocksContent()
        )
        assertFalse(UiLoadState.Content(listOf("cached")).blocksContent())
        assertFalse(
            UiLoadState.Error(
                cause = IllegalStateException("refresh failed"),
                cachedValue = listOf("cached"),
                hasCachedValue = true
            ).blocksContent()
        )
    }
}
