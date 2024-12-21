package com.example.lostandfound.CustomElements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenu(
    items: List<String>,      // a list of items to show up in the menu
    onItemSelected: (String) -> Unit    // function to execute when an item is selected
){
    var isExpanded by remember {
        mutableStateOf(false)
    }

    var selectedText by remember {
        mutableStateOf(items[0])
    }
    Column (
        modifier = Modifier.fillMaxWidth()
    ){
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = {isExpanded = !isExpanded},   // invert is Expanded
        ) {
            // the text displayed in the box
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)  // show the icon on the right
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,

                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                modifier = Modifier.background(MaterialTheme.colorScheme.background),  // a small ring around the texts
                onDismissRequest = { isExpanded = false }
            ) {
                items.forEachIndexed{ index, text ->
                    DropdownMenuItem(
                        text = { Text(
                            text = text,
                            style = MaterialTheme.typography.bodyMedium
                        ) },
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        onClick = {
                            selectedText = items[index]
                            isExpanded = false

                            // call the custom on item selected function
                            onItemSelected(text)
                        }
                    )
                }
            }
        }
    }
}