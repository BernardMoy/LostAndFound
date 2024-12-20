package com.example.lostandfound.CustomElements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.Typography

@Composable
fun CustomGrayTitle(
    text: String
){
    Text(text = text,
        style = Typography.bodyMedium,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.content_margin))
    )
}