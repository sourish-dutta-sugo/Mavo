package com.mavo.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.R
import com.mavo.app.data.Utils
import com.mavo.app.ui.components.CircleIconButton
import com.mavo.app.ui.components.StepProgress
import com.mavo.app.ui.components.WizardTitle
import com.mavo.app.ui.components.ZbField
import com.mavo.app.ui.theme.AppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

private val TextDark = Color(0xFF1A1A1A)
private val MenuWhite = Color(0xFFFFFFFF)

data class PinLookupResult(
    val city: String,
    val state: String,
    val stateCode: String
)

suspend fun fetchPinLookup(pinCode: String): PinLookupResult? = withContext(Dispatchers.IO) {
    runCatching {
        val response = java.net.URL("https://api.postalpincode.in/pincode/$pinCode").readText()
        val root = JSONArray(response).optJSONObject(0) ?: return@runCatching null
        if (!root.optString("Status").equals("Success", ignoreCase = true)) return@runCatching null
        val postOffice = root.optJSONArray("PostOffice")?.optJSONObject(0) ?: return@runCatching null
        val city = postOffice.optString("District").trim()
        val state = postOffice.optString("State").trim()
        if (city.isBlank() || state.isBlank()) return@runCatching null
        val stateCode = resolveIndianStateInfo(state)?.second.orEmpty()
        PinLookupResult(city = city, state = state, stateCode = stateCode)
    }.getOrNull()
}

@Composable
fun StateDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onStateSelected: (Pair<String, String>) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .fillMaxWidth(0.92f)
            .height(280.dp)
            .background(MenuWhite)
    ) {
        Utils.INDIAN_STATES.forEach { statePair ->
            DropdownMenuItem(
                text = {
                    Text(
                        "${statePair.first} (Code ${statePair.second})",
                        color = TextDark
                    )
                },
                onClick = { onStateSelected(statePair) },
                colors = MenuDefaults.itemColors(
                    textColor = TextDark,
                    leadingIconColor = TextDark,
                    trailingIconColor = TextDark
                )
            )
        }
    }
}

@Composable
fun RetailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    fieldModifier: Modifier = Modifier
) = ZbField(
    value = value,
    onValueChange = onValueChange,
    label = label,
    modifier = modifier,
    placeholder = placeholder,
    readOnly = readOnly,
    singleLine = singleLine,
    trailingIcon = trailingIcon,
    keyboardOptions = keyboardOptions,
    keyboardActions = keyboardActions,
    isError = isError,
    supportingText = supportingText,
    fieldModifier = fieldModifier
)

/**
 * Shared chrome for the first-run wizard: header, scrollable body, pinned footer button.
 * Used by every onboarding / terms / permission / setup step so back-stack, spacing and
 * the primary action read the same on all of them.
 */
@Composable
fun WizardScaffold(
    title: String,
    onNext: () -> Unit,
    nextLabel: String = "Continue",
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    stepLabel: String? = null,
    stepProgress: Int? = null,
    onBack: (() -> Unit)? = null,
    nextEnabled: Boolean = true,
    hint: String? = null,
    secondary: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.screenBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (onBack != null || stepLabel != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    CircleIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = onBack
                    )
                } else {
                    Spacer(modifier = Modifier.width(44.dp))
                }
                if (stepLabel != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stepLabel,
                        fontSize = 15.sp,
                        color = AppColors.textSecondary
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            if (stepProgress != null) {
                StepProgress(step = stepProgress)
                Spacer(modifier = Modifier.height(24.dp))
            }
            WizardTitle(text = title, subtitle = subtitle)
            content()
            Spacer(modifier = Modifier.height(24.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (hint != null) {
                Text(
                    text = hint,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.textSecondary
                )
            }
            secondary?.invoke()
            Button(
                onClick = onNext,
                enabled = nextEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
            ) {
                Text(
                    text = nextLabel,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textOnPrimary
                )
            }
        }
    }
}
