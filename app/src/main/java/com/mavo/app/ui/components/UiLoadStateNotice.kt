package com.mavo.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mavo.app.ui.UiLoadState

fun UiLoadState<*>.blocksContent(): Boolean = when (this) {
    UiLoadState.Loading -> true
    is UiLoadState.Error -> !hasCachedValue
    else -> false
}

fun <T> UiLoadState<List<T>>.valueOrEmpty(): List<T> = when (this) {
    is UiLoadState.Content -> value
    is UiLoadState.Empty -> value
    is UiLoadState.Error -> cachedValue.orEmpty()
    UiLoadState.Loading -> emptyList()
}

@Composable
fun UiLoadStateNotice(
    state: UiLoadState<*>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    loadingLabel: String = "Loading records..."
) {
    val error = state as? UiLoadState.Error<*>
    val loading = state == UiLoadState.Loading
    if (!loading && error == null) return
    val cached = error?.hasCachedValue == true
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                loading -> MaterialTheme.colorScheme.surfaceContainerLow
                cached -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
            Text(
                modifier = Modifier.weight(1f),
                text = when {
                    loading -> loadingLabel
                    cached -> "Showing saved data. Some records could not refresh."
                    else -> error?.cause?.localizedMessage ?: "Some records are unavailable."
                },
                color = when {
                    loading -> MaterialTheme.colorScheme.onSurface
                    cached -> MaterialTheme.colorScheme.onSecondaryContainer
                    else -> MaterialTheme.colorScheme.onErrorContainer
                },
                style = MaterialTheme.typography.bodyMedium
            )
            if (!loading) {
                TextButton(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}
