package com.example.lostandfound.CustomElements

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.lostandfound.ui.theme.Typography

// A single chip used to select colors
@Composable
fun CustomFilterChip(
    label: String,
    leadingIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    isError: Boolean,
    leadingIconTint: Color = MaterialTheme.colorScheme.outline
) {

    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = Typography.bodyMedium,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = "Click to select",
                tint = leadingIconTint
            )
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
        ),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            containerColor = MaterialTheme.colorScheme.background,
            labelColor = MaterialTheme.colorScheme.onBackground
        )
    )
}