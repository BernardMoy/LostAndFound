package com.example.lostandfound

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.lostandfound.ui.theme.Typography

@Composable
fun CustomDataField(
    fieldLabel: String,
    fieldContent: String,
    isEditable: Boolean,
    leftIcon: ImageVector,
    rightIcon: ImageVector? = null,
){
    var text by remember{ mutableStateOf(fieldContent)}

    TextField(
        value = text,
        textStyle = Typography.bodyMedium,   // style for the content
        onValueChange = {
            text = it   // it refers to the lambda parameter
        },
        label = {
            Text(text = fieldLabel, style = Typography.bodySmall)   // style for the label
        },
        leadingIcon = {
            Icon(imageVector = leftIcon, contentDescription = null, tint = Color.Gray)
        },
        trailingIcon = {
            // right icon can be null
            if (rightIcon != null){
                Icon(imageVector = rightIcon, contentDescription = null, tint = Color.Gray)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                0.dp,
                dimensionResource(id = R.dimen.content_margin_half),
                0.dp,
                dimensionResource(id = R.dimen.content_margin_half)
            ),
        enabled = isEditable,
        colors = TextFieldDefaults.colors(
            // for enabled (Editable) text
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,

            // for disabled (non editable) text
            disabledContainerColor = Color.Transparent,
            disabledTextColor = MaterialTheme.colorScheme.onBackground,

            // remove the default bottom line
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}