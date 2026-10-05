package com.mavo.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.data.HsnLookup
import com.mavo.app.data.HsnResult
import com.mavo.app.ui.components.CircleIconButton
import com.mavo.app.ui.components.PrimaryButton
import com.mavo.app.ui.components.ScreenBorder
import com.mavo.app.ui.components.SearchField
import com.mavo.app.ui.components.ZbCard
import com.mavo.app.ui.theme.AppColors

// ponytail: no per-row GST % — HsnLookup has no tax data; upgrade path: add taxRate to HsnResult.
@Composable
fun HsnSearchDialog(
    onDismiss: () -> Unit,
    onSelect: (HsnResult) -> Unit,
    initialQuery: String = ""
) {
    var query by remember { mutableStateOf(initialQuery.trim()) }
    val results = remember(query) { HsnLookup.search(query) }
    var selected by remember(results) { mutableStateOf(results.firstOrNull()) }
    val selectedResult = selected

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        containerColor = AppColors.cardBg,
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Tag,
                        contentDescription = null,
                        tint = AppColors.textSecondary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Search HSN / SAC",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.textPrimary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    CircleIconButton(
                        icon = Icons.Default.Close,
                        contentDescription = "Close",
                        onClick = onDismiss,
                        size = 36
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                SearchField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "Search by keyword or code"
                )
                Spacer(modifier = Modifier.height(14.dp))
                ZbCard(contentPadding = 0) {
                    if (results.isEmpty()) {
                        Text(
                            text = if (query.isBlank()) "Type a keyword to search HSN codes." else "No HSN results found.",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp,
                            color = AppColors.textSecondary
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            results.forEachIndexed { index, result ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selected = result }
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = result.hsnCode,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppColors.textPrimary
                                        )
                                        Text(
                                            text = result.description,
                                            fontSize = 13.sp,
                                            color = AppColors.textSecondary
                                        )
                                    }
                                    if (result == selectedResult) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = AppColors.primary
                                        )
                                    }
                                }
                                if (index < results.lastIndex) {
                                    HorizontalDivider(color = ScreenBorder)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                PrimaryButton(
                    label = selectedResult?.let { "Use ${it.hsnCode} · ${it.description}" } ?: "Select a code",
                    onClick = { selectedResult?.let(onSelect) },
                    enabled = selectedResult != null
                )
            }
        }
    )
}
