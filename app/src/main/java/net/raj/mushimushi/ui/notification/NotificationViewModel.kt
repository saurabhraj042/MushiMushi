package net.raj.mushimushi.ui.notification

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import net.raj.mushimushi.ui.Repository

class NotificationViewModel : ViewModel() {
    lateinit var query: Query
    private lateinit var notificationCollection: CollectionReference
    private val repository = Repository()
    private val auth = Firebase.auth

    init {
        setupFireStoreQuery()
    }

    private fun setupFireStoreQuery() {
        notificationCollection = repository.getNotificationCollection()
        val currentUser = auth.currentUser!!.email!!
        query = notificationCollection
            .whereEqualTo("parentPostOwner.uid", currentUser)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
    }

}