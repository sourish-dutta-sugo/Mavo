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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.R
import com.mavo.app.data.Utils
import com.mavo.app.ui.theme.AppColors
import com.mavo.app.ui.theme.mavoInputColors
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
    supportingText: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.labelText
            )
        },
        placeholder = placeholder?.let {
            { Text(text = it, fontSize = 14.sp, color = AppColors.inputPlaceholder) }
        },
        modifier = modifier.fillMaxWidth(),
        readOnly = readOnly,
        singleLine = singleLine,
        textStyle = TextStyle(color = AppColors.inputText, fontSize = 14.sp),
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(12.dp),
        isError = isError,
        supportingText = supportingText,
        colors = mavoInputColors()
    )
}

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AppColors.textPrimary
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(16.dp))
            }
            Image(
                painter = painterResource(R.drawable.logo_transparent),
                contentDescription = "Mavo",
                modifier = Modifier.size(30.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Mavo",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = AppColors.textPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            if (stepLabel != null) {
                Text(
                    text = stepLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textTertiary
                )
                Spacer(modifier = Modifier.width(12.dp))
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
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.textPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = AppColors.textSecondary,
                    lineHeight = 20.sp
                )
            }
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
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
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
