package com.example.lostandfound.ui.Done

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.MainActivity
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography


class DoneActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create the view model here
        val viewModel: DoneViewModel by viewModels()

        // load the passed intent data into the view model
        val passedItem = intent.getStringExtra(IntentExtraNames.INTENT_DONE_ACTIVITY_TITLE)
        if (passedItem != null) {
            viewModel.titleText = passedItem
        }

        setContent {
            DoneScreen(activity = this, viewModel = viewModel)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    DoneScreen(activity = MockActivity(), viewModel = viewModel())
}

@Composable
fun DoneScreen(activity: ComponentActivity, viewModel: DoneViewModel) {
    ComposeTheme {
        Surface {
            Scaffold { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues = innerPadding)
                ) {
                    // includes the top tab bar and the main content
                    MainContent()
                }
            }
        }
    }
}

// content includes avatar, edit fields, reminder message and save button
// get the view model in the function parameter
@Composable
fun MainContent(viewModel: DoneViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    // scale for the checkmark icon
    var originalScale by remember {
        mutableFloatStateOf(0.5f)
    }
    LaunchedEffect(Unit) {
        // when the activity is first launched, scale up the icon
        originalScale = 1.0f
    }
    val animatedScale by animateFloatAsState(
        targetValue = originalScale,
        animationSpec = tween(durationMillis = 1500),
        label = "iconScale"
    )

    // the big background
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                ),
            )
    ) {
        val iconWidth = maxWidth / 2
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.content_margin)),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "Done",
                    modifier = Modifier
                        .size(iconWidth)
                        .scale(animatedScale)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.title_margin)),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = viewModel.titleText,
                    style = Typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.header_margin)),
                horizontalArrangement = Arrangement.Center
            ) {
                // after clicking the done button, the user is always redirected to the main activity
                CustomButton(
                    text = "Done",
                    type = ButtonType.WHITE,
                    onClick = {
                        /*
                        Navigate back to the home activity
                        Start the intent to home activity, then pop all previous activities from the stack
                        */
                        val intent = Intent(context, MainActivity::class.java).apply {
                            flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }

                        // start main activity
                        context.startActivity(intent)

                        // finish current activity
                        (context as Activity).finish()
                    }
                )
            }
        }
    }
}



