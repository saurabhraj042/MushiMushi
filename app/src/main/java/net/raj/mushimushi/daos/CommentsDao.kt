package net.raj.mushimushi.daos

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import net.raj.mushimushi.models.Comments
import net.raj.mushimushi.models.Post
import timber.log.Timber

class CommentsDao {
    private val db = FirebaseFirestore.getInstance()
    private val commentsCollection = db.collection("comments")
    private val notificationDao = NotificationDao()
    private val userDao = UserDao()
    private val postDao = PostDao()
    private val auth = Firebase.auth

    suspend fun addComment(text: String, postId: String) {
        val timestamp = System.currentTimeMillis()

        val user = userDao.getUserById(auth.currentUser!!.email!!).await()
            .toObject(net.raj.mushimushi.models.User::class.java)!!
        val comment = Comments(text, timestamp, user, postId)
        commentsCollection.document().set(comment)

        val post = postDao.getPostCollectionReference().document(postId).get().await()
            .toObject(Post::class.java)!!
        val isCurrentUserNotCommentOwner = comment.user != post.user
        if (isCurrentUserNotCommentOwner) {
            notificationDao.addNotification(postId, "comment", post.user)
        }

    }

    fun getCommentCollection(): CollectionReference {
        return commentsCollection
    }

    fun deleteComments(postId: String) {
        commentsCollection.whereEqualTo("postId", postId)
            .get()
            .addOnSuccessListener {
                for (dc in it) {
                    dc.reference.delete()
                }
            }
    }

    suspend fun updateReaction(docId: String, type: String) {
        val currentUserId = auth.currentUser!!.uid
        val comment =
            commentsCollection.document(docId).get().await().toObject(Comments::class.java)!!

        when (type) {
            "like" -> {
                val isLiked = comment.likes.contains(currentUserId)

                if (isLiked) {
                    comment.likes.remove(currentUserId)
                } else {
                    comment.likes.add(currentUserId)
                }
            }

            "dislike" -> {
                val isDisLiked = comment.dislikes.contains(currentUserId)

                if (isDisLiked) {
                    comment.dislikes.remove(currentUserId)
                } else {
                    comment.dislikes.add(currentUserId)
                }
            }

        }

        commentsCollection.document(docId).set(comment)
    }

    suspend fun deleteSingleComment(docId: String) {
        val comment =
            commentsCollection.document(docId).get().await().toObject(Comments::class.java)!!
        deleteCommentNotification(comment)
        commentsCollection.document(docId).delete()
    }

    private suspend fun deleteCommentNotification(comment: Comments) {
        val postId = comment.postId
        val user = comment.user.uid
        val currentUser = auth.currentUser!!.email
        val isCurrentUserNotCommentOwner = user != currentUser
        Timber.d(user)

        if (isCurrentUserNotCommentOwner) {
            notificationDao.deleteNotification(postId, "comment")
        }
    }

}