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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PestControlRodent
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lostandfound.Data.ChatMessage
import com.example.lostandfound.R
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography
import kotlin.math.max

@Composable
fun CustomEditText(
    fieldLabel: String,
    fieldContent: String,    // val is passed if not editable, var if editable
    isEditable: Boolean,
    leftIcon: ImageVector,
    rightIcon: ImageVector? = null,
    onTextChanged: ((String) -> Unit)? = null,
    isError: Boolean = false
){
    // mutable field content text
    var varFieldContent by remember{ mutableStateOf(fieldContent)}

    TextField(
        // varFieldContent is a mutable version of field content, that would be used when editable is true
        // it is used to be passed to the onTextChanged callback function
        value = if (isEditable) varFieldContent else fieldContent,
        textStyle = Typography.bodyMedium,   // style for the content
        onValueChange = { newText ->
            if (isEditable){
                varFieldContent = newText
                onTextChanged?.invoke(varFieldContent)
            }
        },
        label = {
            Text(text = fieldLabel, style = Typography.bodySmall)   // style for the label
        },
        leadingIcon = {
            Icon(imageVector = leftIcon, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        },
        trailingIcon = {
            // right icon can be null
            if (rightIcon != null){
                Icon(imageVector = rightIcon, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 0.dp, vertical = dimensionResource(id = R.dimen.content_margin_half)
            ),
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
    testTag: String = ""
){
    var varFieldContent by remember{ mutableStateOf(fieldContent)}

    OutlinedTextField(
        value = varFieldContent,
        placeholder = {
            Text(text = placeholder, style = Typography.bodyMedium, color = Color.Gray)
        },
        onValueChange = { newText ->
            if (isEditable){
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
        )
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
    isError: Boolean = false
){

    Column {
        // the text field to prompt user to open date picker
        OutlinedTextField(
            value = if (selectedDate.value == null) "" else DateTimeManager.dateToString(selectedDate.value!!),
            placeholder = {
                Text(text = placeholder,
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
                },
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
                Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = "Pick a date", tint = MaterialTheme.colorScheme.onBackground)
            },
        )

        // if isDialogShown is true, show the date selection dialog
        CustomDatePickerDialog(
            selectedDate = selectedDate,
            isDialogShown = isDialogShown
        )
    }
}



@Composable
fun CustomTimePickerTextField(
    selectedHour: MutableState<Int?>,
    selectedMinute: MutableState<Int?>,
    isDialogShown: MutableState<Boolean>,
    placeholder: String,
    isError: Boolean = false
){

    Column {
        // the text field to prompt user to open date picker
        OutlinedTextField(
            value = if (selectedHour.value == null || selectedMinute.value == null) "" else
                DateTimeManager.timeToString(selectedHour.value!!, selectedMinute.value!!),
            placeholder = {
                Text(text = placeholder,
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
                },
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
                Icon(imageVector = Icons.Outlined.AccessTime, contentDescription = "Pick a time", tint = MaterialTheme.colorScheme.onBackground)
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
    ComposeTheme {
        Column (
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
        ){
            CustomChatField(
                text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
                timestamp = 2L,
                isSentByCurrentUser = false
            )

            CustomChatField(
                text = "e",
                timestamp = 2L,
                isSentByCurrentUser = true
            )
        }
    }
}

@Composable
fun CustomComparisonTextField(
    centerLabel: String,
    contentLeft: String,    // val is passed if not editable, var if editable
    contentRight: String,
    icon: ImageVector?
){
    // partition the row into 3 parts (1:1:1)
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ){
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

        Row(
            modifier = Modifier.weight(3f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            if (icon != null){
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
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        Box(
            modifier = Modifier.weight(3f),
            contentAlignment = Alignment.Center
        ){
            contentLeft()
        }

        Box(
            modifier = Modifier.weight(3f),
            contentAlignment = Alignment.Center
        ){
            centerLabel()
        }

        Box(
            modifier = Modifier.weight(3f),
            contentAlignment = Alignment.Center
        ){
            contentRight()
        }
    }
}


@Composable
fun CustomSearchField(
    placeholder: String,
    fieldContent: MutableState<String>
){

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
            Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search", tint = Color.Gray)
        },
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)),
        trailingIcon = {
            IconButton(
                onClick = {
                    fieldContent.value = ""
                }
            ) {
                Icon(imageVector = Icons.Outlined.Close, contentDescription = "Clear", tint = Color.Gray)
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
fun CustomChatField(
    text: String,
    timestamp: Long,
    isSentByCurrentUser: Boolean
){
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start
    ){
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isSentByCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
            ),
            // set the max width of the text message to be 2/3 of the row width
            modifier = Modifier.widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 2/3)
        ) {
            Text(
                text = text,
                style = Typography.bodyMedium,
                color = if (isSentByCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.title_margin)),
                textAlign = if (isSentByCurrentUser) TextAlign.End else TextAlign.Start
            )
        }
    }
}