package com.mavo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mavo.app.ui.theme.CafePalette

@Composable
fun GraphPaperBackground(modifier: Modifier = Modifier) {
    // 60% Warm, eye-friendly cafe base
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CafePalette.creamBase)
    )
}
