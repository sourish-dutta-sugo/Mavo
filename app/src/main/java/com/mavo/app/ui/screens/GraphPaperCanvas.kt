package com.mavo.app.ui.screens

import androidx.compose.ui.Modifier

// Checkered background removed/blended: returns plain Modifier so grid never draws over cards or content.
internal fun Modifier.graphPaperCanvas(): Modifier = this
