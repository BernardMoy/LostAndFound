package com.example.lostandfound.ui.Settings

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.lostandfound.R
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.BackToolbar
import com.example.lostandfound.CustomActionRow
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography


class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SettingsScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    SettingsScreen(activity = MockActivity())
}

@Composable
fun SettingsScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "Settings", activity = activity)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = innerPadding)
                        .padding(dimensionResource(id = R.dimen.title_margin))
                        .verticalScroll(rememberScrollState())   // make screen scrollable
                ) {
                    // content goes here
                    MainContent()
                }
            }
        }
    }
}

// content includes avatar, edit fields, reminder message and save button
// get the view model in the function parameter
@Composable
fun MainContent(viewModel: SettingsViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    Text(text = "Appearance",
        style = Typography.bodyMedium,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.content_margin))
        )

    Column (
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
    ){
        CustomActionRow(text = "Color theme",
            leftIcon = Icons.Outlined.Palette,
            onClick = {
                Toast.makeText(context, "Color theme", Toast.LENGTH_SHORT).show()
            }
        )

        CustomActionRow(text = "Day / night mode",
            leftIcon = Icons.Outlined.DarkMode,
            onClick = {
                Toast.makeText(context, "Color theme", Toast.LENGTH_SHORT).show()
            }
        )
    }
}