package com.example.lostandfound.ui.ViewReportedUsers

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.ReportedUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class ViewReportedUsersViewModel : ViewModel(){
    interface LoadReportedUsersCallback {
        fun onComplete(success: Boolean)
    }

    val reportedUserList: MutableList<ReportedUser> = mutableListOf()
    val isLoading: MutableState<Boolean> = mutableStateOf(true)

    fun loadData(callback: LoadReportedUsersCallback) {
        reportedUserList.clear()
        isLoading.value = true
        FirebaseFirestore.getInstance().collection(FirebaseNames.COLLECTION_REPORT_USERS).get()
            .addOnSuccessListener { doc ->
                val count = doc.size()
                var n = 0
                if (count == 0){
                    isLoading.value = false
                    callback.onComplete(true)
                }
                for (snapshot: QueryDocumentSnapshot in doc) {
                    val fromUser = snapshot[FirebaseNames.REPORT_USER_FROM].toString()
                    val fromUserFirstName = snapshot[FirebaseNames.REPORT_USER_FROM_FIRST_NAME].toString()
                    val fromUserLastName = snapshot[FirebaseNames.REPORT_USER_FROM_LAST_NAME].toString()
                    val fromUserEmail = snapshot[FirebaseNames.REPORT_USER_FROM_EMAIL].toString()
                    val toUser = snapshot[FirebaseNames.REPORT_USER_TO].toString()
                    val toUserFirstName = snapshot[FirebaseNames.REPORT_USER_TO_FIRST_NAME].toString()
                    val toUserLastName = snapshot[FirebaseNames.REPORT_USER_TO_LAST_NAME].toString()
                    val toUserEmail = snapshot[FirebaseNames.REPORT_USER_TO_EMAIL].toString()
                    val desc = snapshot[FirebaseNames.REPORT_USER_DESC].toString()


                    val item = ReportedUser(
                        fromUid = fromUser,
                        fromFirstName = fromUserFirstName,
                        fromLastName = fromUserLastName,
                        fromEmail = fromUserEmail,
                        toUid = toUser,
                        toFirstName = toUserFirstName,
                        toLastName = toUserLastName,
                        toEmail = toUserEmail,
                        description = desc
                    )
                    reportedUserList.add(item)

                    n += 1
                    if (n == count) {
                        isLoading.value = false
                        callback.onComplete(true)
                    }
                }
            }.addOnFailureListener { e ->
                Log.d("Firebase error", e.message.toString())
                isLoading.value = false
                callback.onComplete(false)
            }
    }
}