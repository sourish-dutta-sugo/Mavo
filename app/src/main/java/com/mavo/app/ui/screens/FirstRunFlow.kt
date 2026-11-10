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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.delay

/**
 * First-run flow: onboarding quick notes -> basic setup -> location -> tax/bank.
 */
@Composable
fun FirstRunFlow(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    var stepIndex by rememberSaveable { mutableStateOf(0) }
    var draft by rememberSaveable(stateSaver = DraftSaver) { mutableStateOf(SetupDraft()) }

    fun goTo(index: Int) {
        stepIndex = index.coerceIn(0, 3)
    }

    BackHandler(enabled = stepIndex > 0) { goTo(stepIndex - 1) }

    when (stepIndex) {
        0 -> OnboardingNotesScreen(
            onContinue = { goTo(1) },
            modifier = modifier
        )

        1 -> SetupBasicScreen(
            draft = draft,
            updateDraft = { transform -> draft = transform(draft) },
            onBack = { goTo(0) },
            onNext = { goTo(2) }
        )

        2 -> SetupLocationScreen(
            draft = draft,
            updateDraft = { transform -> draft = transform(draft) },
            onBack = { goTo(1) },
            onNext = { goTo(3) }
        )

        3 -> SetupTaxBankScreen(
            viewModel = viewModel,
            draft = draft,
            updateDraft = { transform -> draft = transform(draft) },
            onBack = { goTo(2) },
            onFinish = { loadSampleData ->
                viewModel.saveProfile(draft.toProfile()) {
                    if (loadSampleData) viewModel.loadSampleData()
                }
            }
        )
    }
}

@Composable
private fun OnboardingNotesScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    var agreementsAccepted by rememberSaveable { mutableStateOf(false) }
    var readingDoc by rememberSaveable { mutableStateOf<String?>(null) } // "terms" or "privacy"

    val allAccepted = agreementsAccepted

    if (readingDoc != null) {
        AlertDialog(
            onDismissRequest = { readingDoc = null },
            shape = RoundedCornerShape(12.dp),
            containerColor = AppColors.cardBg,
            title = {
                Text(
                    text = if (readingDoc == "terms") "Terms & Conditions" else "Privacy Policy",
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary
                )
            },
            text = {
                Text(
                    text = if (readingDoc == "terms")
                        "1. Acceptance of Terms: By using Mavo, you agree to comply with and be bound by these terms.\n\n2. Usage: Mavo is an offline-first accounting companion. You are responsible for maintaining the security of your device and data.\n\n3. Liability: Mavo is provided as-is without warranty of any kind."
                    else
                        "1. Data Privacy: Mavo operates primarily offline. Your financial data stays securely on your local device.\n\n2. Cloud Backup: Cloud backups are optional and encrypted.\n\n3. No Tracking: We do not sell or share your personal business data with third parties.",
                    color = AppColors.textSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { readingDoc = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }

    val notes = listOf(
        Triple(
            Icons.AutoMirrored.Outlined.MenuBook,
            "Your business, in a single ledger.",
            "Mavo is a fast, offline-first accounting companion built for shopkeepers, traders and small businesses in India."
        ),
        Triple(
            Icons.Outlined.ReceiptLong,
            "Post vouchers. Print invoices. Done.",
            "Sales, purchase, receipts, payments and journals \u2014 all posted to the correct ledger automatically with GST, PDF export and party ledgers."
        ),
        Triple(
            Icons.Default.Check,
            "A quick note before you begin.",
            "Please review our terms and privacy practices. Your data stays on your device unless you choose to back it up."
        )
    )

    val pagerState = rememberPagerState(pageCount = { notes.size })

    // Smooth auto scroll sideways
    LaunchedEffect(pagerState) {
        while (true) {
            delay(2000)
            val nextPage = (pagerState.currentPage + 1) % notes.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.screenBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top sliding animation / indicator row with card background and proper distinction
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Step ${pagerState.currentPage + 1} of ${notes.size}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.textPrimary
                    )
                    OnboardingDots(activeIndex = pagerState.currentPage, count = notes.size)
                }
            }

            // HorizontalPager for smooth sideways liquid scrolling of quick notes
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val note = notes[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.size(128.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = note.first,
                            contentDescription = null,
                            tint = AppColors.textPrimary,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = note.second,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textPrimary,
                            lineHeight = 38.sp
                        )
                        Text(
                            text = note.third,
                            fontSize = 17.sp,
                            color = AppColors.textSecondary,
                            lineHeight = 25.sp
                        )
                    }
                }
            }

            // Universal T&C and Privacy Policy agreement
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreementsAccepted,
                    onCheckedChange = { agreementsAccepted = it }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "I accept ",
                        fontSize = 13.sp,
                        color = AppColors.textSecondary
                    )
                    Text(
                        text = "Terms & Conditions",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.primary,
                        modifier = Modifier.clickable { readingDoc = "terms" }
                    )
                    Text(
                        text = " & ",
                        fontSize = 13.sp,
                        color = AppColors.textSecondary
                    )
                    Text(
                        text = "Privacy Policy",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.primary,
                        modifier = Modifier.clickable { readingDoc = "privacy" }
                    )
                }
            }

            // Enlarged Continue button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = allAccepted, onClick = onContinue),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (allAccepted) AppColors.gold else AppColors.border
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (allAccepted) 4.dp else 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Continue",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (allAccepted) AppColors.textOnPrimary else AppColors.textSecondary,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingDots(
    activeIndex: Int,
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) { index ->
            if (index == activeIndex) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
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
