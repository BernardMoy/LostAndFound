package com.example.lostandfound.ui.Settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.lostandfound.R
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.CustomActionRow
import com.example.lostandfound.CustomElements.CustomGrayTitle
import com.example.lostandfound.Data.SharedPreferencesNames
import com.example.lostandfound.ui.theme.ComposeTheme


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

    Appearance(context = context)
    HorizontalDivider(thickness = 1.dp)
    Permissions(context = context)
    HorizontalDivider(thickness = 1.dp)
    AboutTheApp(context = context)
    HorizontalDivider(thickness = 1.dp)
    Developer(context = context)
}

@Composable
fun Appearance(
    context: Context
){
    CustomGrayTitle(text = "Appearance")

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

@Composable
fun Permissions(
    context: Context
){
    CustomGrayTitle(text = "Permissions")
    Column (
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
    ){
        CustomActionRow(text = "Notifications",
            leftIcon = Icons.Outlined.Notifications,
            onClick = {
                Toast.makeText(context, "Color theme", Toast.LENGTH_SHORT).show()
            }
        )

        CustomActionRow(text = "Location",
            leftIcon = Icons.Outlined.LocationOn,
            onClick = {
                Toast.makeText(context, "Color theme", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun AboutTheApp(
    context: Context
){
    CustomGrayTitle(text = "About the app")
    Column (
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
    ) {
        CustomActionRow(text = "About the app",
            leftIcon = Icons.Outlined.Info,
            onClick = {
                Toast.makeText(context, "Color theme", Toast.LENGTH_SHORT).show()
            }
        )

        /*
        The following feature requires login.
        Currently all settings features does not require login,
        hence it has been removed
         */
        /*
        CustomActionRow(text = "Report a problem with the app",
            leftIcon = Icons.Outlined.Report,
            onClick = {
                Toast.makeText(context, "Color theme", Toast.LENGTH_SHORT).show()
            }
        )

         */
    }
}

@Composable
fun Developer(
    context: Context
){
    CustomGrayTitle(text = "Developer settings")
    Column (
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
    ) {
        CustomActionRow(text = "Delete shared preferences data",
            leftIcon = Icons.Outlined.Delete,
            onClick = {
                // clear shared preferences from each key defined in the names file
                val sp: SharedPreferences = context.getSharedPreferences(SharedPreferencesNames.NAME_USERS, Context.MODE_PRIVATE)
                sp.edit().clear().apply()

                Toast.makeText(context, "Shared preferences cleared", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
