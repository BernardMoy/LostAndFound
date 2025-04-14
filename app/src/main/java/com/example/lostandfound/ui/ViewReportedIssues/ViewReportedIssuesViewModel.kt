package com.example.lostandfound.ui.ViewReportedIssues

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.ReportedIssue
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.FirebaseManagers.UserManager.GetUserCallback
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class ViewReportedIssuesViewModel : ViewModel() {

    interface LoadReportedIssuesCallback {
        fun onComplete(success: Boolean)
    }

    val reportedIssueList: MutableList<ReportedIssue> = mutableListOf()
    val isLoading: MutableState<Boolean> = mutableStateOf(true)

    fun loadData(callback: LoadReportedIssuesCallback) {
        reportedIssueList.clear()
        isLoading.value = true
        FirebaseFirestore.getInstance().collection(FirebaseNames.COLLECTION_REPORT_ISSUE).get()
            .addOnSuccessListener { doc ->
                val count = doc.size()
                var n = 0
                if (count == 0){
                    isLoading.value = false
                    callback.onComplete(true)
                }
                for (snapshot: QueryDocumentSnapshot in doc) {
                    val uid = snapshot[FirebaseNames.REPORT_ISSUE_USER].toString()
                    val desc = snapshot[FirebaseNames.REPORT_ISSUE_DESC].toString()

                    Log.d("USER", uid)

                    UserManager.getUserFromId(uid, object : GetUserCallback {
                        override fun onComplete(user: User?) {
                            if (user == null){
                                isLoading.value = false
                                callback.onComplete(false)
                            }

                            val item = ReportedIssue(
                                user = user ?: User(),
                                description = desc
                            )

                            reportedIssueList.add(item)
                            n += 1
                            if (n == count) {
                                isLoading.value = false
                                callback.onComplete(true)
                            }
                        }
                    })
                }
            }.addOnFailureListener { e ->
                Log.d("Firebase error", e.message.toString())
                isLoading.value = false
                callback.onComplete(false)
            }
    }
}