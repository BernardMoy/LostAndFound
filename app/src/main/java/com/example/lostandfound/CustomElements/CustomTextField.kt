package com.example.lostandfound.CustomElements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lostandfound.R
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.ui.theme.Typography

// contains text fields that the user can interact with
// including typing, picking date etc.
@Composable
fun CustomEditText(
    fieldLabel: String,
    fieldContent: String,    // val is passed if not editable, var if editable
    isEditable: Boolean,
    leftIcon: ImageVector,
    rightIcon: ImageVector? = null,
    onTextChanged: ((String) -> Unit)? = null,
    isError: Boolean = false,
    testTag: String = ""
) {
    // mutable field content text
    var varFieldContent by remember { mutableStateOf(fieldContent) }

    TextField(
        // varFieldContent is a mutable version of field content, that would be used when editable is true
        // it is used to be passed to the onTextChanged callback function
        value = if (isEditable) varFieldContent else fieldContent,
        textStyle = Typography.bodyMedium,   // style for the content
        onValueChange = { newText ->
            if (isEditable) {
                varFieldContent = newText
                onTextChanged?.invoke(varFieldContent)
            }
        },
        label = {
            Text(text = fieldLabel, style = Typography.bodySmall)   // style for the label
        },
        leadingIcon = {
            Icon(
                imageVector = leftIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        },
        trailingIcon = {
            // right icon can be null
            if (rightIcon != null) {
                Icon(
                    imageVector = rightIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 0.dp, vertical = dimensionResource(id = R.dimen.content_margin_half)
            )
            .testTag(testTag),
        enabled = isEditable,
        isError = isError,
        colors = TextFieldDefaults.colors(
            // for enabled (Editable) text
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,

            // for disabled (non editable) text
            disabledContainerColor = Color.Transparent,
            disabledTextColor = MaterialTheme.colorScheme.onBackground,
            disabledLabelColor = Color.Gray,

            // remove the default bottom line
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun CustomInputField(
    placeholder: String,
    fieldContent: String,    // val is passed if not editable, var if editable
    isEditable: Boolean,
    onTextChanged: ((String) -> Unit)? = null,
    isError: Boolean = false,
    isMultiLine: Boolean = false,
    testTag: String = "",
    leadingIcon: ImageVector? = null
) {
    var varFieldContent by remember { mutableStateOf(fieldContent) }

    OutlinedTextField(
        value = varFieldContent,
        placeholder = {
            Text(text = placeholder, style = Typography.bodyMedium, color = Color.Gray)
        },
        onValueChange = { newText ->
            if (isEditable) {
                varFieldContent = newText
                onTextChanged?.invoke(varFieldContent)
            }
        },
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)),
        enabled = isEditable,
        isError = isError,
        singleLine = !isMultiLine,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 0.dp, vertical = dimensionResource(id = R.dimen.content_margin_half)
            )
            .testTag(testTag),
        colors = TextFieldDefaults.colors(
            // for enabled (Editable) text
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedContainerColor = MaterialTheme.colorScheme.background,

            // for disabled (non editable) text
            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledTextColor = MaterialTheme.colorScheme.onBackground,

            // for the color of the border
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
            disabledIndicatorColor = MaterialTheme.colorScheme.outline,

            errorContainerColor = MaterialTheme.colorScheme.background,
            errorTextColor = MaterialTheme.colorScheme.onBackground
        ),
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = "Icon",
                    tint = Color.Gray
                )
            }
        }
    )
}

// currently, this text field has no function to callback when the string date changes
// may be implemented in the future if needed
@Composable
fun CustomDatePickerTextField(
    // pass in a variable of selected date, initially is the current date.
    selectedDate: MutableState<Long?>,
    isDialogShown: MutableState<Boolean>,
    placeholder: String,
    datePickerTitle: String,
    isError: Boolean = false,
    testTag: String = "",
) {

    Column {
        // the text field to prompt user to open date picker
        OutlinedTextField(
            value = if (selectedDate.value == null) "" else DateTimeManager.dateToString(
                selectedDate.value!!
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    style = Typography.bodyMedium,
                    color = Color.Gray
                )
            },
            onValueChange = {
                // do nothing as it is always disabled
            },
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)),
            isError = isError,
            readOnly = true,   // make it read only
            singleLine = true,
            enabled = false,   // this is needed to make the text field clickable
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 0.dp,
                    vertical = dimensionResource(id = R.dimen.content_margin_half)
                )
                .clickable {
                    // when the text field is clicked, open the dialog
                    isDialogShown.value = true
                }
                .testTag(testTag),
            colors = TextFieldDefaults.colors(
                // for enabled (Editable) text
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                errorContainerColor = MaterialTheme.colorScheme.background,

                // for the color of the border
                disabledIndicatorColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,

                // for the text color that is not placeholder
                disabledTextColor = MaterialTheme.colorScheme.onBackground,

                ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = "Pick a date",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
        )

        // if isDialogShown is true, show the date selection dialog
        CustomDatePickerDialog(
            selectedDate = selectedDate,
            isDialogShown = isDialogShown,
            datePickerTitle = datePickerTitle
        )
    }
}


@Composable
fun CustomTimePickerTextField(
    selectedHour: MutableState<Int?>,
    selectedMinute: MutableState<Int?>,
    isDialogShown: MutableState<Boolean>,
    placeholder: String,
    isError: Boolean = false,
    testTag: String = ""
) {

    Column {
        // the text field to prompt user to open date picker
        OutlinedTextField(
            value = if (selectedHour.value == null || selectedMinute.value == null) "" else
                DateTimeManager.timeToString(selectedHour.value!!, selectedMinute.value!!),
            placeholder = {
                Text(
                    text = placeholder,
                    style = Typography.bodyMedium,
                    color = Color.Gray
                )
            },
            onValueChange = {

            },
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)),
            isError = isError,
            readOnly = true,   // make it read only
            singleLine = true,
            enabled = false,   // this is needed to make the text field clickable
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 0.dp,
                    vertical = dimensionResource(id = R.dimen.content_margin_half)
                )
                .clickable {
                    // when the text field is clicked, open the dialog
                    isDialogShown.value = true
                }
                .testTag(testTag),
            colors = TextFieldDefaults.colors(
                // for enabled (Editable) text
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                errorContainerColor = MaterialTheme.colorScheme.background,

                // for the color of the border
                disabledIndicatorColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,

                // for the text color that is not placeholder
                disabledTextColor = MaterialTheme.colorScheme.onBackground
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = "Pick a time",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
        )

        // if isDialogShown is true, show the date selection dialog
        CustomTimePickerDialog(
            selectedHour = selectedHour,
            selectedMinute = selectedMinute,
            isDialogShown = isDialogShown
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewDoubleText() {
    /*
    CustomComparisonTextField(
        centerLabel = "Label",
        contentLeft = "Lefteeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee",
        contentRight = "Right",
        icon = Icons.Outlined.PestControlRodent
    )

     */

    CustomChoiceTextField(
        title = "Choice",
        leadingIcon = Icons.Outlined.MonetizationOn,
        state = remember {
            true
        })
}

@Composable
fun CustomComparisonTextField(
    centerLabel: String,
    contentLeft: String,    // val is passed if not editable, var if editable
    contentRight: String,
    icon: ImageVector?,
    isMatch: Boolean = false   // if true, then the text will appear green and also a matching text will be shown
) {
    // partition the row into 3 parts (1:1:1)
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = contentLeft,
            style = Typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .weight(3f)
                .padding(
                    horizontal = dimensionResource(id = R.dimen.content_margin_half)
                ),
            textAlign = TextAlign.Center,
        )

        Column(
            modifier = Modifier.weight(3f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Attribute icon",
                        tint = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.content_margin_half)))

                Text(
                    text = centerLabel,
                    style = Typography.bodyMedium,
                    color = Color.Gray
                )
            }
            // show the matching icon
            if (isMatch) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        tint = colorResource(id = R.color.status2),
                        contentDescription = "Match",
                        modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                    )
                    Text(
                        text = "Matches",
                        style = Typography.bodyMedium,
                        color = colorResource(id = R.color.status2),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }


        Text(
            text = contentRight,
            style = Typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .weight(3f)
                .padding(
                    horizontal = dimensionResource(id = R.dimen.content_margin_half)
                ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CustomComparisonField(
    centerLabel: @Composable () -> Unit,
    contentLeft: @Composable () -> Unit,
    contentRight: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier.weight(3f),
            contentAlignment = Alignment.Center
        ) {
            contentLeft()
        }

        Box(
            modifier = Modifier.weight(3f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                centerLabel()
            }
        }

        Box(
            modifier = Modifier.weight(3f),
            contentAlignment = Alignment.Center
        ) {
            contentRight()
        }
    }
}


@Composable
fun CustomSearchField(
    placeholder: String,
    fieldContent: MutableState<String>
) {

    OutlinedTextField(
        value = fieldContent.value,
        placeholder = {
            Text(text = placeholder, style = Typography.bodyMedium, color = Color.Gray)
        },
        onValueChange = { newText ->
            fieldContent.value = newText
        },
        enabled = true,
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )
        },
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)),
        trailingIcon = {
            CustomTooltipBox(text = "Clear") {
                IconButton(
                    onClick = {
                        fieldContent.value = ""
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Clear",
                        tint = Color.Gray
                    )
                }
            }

        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 0.dp, vertical = dimensionResource(id = R.dimen.content_margin_half)
            ),

        colors = TextFieldDefaults.colors(
            // for enabled (Editable) text
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedContainerColor = MaterialTheme.colorScheme.background,

            // for disabled (non editable) text
            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledTextColor = MaterialTheme.colorScheme.onBackground,

            // for the color of the border
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = Color.Gray,
            disabledIndicatorColor = MaterialTheme.colorScheme.outline,

            errorContainerColor = MaterialTheme.colorScheme.background,
            errorTextColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun CustomChoiceTextField(
    title: String,
    leadingIcon: ImageVector,
    state: Boolean,  // the state that selects to true when the field is clicked
    onClick: () -> Unit = {

    }
) {
    TextField(
        value = title,
        onValueChange = {},
        textStyle = Typography.bodyMedium,
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        },
        trailingIcon = {
            if (state) {
                Icon(
                    imageVector = Icons.Filled.Circle,
                    contentDescription = null,
                    tint = colorResource(id = R.color.status2),
                    modifier = Modifier.size(16.dp)
                )
            }

            Icon(
                imageVector = Icons.Outlined.Circle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 0.dp, vertical = dimensionResource(id = R.dimen.content_margin_half)
            ),

        enabled = false,
        isError = false,
        colors = TextFieldDefaults.colors(
            // for enabled (Editable) text
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,

            // for disabled (non-editable) text
            disabledContainerColor = Color.Transparent,
            disabledTextColor = MaterialTheme.colorScheme.onBackground,
            disabledLabelColor = Color.Gray,

            // remove the default bottom line
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}