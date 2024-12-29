package com.example.lostandfound.CustomElements

import android.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.Typography

@Preview(showBackground = true)
@Composable
fun Preview() {
    val data = mapOf(
        FirebaseNames.LOSTFOUND_USER to 3
    )
    CustomLostItemPreview(data = data)
}

@Composable
fun CustomLostItemPreview(
    data: Map<String, Any>
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)
            )
        )
    ){
        Text(text = "Lost Entry",
            style = Typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = dimensionResource(id = R.dimen.content_margin),
                    top = dimensionResource(id = R.dimen.title_margin)
                )
        )
    }
}