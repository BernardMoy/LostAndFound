package com.example.lostandfound.CustomElements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import com.example.lostandfound.R

// the dropdown menu is used for search ordering and category selection
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenu(
    items: List<String>,      // a list of items to show up in the menu
    selectedText: MutableState<String>, // placeholder is displayed if this value is ""
    placeholder: String = "Select an item...",
    isError: Boolean = false,
    onValueChange: (String) -> Unit = {}
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded },   // invert is Expanded
        ) {
            // the text displayed in the box
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                value = if (selectedText.value != "") selectedText.value else placeholder,
                onValueChange = onValueChange,
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)  // show the icon on the right
                },
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)),
                isError = isError,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,

                    // the text color is gray for the placeholder
                    unfocusedTextColor = if (selectedText.value == "") Color.Gray else MaterialTheme.colorScheme.onBackground,
                    focusedTextColor = if (selectedText.value == "") Color.Gray else MaterialTheme.colorScheme.onBackground,

                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,

                    errorContainerColor = MaterialTheme.colorScheme.background,
                    errorTextColor = if (selectedText.value == "") Color.Gray else MaterialTheme.colorScheme.onBackground
                ),
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                modifier = Modifier.background(MaterialTheme.colorScheme.background),  // a small ring around the texts
                onDismissRequest = { isExpanded = false }
            ) {
                items.forEachIndexed { index, text ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        onClick = {
                            selectedText.value = items[index]  // change the selected text
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}