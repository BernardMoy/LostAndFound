package com.example.lostandfound.ui.ReportIssue

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Utility.ErrorCallback
import com.example.lostandfound.FirebaseManagers.FirebaseStorageManager
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.Data.Category
import com.example.lostandfound.Data.Colors
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.Data.FirebaseNames

class ReportIssueViewModel: ViewModel() {
    val description: MutableState<String> = mutableStateOf("")
    val descriptionError: MutableState<String> = mutableStateOf("")

    fun onDescriptionChanged(s: String){
        description.value = s
    }

    fun validateInput(): Boolean {
        // initially reset
        descriptionError.value = ""

        if (description.value.isEmpty()){
            descriptionError.value = "Description cannot be empty"
            return false
        }

        return true
    }

    fun onDoneButtonClicked(){

    }
}