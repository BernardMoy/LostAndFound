package com.example.lostandfound.ui.Settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Animation
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Compare
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.CustomActionRow
import com.example.lostandfound.CustomElements.CustomGrayTitle
import com.example.lostandfound.Data.DevData
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.SharedPreferencesNames
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.R
import com.example.lostandfound.ui.AboutApp.AboutAppActivity
import com.example.lostandfound.ui.Done.DoneActivity
import com.example.lostandfound.ui.EmailSenderTest.EmailSenderTestActivity
import com.example.lostandfound.ui.ImageComparison.ImageComparisonActivity
import com.example.lostandfound.ui.ItemComparison.ItemComparisonActivity
import com.example.lostandfound.ui.PermissionsTest.PermissionsTestActivity
import com.example.lostandfound.ui.PushNotificationsTest.PushNotificationsTestActivity
import com.example.lostandfound.ui.Search.SearchActivity
import com.example.lostandfound.ui.SettingsAnimation.SettingsAnimationActivity
import com.example.lostandfound.ui.SettingsAutoLoading.SettingsAutoLoadingActivity
import com.example.lostandfound.ui.SettingsFontSize.SettingsFontSizeActivity
import com.example.lostandfound.ui.SettingsPushNotifications.SettingsPushNotificationsActivity
import com.example.lostandfound.ui.SettingsTheme.SettingsThemeActivity
import com.example.lostandfound.ui.ViewComparison.ViewComparisonActivity
import com.example.lostandfound.ui.ViewReportedIssues.ViewReportedIssuesActivity
import com.example.lostandfound.ui.ViewReportedUsers.ViewReportedUsersActivity
import com.example.lostandfound.ui.theme.ComposeTheme
import com.google.firebase.firestore.FirebaseFirestore

/*
Settings that are classified into regular, admin settings, developer settings
 */
class SettingsActivity : ComponentActivity() {
    val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SettingsScreen(activity = this, viewModel = viewModel)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()


@Composable
fun SettingsScreen(activity: ComponentActivity, viewModel: SettingsViewModel) {
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
                        .verticalScroll(rememberScrollState())   // make screen scrollable
                ) {
                    // content goes here
                    MainContent(viewModel = viewModel)

                    // if the user is logged in, load if is admin
                    if (UserManager.isUserLoggedIn()) {
                        viewModel.loadIsAdmin()
                    }
                }
            }
        }
    }
}

// content includes avatar, edit fields, reminder message and save button
// get the view model in the function parameter
@Composable
fun MainContent(viewModel: SettingsViewModel) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current


    Appearance(context = context)
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
    Permissions(context = context)
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
    AboutTheApp(context = context)

    // if the user is admin, enable these
    if (viewModel.isAdmin.value) {
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        Admin(context = context, viewModel = viewModel)
    }

    // if the user is developer and dev settings are enabled, enable these
    if (UserManager.isUserDev() && DevData.IS_DEV_SETTINGS_SHOWN) {
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        Developer(context = context, viewModel = viewModel)
    }

    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
}

@Composable
fun Appearance(
    context: Context
) {
    Box(
        modifier = Modifier.padding(
            horizontal = dimensionResource(id = R.dimen.content_margin)
        )
    ) {
        CustomGrayTitle(text = "Appearance")
    }

    Column {
        /*
        CustomActionRow(text = "Color theme",
            leftIcon = Icons.Outlined.Palette,
            onClick = {
                Toast.makeText(context, "Color theme", Toast.LENGTH_SHORT).show()
            }
        )

         */

        CustomActionRow(text = "Day / night mode",
            leftIcon = Icons.Outlined.DarkMode,
            onClick = {
                val intent = Intent(context, SettingsThemeActivity::class.java)
                context.startActivity(intent)
            }
        )

        // also control the font size
        CustomActionRow(text = "Font size",
            leftIcon = Icons.Outlined.TextFields,
            onClick = {
                val intent = Intent(context, SettingsFontSizeActivity::class.java)
                context.startActivity(intent)
            }
        )

        // able to toggle on or off animations
        CustomActionRow(text = "Animations",
            leftIcon = Icons.Outlined.Animation,
            onClick = {
                val intent = Intent(context, SettingsAnimationActivity::class.java)
                context.startActivity(intent)
            }
        )

        // able to toggle on or off auto loading
        CustomActionRow(text = "Auto loading",
            leftIcon = Icons.Outlined.Refresh,
            onClick = {
                val intent = Intent(context, SettingsAutoLoadingActivity::class.java)
                context.startActivity(intent)
            }
        )

        // only available when the user is logged in
        // whether to send push notifications
        if (UserManager.isUserLoggedIn()) {
            CustomActionRow(text = "Push notifications",
                leftIcon = Icons.Outlined.Notifications,
                onClick = {
                    val intent = Intent(context, SettingsPushNotificationsActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun Permissions(
    context: Context
) {
    Box(
        modifier = Modifier.padding(
            horizontal = dimensionResource(id = R.dimen.content_margin)
        )
    ) {
        CustomGrayTitle(text = "Permissions")
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
    ) {
        CustomActionRow(text = "Manage app permissions",
            leftIcon = Icons.Outlined.Settings,
            onClick = {
                // redirect to the settings page of the os
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
            }
        )
    }
}

@Composable
fun AboutTheApp(
    context: Context
) {
    Box(
        modifier = Modifier.padding(
            horizontal = dimensionResource(id = R.dimen.content_margin)
        )
    ) {
        CustomGrayTitle(text = "About the app")
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
    ) {
        CustomActionRow(text = "About the app",
            leftIcon = Icons.Outlined.Info,
            onClick = {
                val i: Intent = Intent(
                    context,
                    AboutAppActivity::class.java
                )
                context.startActivity(i)
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

// admin settings
@Composable
fun Admin(
    context: Context,
    viewModel: SettingsViewModel
) {
    Box(
        modifier = Modifier.padding(
            horizontal = dimensionResource(id = R.dimen.content_margin)
        )
    ) {
        CustomGrayTitle(text = "Admin settings")
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
    ) {
        CustomActionRow(text = "View reported issues",
            leftIcon = Icons.Outlined.Report,
            onClick = {
                val i: Intent = Intent(context, ViewReportedIssuesActivity::class.java)
                context.startActivity(i)
            }
        )

        CustomActionRow(text = "View reported users",
            leftIcon = Icons.Outlined.Report,
            onClick = {
                val i: Intent = Intent(context, ViewReportedUsersActivity::class.java)
                context.startActivity(i)
            }
        )
    }
}

// developer settings
@Composable
fun Developer(
    context: Context,
    viewModel: SettingsViewModel
) {
    Box(
        modifier = Modifier.padding(
            horizontal = dimensionResource(id = R.dimen.content_margin)
        )
    ) {
        CustomGrayTitle(text = "Developer settings")
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
    ) {
        CustomActionRow(text = "Delete shared preferences data",
            leftIcon = Icons.Outlined.Delete,
            onClick = {
                // clear shared preferences from each key defined in the names file
                val sp: SharedPreferences = context.getSharedPreferences(
                    SharedPreferencesNames.NAME_USERS,
                    Context.MODE_PRIVATE
                )
                sp.edit().clear().apply()

                Toast.makeText(context, "Shared preferences cleared", Toast.LENGTH_SHORT).show()
            }
        )

        CustomActionRow(text = "Delete all chats data",
            leftIcon = Icons.Outlined.Delete,
            onClick = {
                val db = FirebaseFirestore.getInstance()
                // get all
                db.collection(FirebaseNames.COLLECTION_CHATS)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // delete each reference of the collection
                            val batch = db.batch()
                            for ((_, item) in task.result.withIndex()) {
                                batch.delete(item.reference)
                            }

                            // commit the batch
                            batch.commit().addOnCompleteListener { result ->
                                if (result.isSuccessful) {
                                    // get all chat inboxes also
                                    db.collection(FirebaseNames.COLLECTION_CHAT_INBOXES)
                                        .get()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // delete each reference of the collection
                                                val batch2 = db.batch()
                                                for ((_, item) in task.result.withIndex()) {
                                                    batch2.delete(item.reference)
                                                }

                                                // commit the batch
                                                batch2.commit().addOnCompleteListener { result ->
                                                    if (result.isSuccessful) {
                                                        Toast.makeText(
                                                            context,
                                                            "Deleted chat successfully",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Delete chat failed",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Delete chat failed",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Delete chat failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Delete chat failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        )

        CustomActionRow(text = "Delete all lost item data",
            leftIcon = Icons.Outlined.Delete,
            onClick = {
                val db = FirebaseFirestore.getInstance()
                // get all
                db.collection(FirebaseNames.COLLECTION_LOST_ITEMS)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // delete each reference of the collection
                            val batch = db.batch()
                            for ((_, item) in task.result.withIndex()) {
                                batch.delete(item.reference)
                            }

                            // commit the batch
                            batch.commit().addOnCompleteListener { result ->
                                if (result.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Deleted lost items successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Delete lost items failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Delete lost items failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        )

        CustomActionRow(text = "Delete all found item data",
            leftIcon = Icons.Outlined.Delete,
            onClick = {
                val db = FirebaseFirestore.getInstance()
                // get all
                db.collection(FirebaseNames.COLLECTION_FOUND_ITEMS)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // delete each reference of the collection
                            val batch = db.batch()
                            for ((_, item) in task.result.withIndex()) {
                                batch.delete(item.reference)
                            }

                            // commit the batch
                            batch.commit().addOnCompleteListener { result ->
                                if (result.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Deleted found items successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Delete found items failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Delete found items failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        )

        CustomActionRow(text = "Delete all claims data",
            leftIcon = Icons.Outlined.Delete,
            onClick = {
                val db = FirebaseFirestore.getInstance()
                // get all
                db.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // delete each reference of the collection
                            val batch = db.batch()
                            for ((_, item) in task.result.withIndex()) {
                                batch.delete(item.reference)
                            }

                            // commit the batch
                            batch.commit().addOnCompleteListener { result ->
                                if (result.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Deleted claims successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Delete claims failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Delete claims failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        )

        CustomActionRow(text = "Delete all notifications data",
            leftIcon = Icons.Outlined.Delete,
            onClick = {
                val db = FirebaseFirestore.getInstance()
                // get all
                db.collection(FirebaseNames.COLLECTION_NOTIFICATIONS)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // delete each reference of the collection
                            val batch = db.batch()
                            for ((_, item) in task.result.withIndex()) {
                                batch.delete(item.reference)
                            }

                            // commit the batch
                            batch.commit().addOnCompleteListener { result ->
                                if (result.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Deleted notifications successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Delete notifications failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Delete notifications failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        )

        CustomActionRow(text = "Open comparison activity",
            leftIcon = Icons.Outlined.Compare,
            onClick = {
                val i: Intent = Intent(context, ViewComparisonActivity::class.java)
                i.putExtra(
                    IntentExtraNames.INTENT_LOST_ITEM,
                    viewModel.placeholderLostItem
                )
                i.putExtra(
                    IntentExtraNames.INTENT_FOUND_ITEM,
                    viewModel.placeholderFoundItem
                )
                context.startActivity(i)
            }
        )

        CustomActionRow(text = "Open search activity",
            leftIcon = Icons.Outlined.Search,
            onClick = {
                val i: Intent = Intent(context, SearchActivity::class.java)
                i.putExtra(
                    IntentExtraNames.INTENT_LOST_ITEM,
                    viewModel.placeholderLostItem
                )
                context.startActivity(i)
            }
        )

        CustomActionRow(text = "Open done activity",
            leftIcon = Icons.Outlined.CheckCircle,
            onClick = {
                val i: Intent = Intent(context, DoneActivity::class.java)
                i.putExtra(
                    IntentExtraNames.INTENT_DONE_ACTIVITY_TITLE,
                    "Item posted!"
                )
                context.startActivity(i)
            }
        )


        CustomActionRow(text = "Open image comparison activity",
            leftIcon = Icons.Outlined.Image,
            onClick = {
                val i: Intent = Intent(context, ImageComparisonActivity::class.java)
                context.startActivity(i)
            }
        )

        CustomActionRow(text = "Open item comparison activity",
            leftIcon = Icons.Outlined.Compare,
            onClick = {
                val i: Intent = Intent(context, ItemComparisonActivity::class.java)
                context.startActivity(i)
            }
        )

        CustomActionRow(text = "Open permissions test activity",
            leftIcon = Icons.Outlined.LocationOn,
            onClick = {
                val i: Intent = Intent(context, PermissionsTestActivity::class.java)
                context.startActivity(i)
            }
        )

        CustomActionRow(text = "Show welcome message",
            leftIcon = Icons.Outlined.LocationOn,
            onClick = {
                val sp: SharedPreferences = context.getSharedPreferences(
                    SharedPreferencesNames.NAME_SHOW_WELCOME_MESSAGE,
                    Context.MODE_PRIVATE
                )
                sp.edit().putBoolean(SharedPreferencesNames.SHOW_WELCOME_MESSAGE_VALUE, true)
                    .apply()

                Toast.makeText(context, "Settings set", Toast.LENGTH_SHORT).show()
            }
        )

        CustomActionRow(text = "Email sending test",
            leftIcon = Icons.Outlined.Email,
            onClick = {
                val i: Intent = Intent(context, EmailSenderTestActivity::class.java)
                context.startActivity(i)
            }
        )

        CustomActionRow(text = "Push notification test",
            leftIcon = Icons.Outlined.Notifications,
            onClick = {
                val i: Intent = Intent(context, PushNotificationsTestActivity::class.java)
                context.startActivity(i)
            }
        )
    }
}
