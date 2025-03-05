package com.example.lostandfound

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.ui.Search.SearchActivity
import com.example.lostandfound.ui.Search.SearchScreen
import com.example.lostandfound.ui.Search.SearchViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchActivityTest : FirebaseTestsSetUp() {
    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()

        private var viewModel: SearchViewModel = SearchViewModel()

        // create item details
        private val dataLost1 = LostItem(
            itemID = "2e9j8qijwqiie",
            userID = "92j9asdsd",
            itemName = "TestItem",
            category = "TestCat",
            subCategory = "TestSubCat",
            color = listOf("Black", "Red"),
            brand = "TestBrand",
            dateTime = 1738819980L,
            location = Pair(52.381162440739686, -1.5614377315953403),
            description = "TestDesc",
            isTracking = false,
            timePosted = 1739941511L
        )
    }

    @get:Rule
    val activityRule = ActivityScenarioRule(SearchActivity::class.java)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<SearchActivity>()

    @Before
    fun setUp() {
        // Create an Intent with extras
        activityRule.scenario.onActivity { activity ->
            val intent = Intent(ApplicationProvider.getApplicationContext(), SearchActivity::class.java).apply {
                putExtra(
                    IntentExtraNames.INTENT_LOST_ID,    // pass the lost item as intent extra 
                    dataLost1
                )
            }
            activity.intent.putExtras(intent.extras ?: Bundle())
        }
    }

    @Test
    fun testSearch() {
        activityRule.scenario.onActivity { activity ->
            // assert the intent of lost item passed in is correct
            Assert.assertEquals(
                dataLost1,
                activity.intent.getParcelableExtra(IntentExtraNames.INTENT_LOST_ID)
            )
        }
    }
}