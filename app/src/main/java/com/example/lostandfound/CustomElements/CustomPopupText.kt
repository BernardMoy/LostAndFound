package com.example.lostandfound.CustomElements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography

@Preview(showBackground = true)
@Composable
fun Previews() {
    ComposeTheme {
        CustomPopupText(
            title = "Question",
            content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in r"
        )
    }
}

@Composable
fun CustomPopupText(
    title: String,
    content: String
){
    // is pop up is default to false
    val isPopUp = remember{
        mutableStateOf(false)
    }

    Column(

    ){
        // show the question
        Row (
            modifier = Modifier.fillMaxWidth()
                .clickable {
                    // set is pop up to true
                    isPopUp.value = !isPopUp.value
                }
                .padding(
                    dimensionResource(R.dimen.content_margin)
                ),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = title,
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )

            if (isPopUp.value){
                Icon(
                    imageVector = Icons.Outlined.ArrowDropUp,
                    contentDescription = "Open",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = "Open",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // show the answer if is pop up is true
        if (isPopUp.value){
            Row (
                modifier = Modifier
                    .padding(
                        dimensionResource(R.dimen.content_margin)
                    ),
            ) {
                Text(
                    text = content,
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}