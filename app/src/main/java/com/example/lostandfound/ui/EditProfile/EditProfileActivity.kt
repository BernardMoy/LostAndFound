package com.example.lostandfound.ui.EditProfile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.lostandfound.R
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import com.example.lostandfound.backToolbar


class EditProfileActivity : ComponentActivity() { // Use ComponentActivity here
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditProfileScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    EditProfileScreen(activity = MockActivity())
}

@Composable
fun EditProfileScreen(activity: ComponentActivity){
    Surface {
        Scaffold(
            // top toolbar
            topBar = {
                backToolbar(title = "Edit Profile", activity = activity)
            }
        ){ innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = innerPadding)
            ) {
                // content goes here
                IntroText()
                incrementButton()
            }
        }
    }
}

@Composable
fun IntroText() {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()   // padding for entire screen
    ) {

        Text(text = "Hello Compose",
            color = Color.Blue,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Start)
        )

        Text(text = "Hello Compose 2",
            color = colorResource(id = R.color.primaryColor),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End)
        )
    }

}

@Composable
fun incrementButton(){
    val context = LocalContext.current   // get context inside compose
    var count by remember{
        mutableIntStateOf(0)
    }       // var x by -> No need .value

    Column(
        modifier = Modifier.fillMaxSize()
    ){
        // text showing number
        Text(
            text = count.toString(),
            fontSize = dimensionResource(id = R.dimen.title_font_size).value.sp
        )
        // button to increment number
        Button(onClick = {
            Toast.makeText(context, "Incremented", Toast.LENGTH_SHORT).show()
            count++
        }) {
            // properties of button here
            Text(text = "Increment!")
        }
    }

}

@Composable
fun BackgroundImage(){
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        items(30){
            Image(
                painter = painterResource(id = R.drawable.placeholder),
                contentDescription = "Placeholder image"
            )
        }
    }
}
