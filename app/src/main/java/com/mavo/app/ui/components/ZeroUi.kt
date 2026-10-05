package com.mavo.app.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.mavo.app.ui.animation.pressScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.ui.animation.premiumClickable
import com.mavo.app.ui.theme.AppColors
import com.mavo.app.ui.theme.Surface
import com.mavo.app.ui.theme.mavoInputColors

val ScreenBorder: Color = Surface.cardBorder
val FieldBorder: Color = Surface.inputBorder

@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 44,
    tint: Color = AppColors.textPrimary
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .pressScale(enabled = true, interactionSource = interactionSource)
            .size(size.dp)
            .clip(CircleShape)
            .background(AppColors.cardBg)
            .border(1.dp, FieldBorder, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun StepProgress(
    step: Int,
    total: Int = 3,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        // Mockup 27: 4dp tall bars, 4dp gaps.
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (index < step) AppColors.primary else FieldBorder)
            )
        }
    }
}

@Composable
fun WizardHeader(
    stepLabel: String,
    onBack: (() -> Unit)?,
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
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
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stepLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
            color = AppColors.textSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
        if (onClose != null) {
            CircleIconButton(
                icon = Icons.Default.Close,
                contentDescription = "Close",
                onClick = onClose
            )
        } else {
            Spacer(modifier = Modifier.width(44.dp))
        }
    }
}

@Composable
fun ScreenHeader(
    title: String,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            CircleIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBack
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.weight(1f))
        trailing()
    }
}

@Composable
fun WizardTitle(
    text: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = text,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.textPrimary,
            lineHeight = 36.sp
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 15.sp,
                color = AppColors.textSecondary,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .pressScale(enabled = enabled, interactionSource = interactionSource)
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(if (enabled) AppColors.primary else AppColors.primary.copy(alpha = 0.35f))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.textOnPrimary
        )
    }
}

@Composable
fun SecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .pressScale(enabled = enabled, interactionSource = interactionSource)
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(AppColors.cardBg)
            .border(1.dp, FieldBorder, RoundedCornerShape(28.dp))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) AppColors.textPrimary else AppColors.textDisabled
        )
    }
}

@Composable
fun ZbField(
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
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
            color = AppColors.textSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = fieldModifier.fillMaxWidth(),
            placeholder = placeholder?.let {
                { Text(text = it, fontSize = 15.sp, color = AppColors.inputPlaceholder) }
            },
            readOnly = readOnly,
            singleLine = singleLine,
            textStyle = TextStyle(color = AppColors.inputText, fontSize = 15.sp),
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(14.dp),
            isError = isError,
            supportingText = supportingText,
            colors = mavoInputColors()
        )
    }
}

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val field: @Composable () -> Unit = {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().then(modifier),
            placeholder = {
                Text(text = placeholder, fontSize = 15.sp, color = AppColors.inputPlaceholder)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = AppColors.textTertiary
                )
            },
            readOnly = readOnly,
            singleLine = true,
            enabled = onClick == null,
            textStyle = TextStyle(color = AppColors.inputText, fontSize = 15.sp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FieldBorder,
                unfocusedBorderColor = FieldBorder,
                focusedContainerColor = AppColors.cardBg,
                unfocusedContainerColor = AppColors.cardBg,
                disabledContainerColor = AppColors.cardBg
            )
        )
    }
    if (onClick != null) {
        val interactionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(interactionSource = interactionSource, indication = LocalIndication.current, onClick = onClick)
        ) { field() }
    } else {
        field()
    }
}

@Composable
fun ZbCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: Int = 16,
    content: @Composable ColumnScope.() -> Unit
) {
    val base = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(AppColors.cardBg)
        .border(1.dp, ScreenBorder, RoundedCornerShape(16.dp))
    val content2 = if (onClick != null) {
        val interactionSource = remember { MutableInteractionSource() }
        base.pressScale(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = LocalIndication.current, onClick = onClick)
    } else {
        base
    }
    Column(
        modifier = content2.padding(contentPadding.dp),
        content = content
    )
}

@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = AppColors.textTertiary
        )
        Spacer(modifier = Modifier.weight(1f))
        action?.invoke()
    }
}

@Composable
fun ChoiceChipRow(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .pressScale(interactionSource = interactionSource)
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (selected) AppColors.primary else AppColors.cardBg)
                    .border(1.dp, if (selected) AppColors.primary else FieldBorder, RoundedCornerShape(18.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current
                    ) { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (selected) AppColors.textOnPrimary else AppColors.textSecondary
                )
            }
        }
    }
}

@Composable
fun ChipRow(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
    ) {
        items(options.size) { index ->
            val selected = index == selectedIndex
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .pressScale(interactionSource = interactionSource)
                    .height(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (selected) AppColors.primary else AppColors.cardBg)
                    .border(1.dp, if (selected) AppColors.primary else FieldBorder, RoundedCornerShape(18.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current
                    ) { onSelect(index) }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = options[index],
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (selected) AppColors.textOnPrimary else AppColors.textSecondary
                )
            }
        }
    }
}

@Composable
fun SegmentedTabs(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.sectionHeaderBg)
            .padding(4.dp)
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .pressScale(interactionSource = interactionSource)
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) AppColors.primary else Color.Transparent)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current
                    ) { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (selected) AppColors.textOnPrimary else AppColors.textSecondary
                )
            }
        }
    }
}

@Composable
fun BottomActionBar(
    primaryLabel: String,
    onPrimary: () -> Unit,
    modifier: Modifier = Modifier,
    primaryEnabled: Boolean = true,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AppColors.cardBg)
    ) {
        HorizontalDivider(color = ScreenBorder)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (secondaryLabel != null && onSecondary != null) {
                SecondaryButton(label = secondaryLabel, onClick = onSecondary)
            }
            PrimaryButton(
                label = primaryLabel,
                onClick = onPrimary,
                enabled = primaryEnabled
            )
        }
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(AppColors.sectionHeaderBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.textSecondary,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.textPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = AppColors.textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(20.dp))
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .pressScale(interactionSource = interactionSource)
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppColors.primary)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        onClick = onAction
                    )
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = actionLabel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textOnPrimary
                )
            }
        }
    }
}
