package net.raj.mushimushi.daos

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import net.raj.mushimushi.models.Notification
import net.raj.mushimushi.models.User

class NotificationDao {

    private val db = FirebaseFirestore.getInstance()
    private val notificationCollection = db.collection("notification")
    private val auth = Firebase.auth
    private val userDao = UserDao()

    suspend fun addNotification(postId: String, type: String, parentPostOwner: User) {
        val timestamp = System.currentTimeMillis()
        val currentUser = userDao.getUserById(auth.currentUser!!.email!!).await()
            .toObject(net.raj.mushimushi.models.User::class.java)!!

        val notification = Notification(postId, parentPostOwner, currentUser, type, timestamp)

        notificationCollection.document().set(notification)
    }

    fun getNotificationCollection(): CollectionReference {
        return notificationCollection
    }

    suspend fun deleteNotification(postId: String, type: String) {
        val currentUser = userDao.getUserById(auth.currentUser!!.email!!).await()
            .toObject(net.raj.mushimushi.models.User::class.java)!!

        notificationCollection.whereEqualTo("postId", postId)
            .whereEqualTo("type", type)
            .whereEqualTo("user", currentUser)
            .get().addOnSuccessListener {
                for (dc in it) {
                    dc.reference.delete()
                }
            }
    }

    suspend fun deleteAllNotificationsForPost(postId: String) {
        notificationCollection.whereEqualTo("postId", postId)
            .get().addOnSuccessListener {
                for (dc in it) {
                    dc.reference.delete()
                }
            }
    }


}