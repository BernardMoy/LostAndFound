package com.example.lostandfound.ui.ReportIssue

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Utility.ErrorCallback
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.FirestoreManager
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

    // add the issue to the database when done button is clicked
    fun onDoneButtonClicked(callback: ErrorCallback){

        // validate input
        if (!validateInput()) {
            return
        }

        // add the issue to database with the user id
        val data = mapOf(
            FirebaseNames.REPORT_ISSUE_USER to FirebaseUtility.getUserID(),
            FirebaseNames.REPORT_ISSUE_DESC to description.value
        )

        val manager = FirestoreManager()
        manager.putWithUniqueId(FirebaseNames.COLLECTION_REPORT_ISSUE, data, object: FirestoreManager.Callback<String>{
            override fun onComplete(result: String) {
                if (result.isEmpty()){
                    callback.onComplete("Error adding item to database")
                    return
                }

                callback.onComplete("")   // return no errors
            }
        })

    }
}