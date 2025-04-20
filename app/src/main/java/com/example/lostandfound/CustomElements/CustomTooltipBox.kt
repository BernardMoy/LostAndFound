package com.example.lostandfound.CustomElements

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTooltipBox(
    text: String,
    content: @Composable () -> Unit
) {
    // customise tooltip styles here
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
            ) {
                Text(
                    text = text,
                )
            }
        },
        state = TooltipState(initialIsVisible = false)
    ) {
        content()
    }
}