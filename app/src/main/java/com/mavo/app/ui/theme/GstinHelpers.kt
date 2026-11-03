package com.mavo.app.ui.theme
import com.mavo.app.ui.theme.AppColors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.mavo.app.data.Utils

val STATE_CODE_TO_NAME = Utils.STATE_NAME_BY_CODE

private val GSTIN_REGEX = Regex(
    "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$"
)

data class GstinParseResult(
    val gstin: String,
    val valid: Boolean?,
    val stateCode: String,
    val pan: String,
    val stateName: String?
)

fun parseGstinInput(
    value: String,
    currentPan: String,
    currentState: String,
    currentStateCode: String
): GstinParseResult {
    val v = value.uppercase().filter { it.isLetterOrDigit() }.take(15)
    if (v.length != 15) {
        return GstinParseResult(v, null, currentStateCode, currentPan, null)
    }
    val valid = GSTIN_REGEX.matches(v)
    if (!valid) {
        return GstinParseResult(v, false, currentStateCode, currentPan, null)
    }
    val code = v.substring(0, 2)
    val extractedPan = v.substring(2, 12)
    val stateName = STATE_CODE_TO_NAME[code]
    return GstinParseResult(
        gstin = v,
        valid = true,
        stateCode = code,
        pan = extractedPan,
        stateName = stateName
    )
}

@Composable
fun GstinValidationFeedback(
    gstin: String,
    gstinValid: Boolean?,
    pan: String,
    state: String,
    stateCode: String,
    modifier: Modifier = Modifier
) {
    when {
        gstin.length == 15 && gstinValid == true -> {
            Column(modifier = modifier.padding(top = 4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .background(AppColors.successBg, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Valid",
                        tint = AppColors.success,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        "Valid GSTIN",
                        color = AppColors.success,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (pan.isNotBlank()) {
                    Text(
                        "PAN: $pan (auto-extracted)",
                        color = AppColors.textSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (state.isNotBlank()) {
                    Text(
                        "State: $state (Code: $stateCode)",
                        color = AppColors.textSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
        gstin.length == 15 && gstinValid == false -> {
            Column(modifier = modifier.padding(top = 4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .background(AppColors.errorBg, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Invalid",
                        tint = AppColors.error,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        "Invalid GSTIN format",
                        color = AppColors.error,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    "Check: 2 digits + 5 letters + 4 digits + letter + digit/letter + Z + digit/letter",
                    color = AppColors.textTertiary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
