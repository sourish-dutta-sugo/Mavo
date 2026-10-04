package com.mavo.app.ui.screens

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.theme.AppColors
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * First-run flow: onboarding -> terms -> permission -> three setup steps.
 * Lives outside the NavHost (same gate the old SetupScreen used), so back only
 * walks these steps and never touches the rest of the app.
 */
@Composable
fun FirstRunFlow(viewModel: AppViewModel) {
    var stepIndex by rememberSaveable { mutableStateOf(0) }
    var draft by rememberSaveable(stateSaver = DraftSaver) { mutableStateOf(SetupDraft()) }
    var termsAccepted by rememberSaveable { mutableStateOf(false) }
    val step = SetupStep.entries[stepIndex]

    fun goTo(index: Int) {
        stepIndex = index.coerceIn(0, SetupStep.entries.lastIndex)
    }

    // ponytail: system back only steps through the wizard; exiting at step 1 uses default behaviour.
    BackHandler(enabled = stepIndex > 0) { goTo(stepIndex - 1) }

    when (step) {
        SetupStep.Onboarding1 -> WizardScaffold(
            title = "What is Mavo?",
            subtitle = "Mavo is a GST-ready accounting app built for Indian shops, wholesalers and manufacturers.",
            onNext = { goTo(stepIndex + 1) },
            nextLabel = "Next"
        ) {
            WizardBullet("One place for your books", "Bills, parties, stock and payments recorded as you work.")
            WizardBullet("Built for Indian business", "GST invoices, HSN codes and state codes handled for you.")
            WizardBullet("Nothing leaves your phone", "Your data is stored on this device and works offline.")
        }

        SetupStep.Onboarding2 -> WizardScaffold(
            title = "What Mavo does",
            subtitle = "Everything you need to run the numbers at your shop.",
            onNext = { goTo(stepIndex + 1) },
            nextLabel = "Next"
        ) {
            WizardBullet("Bill in seconds", "Raise GST invoices, quotations and vouchers without the paperwork.")
            WizardBullet("Know who owes whom", "Party ledgers track outstanding balances automatically.")
            WizardBullet("See how the business is doing", "Reports, summaries and trends, ready when you are.")
        }

        SetupStep.Onboarding3 -> WizardScaffold(
            title = "A quick note",
            subtitle = "Two things worth knowing before you start.",
            onNext = { goTo(stepIndex + 1) },
            nextLabel = "Got it"
        ) {
            WizardBullet("Back up regularly", "Your books live on this device. Export a backup from Settings from time to time.")
            WizardBullet("Mavo is your tool, not your accountant", "It records and reports what you enter — filing returns stays your responsibility.")
        }

        SetupStep.Terms -> WizardScaffold(
            title = "Terms & Privacy",
            subtitle = "Please read and accept both to continue.",
            onBack = { goTo(stepIndex - 1) },
            onNext = { goTo(stepIndex + 1) },
            nextLabel = "Agree & Continue",
            nextEnabled = termsAccepted,
            hint = if (termsAccepted) null else "Tick the box to continue."
        ) {
            TermsBody()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.cardBg, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    colors = CheckboxDefaults.colors(checkedColor = AppColors.primary)
                )
                Text(
                    text = "I have read and agree to the Terms & Conditions and the Privacy Policy.",
                    fontSize = 13.sp,
                    color = AppColors.textPrimary
                )
            }
        }

        SetupStep.Permission -> PermissionStep(
            onBack = { goTo(stepIndex - 1) },
            onDetected = { detected ->
                draft = draft.copy(state = detected.first, stateCode = detected.second)
            },
            onNext = { goTo(stepIndex + 1) }
        )

        SetupStep.Basic -> SetupBasicScreen(
            draft = draft,
            updateDraft = { transform -> draft = transform(draft) },
            onBack = { goTo(stepIndex - 1) },
            onNext = { goTo(stepIndex + 1) }
        )

        SetupStep.Location -> SetupLocationScreen(
            draft = draft,
            updateDraft = { transform -> draft = transform(draft) },
            onBack = { goTo(stepIndex - 1) },
            onNext = { goTo(stepIndex + 1) }
        )

        SetupStep.TaxBank -> SetupTaxBankScreen(
            viewModel = viewModel,
            draft = draft,
            updateDraft = { transform -> draft = transform(draft) },
            onBack = { goTo(stepIndex - 1) },
            onFinish = { loadSampleData ->
                viewModel.saveProfile(draft.toProfile()) {
                    if (loadSampleData) viewModel.loadSampleData()
                }
            }
        )
    }
}

@Composable
private fun WizardBullet(title: String, body: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(AppColors.primaryLight, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\u2022",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.primary
            )
        }
        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.textPrimary
            )
            Text(
                text = body,
                fontSize = 13.sp,
                color = AppColors.textSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun TermsBody() {
    Text(text = "Terms & Conditions", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppColors.primary)
    Text(
        text = "1. Mavo is a tool for recording your own business transactions. You stay responsible for the accuracy of what you enter.\n" +
            "2. Invoices, ledgers and reports produced by Mavo reflect the data you provide.\n" +
            "3. Features may change as the app is updated; your data carries across updates.\n" +
            "4. The app is provided as-is, without warranty of uninterrupted availability.",
        fontSize = 13.sp,
        color = AppColors.textSecondary,
        lineHeight = 19.sp
    )
    Text(text = "Privacy Policy", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppColors.primary)
    Text(
        text = "1. Your business data is stored on this device. Mavo keeps no copy on a server.\n" +
            "2. Location is requested only to pre-fill your GST state code. It is optional and can be denied.\n" +
            "3. PIN code, GSTIN and IFSC lookups send only the value you typed, over HTTPS.\n" +
            "4. Mavo contains no advertising or tracking SDKs.",
        fontSize = 13.sp,
        color = AppColors.textSecondary,
        lineHeight = 19.sp
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionStep(
    onBack: () -> Unit,
    onDetected: (Pair<String, String>) -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    var requested by rememberSaveable { mutableStateOf(false) }
    val granted = permissionsState.allPermissionsGranted

    LaunchedEffect(granted) {
        if (requested && granted) {
            detectGstStateInfoFromLocation(context)?.let(onDetected)
            onNext()
        }
    }

    WizardScaffold(
        title = "Location access",
        subtitle = "Mavo can read your location once to pick the GST state code printed on your invoices. You can always set it manually instead — nothing else uses your location.",
        onBack = onBack,
        onNext = {
            if (granted) {
                onNext()
            } else {
                requested = true
                permissionsState.launchMultiplePermissionRequest()
            }
        },
        nextLabel = if (granted) "Continue" else "Allow location access",
        secondary = {
            TextButton(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
                Text("Skip for now", color = AppColors.primary, fontWeight = FontWeight.SemiBold)
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.cardBg, RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = AppColors.primary,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = "Optional, and only for the state code",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textPrimary
                )
                Text(
                    text = "Deny it and you will pick your state by hand on the next screen.",
                    fontSize = 12.sp,
                    color = AppColors.textSecondary,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
