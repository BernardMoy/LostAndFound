package com.example.lostandfound.ui.Notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.CustomActionText
import com.example.lostandfound.CustomElements.CustomCenterText
import com.example.lostandfound.CustomElements.CustomCenteredProgressbar
import com.example.lostandfound.CustomElements.CustomNotificationItemPreview
import com.example.lostandfound.CustomElements.CustomTooltipBox
import com.example.lostandfound.Data.Claim
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.ScoreData
import com.example.lostandfound.FirebaseManagers.ItemManager
import com.example.lostandfound.R
import com.example.lostandfound.ui.ViewClaim.ViewClaimActivity
import com.example.lostandfound.ui.ViewComparison.ViewComparisonActivity
import com.example.lostandfound.ui.theme.ComposeTheme

class NotificationsActivity : ComponentActivity() { // Use ComponentActivity here

    val viewModel: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NotificationsScreen(activity = this, viewModel = viewModel)
        }
    }

    override fun onResume() {
        super.onResume()

        // load notifications on resume
        loadNotifications(context = this, viewModel = viewModel)
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    NotificationsScreen(activity = MockActivity(), viewModel = viewModel(), isTesting = false)
}

@Composable
fun NotificationsScreen(
    activity: ComponentActivity,
    viewModel: NotificationsViewModel,
    isTesting: Boolean = false
) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "Notifications", activity = activity)
                }
            ) { innerPadding ->
                val context: Context = LocalContext.current
                if (isTesting) {
                    LaunchedEffect(Unit) {
                        loadNotifications(context, viewModel)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues = innerPadding)
                ) {

                    Box(
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.title_margin))
                    ) {
                        if (viewModel.isItemsLoading.value) {
                            CustomCenteredProgressbar()
                        } else if (viewModel.itemsNotificationList.isEmpty()) {
                            CustomCenterText(text = "You have no notifications.")
                        } else {
                            Items(context = context, viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}


// tabs are no longer shown
// initially new messages are also shown here
/*
@Composable
fun Tabs(viewModel: NotificationsViewModel){
    val context: Context = LocalContext.current

    val tabNames = listOf("Items", "Messages")

    // a variable to store the selected tab index (0 or 1)
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }

    // variable to store pager state
    var pagerState = rememberPagerState {
        tabNames.size    // set the initial amount of pages
    }

    // make the page content change with the tab selected
    // if the selected tab index changes, then scroll to the corresponding page
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    // if the pagerstate.currentPage changes, then change the selected index
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        // avoid updating the page index during scrolling
        // applies when there are more than 2 tabs
        if (!pagerState.isScrollInProgress){
            selectedTabIndex = pagerState.currentPage
        }
    }

    // store the tab row in a column
    Column (
        modifier = Modifier.fillMaxWidth()
    ){
        // displays the tab row
        TabRow(selectedTabIndex = selectedTabIndex) {
            // iterate over the list of tabNames by the index and their item
            tabNames.forEachIndexed{index, item ->

                val isSelected = index == selectedTabIndex

                // display the selected tab as primary color
                val tabColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray

                // properties of a tab
                Tab(
                    selected = isSelected,
                    onClick = {
                        selectedTabIndex = index  // change the selected index when clicked

                        // depending on the index, turn off its unread button
                        if (index == 0){
                            viewModel.isItemsUnread.value = false
                        }
                        if (index == 1){
                            viewModel.isMessagesUnread.value = false
                        }

                              },
                    text = {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                        ){
                            Text(
                                text = item,
                                color = tabColor,
                                style = Typography.bodyMedium,
                            )

                            // the unread red dot
                            if (index == 0){
                                if (viewModel.isItemsUnread.value){
                                    Icon(
                                        imageVector = Icons.Filled.Circle,
                                        tint = MaterialTheme.colorScheme.error,
                                        contentDescription = "Unread",
                                        modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                                    )
                                }
                            }

                            if (index == 1){
                                if (viewModel.isMessagesUnread.value){
                                    Icon(
                                        imageVector = Icons.Filled.Circle,
                                        tint = MaterialTheme.colorScheme.error,
                                        contentDescription = "Unread",
                                        modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                                    )
                                }
                            }

                        }
                    }
                )
            }
        }

        // displays the content below tab row
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.Top

        ) { index ->
            Box(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.title_margin))
            ){
                if (index == 0){
                    // Items tab
                    if (viewModel.isItemsLoading.value){
                        CustomCenteredProgressbar()
                    } else if (viewModel.itemsNotificationList.isEmpty()){
                        CustomCenterText(text = "You have no notifications.")
                    } else {
                        Items(context = context, viewModel = viewModel)
                    }


                } else {
                    // messages tab
                    if (viewModel.isItemsLoading.value){
                        CustomCenteredProgressbar()
                    } else if (viewModel.itemsNotificationList.isEmpty()){
                        CustomCenterText(text = "You have no notifications.")
                    } else {
                        Messages(context = context, viewModel = viewModel)
                    }
                }
            }
        }
    }
}

 */

@Composable
fun Items(
    context: Context,
    viewModel: NotificationsViewModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
    ) {
        // a text for marking all notifications as read
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // the action text to mark all as read
            Box(
                modifier = Modifier.weight(1f)
            ) {
                CustomActionText(
                    text = "Mark all as read",
                    onClick = {
                        viewModel.markAllAsRead(object : LoadNotificationsCallback {
                            override fun onComplete(result: Boolean) {
                                if (!result) {
                                    Toast.makeText(
                                        context,
                                        "Failed to mark all notifications as read",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return
                                }

                                // when success, reload the screen
                                loadNotifications(context, viewModel)
                            }
                        })
                    }
                )
            }


            // the refresh button
            CustomTooltipBox(text = "Refresh") {
                IconButton(
                    onClick = {
                        // refresh the list - manually (by now)
                        loadNotifications(context, viewModel)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Reload",
                        modifier = Modifier.size(dimensionResource(id = R.dimen.image_button_size)),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

        }


        LazyColumn(
            // it is assigned all the remaining height from the MainContent() composable
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
        ) {
            // for each preview in the view model, display it
            items(viewModel.itemsNotificationList) { notification ->
                // display different notifications type
                val type = (notification[FirebaseNames.NOTIFICATION_TYPE] as Long).toInt()
                val timeStamp = notification[FirebaseNames.NOTIFICATION_TIMESTAMP] as Long
                val notificationID = notification[FirebaseNames.NOTIFICATION_ID].toString()
                val isRead = notification[FirebaseNames.NOTIFICATION_IS_READ] as Boolean

                when (type) {
                    0 -> CustomNotificationItemPreview(
                        type = 0,
                        notificationID = notificationID,
                        timestamp = timeStamp,
                        isRead = isRead,
                        testTag = "notification_$notificationID",
                        onClick = {
                            val lostItemID =
                                notification[FirebaseNames.NOTIFICATION_LOST_ITEM_ID].toString()
                            val foundItemID =
                                notification[FirebaseNames.NOTIFICATION_FOUND_ITEM_ID].toString()

                            // get scores
                            val passedScoreData = ScoreData(
                                imageScore = notification[FirebaseNames.NOTIFICATION_IMAGE_SCORE] as? Double,
                                categoryScore = notification[FirebaseNames.NOTIFICATION_CATEGORY_SCORE] as? Double
                                    ?: 0.0,
                                colorScore = notification[FirebaseNames.NOTIFICATION_COLOR_SCORE] as? Double
                                    ?: 0.0,
                                brandScore = notification[FirebaseNames.NOTIFICATION_BRAND_SCORE] as? Double,
                                locationScore = notification[FirebaseNames.NOTIFICATION_LOCATION_SCORE] as? Double,
                                overallScore = notification[FirebaseNames.NOTIFICATION_OVERALL_SCORE] as? Double
                                    ?: 0.0
                            )

                            ItemManager.getLostItemFromId(
                                lostItemID = lostItemID,
                                object : ItemManager.LostItemCallback {
                                    override fun onComplete(lostItem: LostItem?) {
                                        if (lostItem == null) {
                                            Toast.makeText(
                                                context,
                                                "The lost item no longer exists",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return
                                        }

                                        ItemManager.getFoundItemFromId(
                                            foundItemID,
                                            object : ItemManager.FoundItemCallback {
                                                override fun onComplete(foundItem: FoundItem?) {
                                                    if (foundItem == null) {
                                                        Toast.makeText(
                                                            context,
                                                            "The found item no longer exists",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        return
                                                    }
                                                    // get claim if status != 0
                                                    if (lostItem.status == 1 || lostItem.status == 2) {
                                                        ItemManager.getClaimFromLostId(
                                                            lostItemID,
                                                            object : ItemManager.LostClaimCallback {
                                                                override fun onComplete(claim: Claim?) {
                                                                    if (claim == null) {
                                                                        Toast.makeText(
                                                                            context,
                                                                            "Failed to claim data",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                        return
                                                                    }
                                                                    // start view comparison activity here
                                                                    val intent = Intent(
                                                                        context,
                                                                        ViewComparisonActivity::class.java
                                                                    )
                                                                    // pass both the lost item and found item
                                                                    intent.putExtra(
                                                                        IntentExtraNames.INTENT_LOST_ITEM,
                                                                        lostItem
                                                                    )
                                                                    intent.putExtra(
                                                                        IntentExtraNames.INTENT_FOUND_ITEM,
                                                                        foundItem
                                                                    )
                                                                    // also pass the claim item of the lost item
                                                                    intent.putExtra(
                                                                        IntentExtraNames.INTENT_CLAIM_ITEM,
                                                                        claim
                                                                    )
                                                                    // and pass the score data
                                                                    intent.putExtra(
                                                                        IntentExtraNames.INTENT_SCORE_DATA,
                                                                        passedScoreData
                                                                    )
                                                                    context.startActivity(intent)
                                                                }
                                                            })
                                                    } else {
                                                        val intent = Intent(
                                                            context,
                                                            ViewComparisonActivity::class.java
                                                        )
                                                        // pass both the lost item and found item
                                                        intent.putExtra(
                                                            IntentExtraNames.INTENT_LOST_ITEM,
                                                            lostItem
                                                        )
                                                        intent.putExtra(
                                                            IntentExtraNames.INTENT_FOUND_ITEM,
                                                            foundItem
                                                        )
                                                        // also pass the claim item of the lost item
                                                        intent.putExtra(
                                                            IntentExtraNames.INTENT_CLAIM_ITEM,
                                                            Claim()  // no claim data
                                                        )
                                                        // and pass the score data
                                                        intent.putExtra(
                                                            IntentExtraNames.INTENT_SCORE_DATA,
                                                            passedScoreData
                                                        )
                                                        context.startActivity(intent)
                                                    }
                                                }
                                            })
                                    }
                                })
                        }
                    )

                    1 -> CustomNotificationItemPreview(
                        type = 1,
                        notificationID = notificationID,
                        timestamp = timeStamp,
                        isRead = isRead,
                        testTag = "notification_$notificationID",
                        onClick = {
                            val claimID =
                                notification[FirebaseNames.NOTIFICATION_CLAIM_ID] as String

                            // pass the claim ITEM
                            ItemManager.getClaimFromClaimId(
                                claimID,
                                object : ItemManager.ClaimCallback {
                                    override fun onComplete(claim: Claim?) {
                                        if (claim == null) {
                                            Toast.makeText(
                                                context,
                                                "Failed to view claim",     // claims cannot be deleted, so they should not doesnt exist
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return
                                        }

                                        // start view claim activity
                                        val intent: Intent =
                                            Intent(context, ViewClaimActivity::class.java)
                                        intent.putExtra(IntentExtraNames.INTENT_CLAIM_ITEM, claim)
                                        context.startActivity(intent)
                                    }
                                })
                        }
                    )

                    2 -> CustomNotificationItemPreview(
                        type = 2,
                        notificationID = notificationID,
                        timestamp = timeStamp,
                        isRead = isRead,
                        testTag = "notification_$notificationID",
                        onClick = {
                            val claimID =
                                notification[FirebaseNames.NOTIFICATION_CLAIM_ID] as String

                            // pass the claim ITEM
                            ItemManager.getClaimFromClaimId(
                                claimID,
                                object : ItemManager.ClaimCallback {
                                    override fun onComplete(claim: Claim?) {
                                        if (claim == null) {
                                            Toast.makeText(
                                                context,
                                                "Failed to view claim",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return
                                        }

                                        // start view claim activity
                                        val intent: Intent =
                                            Intent(context, ViewClaimActivity::class.java)
                                        intent.putExtra(IntentExtraNames.INTENT_CLAIM_ITEM, claim)
                                        context.startActivity(intent)
                                    }
                                })
                        }
                    )

                    3 -> {
                        CustomNotificationItemPreview(
                            type = 3,
                            notificationID = notificationID,
                            timestamp = timeStamp,
                            isRead = isRead,
                            testTag = "notification_$notificationID",
                            onClick = {
                                val claimID =
                                    notification[FirebaseNames.NOTIFICATION_CLAIM_ID] as String

                                // pass the claim ITEM
                                ItemManager.getClaimFromClaimId(
                                    claimID,
                                    object : ItemManager.ClaimCallback {
                                        override fun onComplete(claim: Claim?) {
                                            if (claim == null) {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to view claim",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return
                                            }

                                            // start view claim activity
                                            val intent: Intent =
                                                Intent(context, ViewClaimActivity::class.java)
                                            intent.putExtra(
                                                IntentExtraNames.INTENT_CLAIM_ITEM,
                                                claim
                                            )
                                            context.startActivity(intent)
                                        }
                                    })
                            }
                        )
                    }
                }
            }
        }
    }


}

@Composable
fun Messages(
    context: Context,
    viewModel: NotificationsViewModel
) {
    Text(text = "Messages")

}

// function to load notifications on create
fun loadNotifications(
    context: Context,
    viewModel: NotificationsViewModel
) {
    viewModel.isItemsLoading.value = true

    viewModel.loadItemsNotifications(object : LoadNotificationsCallback {
        override fun onComplete(result: Boolean) {
            viewModel.isItemsLoading.value = false

            if (!result) {
                Toast.makeText(context, "Load notifications failed", Toast.LENGTH_SHORT).show()
                return
            }
            // do nothing when successful
        }
    })
}