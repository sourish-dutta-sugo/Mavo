package com.mavo.app.ui.screens

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.components.PrimaryButton
import com.mavo.app.ui.components.SecondaryButton
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
    val step = SetupStep.entries[stepIndex]

    fun goTo(index: Int) {
        stepIndex = index.coerceIn(0, SetupStep.entries.lastIndex)
    }

    // ponytail: system back only steps through the wizard; exiting at step 1 uses default behaviour.
    BackHandler(enabled = stepIndex > 0) { goTo(stepIndex - 1) }

    when (step) {
        SetupStep.Onboarding1 -> OnboardingPage(
            icon = Icons.AutoMirrored.Outlined.MenuBook,
            title = "Your business, in a single ledger.",
            body = "Mavo is a fast, offline-first accounting companion built for shopkeepers, traders and small businesses in India.",
            pageIndex = 0,
            onNext = { goTo(stepIndex + 1) }
        )

        SetupStep.Onboarding2 -> OnboardingPage(
            icon = Icons.Outlined.ReceiptLong,
            title = "Post vouchers. Print invoices. Done.",
            body = "Sales, purchase, receipts, payments and journals \u2014 all posted to the correct ledger automatically with GST, PDF export and party ledgers.",
            pageIndex = 1,
            onNext = { goTo(stepIndex + 1) }
        )

        SetupStep.Terms -> TermsPage(
            onNext = { goTo(stepIndex + 1) }
        )

        SetupStep.Permission -> PermissionStep(
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

private const val ONBOARDING_PAGE_COUNT = 3

@Composable
private fun OnboardingPage(
    icon: ImageVector,
    title: String,
    body: String,
    pageIndex: Int,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.screenBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(72.dp))
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(RoundedCornerShape(34.dp))
                .background(AppColors.cardBg)
                .border(1.dp, AppColors.border, RoundedCornerShape(34.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.textPrimary,
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(modifier = Modifier.height(56.dp))
        Text(
            text = title,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.textPrimary,
            lineHeight = 38.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = body,
            fontSize = 17.sp,
            color = AppColors.textSecondary,
            lineHeight = 25.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OnboardingDots(activeIndex = pageIndex, count = ONBOARDING_PAGE_COUNT)
            Spacer(modifier = Modifier.weight(1f))
            PillButton(label = "Next", showChevron = true, onClick = onNext)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun TermsPage(onNext: () -> Unit) {
    var accepted by rememberSaveable { mutableStateOf(false) }
    var showDocumentText by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.screenBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(RoundedCornerShape(34.dp))
                .background(AppColors.cardBg)
                .border(1.dp, AppColors.border, RoundedCornerShape(34.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = AppColors.textPrimary,
                modifier = Modifier.size(64.dp)
            )
        }
        Spacer(modifier = Modifier.height(44.dp))
        Text(
            text = "A quick note before you begin.",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.textPrimary,
            lineHeight = 38.sp
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Please review our terms and privacy practices. Your data stays on your device unless you choose to back it up.",
            fontSize = 17.sp,
            color = AppColors.textSecondary,
            lineHeight = 25.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        DocumentRow(
            label = "Read Terms & Conditions",
            onClick = { showDocumentText = !showDocumentText }
        )
        Spacer(modifier = Modifier.height(12.dp))
        DocumentRow(
            label = "Read Privacy Policy",
            onClick = { showDocumentText = !showDocumentText }
        )
        if (showDocumentText) {
            Spacer(modifier = Modifier.height(12.dp))
            TermsBody()
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { accepted = !accepted },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = accepted,
                onCheckedChange = { accepted = it },
                colors = CheckboxDefaults.colors(checkedColor = AppColors.primary)
            )
            Text(
                text = "I have read and agree to the Terms & Conditions and Privacy Policy.",
                fontSize = 14.sp,
                color = AppColors.textPrimary,
                lineHeight = 20.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OnboardingDots(activeIndex = 2, count = ONBOARDING_PAGE_COUNT)
            Spacer(modifier = Modifier.weight(1f))
            PillButton(
                label = "Get Started",
                showChevron = false,
                enabled = accepted,
                onClick = { if (accepted) onNext() }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun DocumentRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.cardBg)
            .border(1.dp, AppColors.border, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = AppColors.textTertiary
        )
    }
}

@Composable
private fun OnboardingDots(activeIndex: Int, count: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) { index ->
            if (index == activeIndex) {
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height(8.dp)
                        .background(AppColors.primary, RoundedCornerShape(4.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(AppColors.border, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun PillButton(
    label: String,
    showChevron: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(28.dp))
            .background(if (enabled) AppColors.primary else AppColors.primary.copy(alpha = 0.35f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 26.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.textOnPrimary
        )
        if (showChevron) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = AppColors.textOnPrimary,
                modifier = Modifier.size(18.dp)
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.screenBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(72.dp))
        Box(
            modifier = Modifier
                .size(130.dp)
                .background(AppColors.cardBg, CircleShape)
                .border(1.dp, AppColors.border, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = AppColors.textPrimary,
                modifier = Modifier.size(52.dp)
            )
        }
        Spacer(modifier = Modifier.height(56.dp))
        Text(
            text = "Allow location access",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.textPrimary,
            lineHeight = 38.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "We use your location once to auto-fill your pin code, city and state during setup. Nothing is uploaded \u2014 you can also enter these manually.",
            fontSize = 17.sp,
            color = AppColors.textSecondary,
            lineHeight = 25.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            label = if (granted) "Continue" else "Allow location",
            onClick = {
                if (granted) {
                    onNext()
                } else {
                    requested = true
                    permissionsState.launchMultiplePermissionRequest()
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        SecondaryButton(label = "Enter manually", onClick = onNext)
        Spacer(modifier = Modifier.height(20.dp))
    }
}
