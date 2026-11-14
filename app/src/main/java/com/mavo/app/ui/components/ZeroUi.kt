package com.mavo.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.mavo.app.ui.animation.cardExpandClickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mavo.app.ui.theme.AppColors
import com.mavo.app.ui.theme.DimensionTokens
import com.mavo.app.ui.theme.IconTokens
import com.mavo.app.ui.theme.LayoutTokens
import com.mavo.app.ui.theme.ShapeTokens
import com.mavo.app.ui.theme.Surface
import com.mavo.app.ui.theme.TextSizeTokens
import com.mavo.app.ui.theme.mavoInputColors

val ScreenBorder: Color = Color.Transparent
val FieldBorder: Color = Surface.inputBorder

@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 48,
    tint: Color = AppColors.textPrimary
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier.size(size.coerceAtLeast(DimensionTokens.minTouchTarget.value.toInt()).dp),
        colors = androidx.compose.material3.IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = AppColors.cardBg,
            contentColor = tint
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(IconTokens.medium)
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
        horizontalArrangement = Arrangement.spacedBy(LayoutTokens.micro)
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(LayoutTokens.micro)
                    .clip(ShapeTokens.badge)
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
            .padding(horizontal = LayoutTokens.large, vertical = LayoutTokens.medium),
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
            fontSize = TextSizeTokens.caption,
            fontWeight = FontWeight.SemiBold,
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
fun WizardTitle(
    text: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = text,
            style = MaterialTheme.typography.displaySmall,
            color = AppColors.textPrimary,
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(LayoutTokens.small))
            Text(
                text = subtitle,
                fontSize = TextSizeTokens.bodyLarge,
                lineHeight = TextSizeTokens.bodyLargeLineHeight,
                color = AppColors.textSecondary
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
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(DimensionTokens.buttonHeightLarge),
        enabled = enabled,
        shape = ShapeTokens.button,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(DimensionTokens.buttonHeightLarge),
        enabled = enabled,
        shape = ShapeTokens.button,
        border = BorderStroke(DimensionTokens.borderThin, AppColors.primary.copy(alpha = 0.4f))
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
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
            text = label,
            fontSize = TextSizeTokens.caption,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.textSecondary
        )
        Spacer(modifier = Modifier.height(LayoutTokens.small))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = fieldModifier.fillMaxWidth(),
            placeholder = placeholder?.let {
                { Text(text = it, fontSize = TextSizeTokens.bodyLarge, color = AppColors.inputPlaceholder) }
            },
            readOnly = readOnly,
            singleLine = singleLine,
            textStyle = TextStyle(color = AppColors.inputText, fontSize = TextSizeTokens.bodyLarge),
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = ShapeTokens.input,
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
    shape: Shape = ShapeTokens.input,
    onClick: (() -> Unit)? = null
) {
    val field: @Composable () -> Unit = {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().then(modifier),
            placeholder = {
                Text(text = placeholder, fontSize = TextSizeTokens.bodyLarge, color = AppColors.inputPlaceholder)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = AppColors.textTertiary,
                    modifier = Modifier.size(IconTokens.standard)
                )
            },
            readOnly = readOnly,
            singleLine = true,
            enabled = onClick == null,
            textStyle = TextStyle(color = AppColors.inputText, fontSize = TextSizeTokens.bodyLarge),
            shape = shape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.primary,
                unfocusedBorderColor = Color.Transparent,
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
    contentPadding: Int = LayoutTokens.cardPadding.value.toInt(),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShapeTokens.card)
            .background(Surface.card)
            .border(DimensionTokens.borderThin, Surface.cardBorder, ShapeTokens.card)
            .then(onClick?.let { Modifier.cardExpandClickable(onClick = it) } ?: Modifier)
            .padding(contentPadding.dp),
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
            text = text,
            fontSize = TextSizeTokens.caption,
            fontWeight = FontWeight.SemiBold,
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
        horizontalArrangement = Arrangement.spacedBy(LayoutTokens.small)
    ) {
        options.forEachIndexed { index, option ->
            FilterChip(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                label = { Text(option, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
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
        horizontalArrangement = Arrangement.spacedBy(LayoutTokens.small),
        contentPadding = PaddingValues(horizontal = LayoutTokens.large)
    ) {
        items(options.size) { index ->
            FilterChip(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                label = { Text(options[index], maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
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
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
            ) {
                Text(option, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
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
            .padding(horizontal = LayoutTokens.xxl, vertical = LayoutTokens.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(IconTokens.hero)
                .clip(CircleShape)
                .background(AppColors.sectionHeaderBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.textSecondary,
                modifier = Modifier.size(IconTokens.standard)
            )
        }
        Spacer(modifier = Modifier.height(LayoutTokens.large))
        Text(
            text = title,
            fontSize = TextSizeTokens.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.textPrimary
        )
        Spacer(modifier = Modifier.height(LayoutTokens.small))
        Text(
            text = message,
            fontSize = TextSizeTokens.bodyMedium,
            lineHeight = TextSizeTokens.bodyMediumLineHeight,
            color = AppColors.textSecondary,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(LayoutTokens.large))
            TextButton(onClick = onAction) {
                Text(actionLabel)
            }
        }
    }
}
