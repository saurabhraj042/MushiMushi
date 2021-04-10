package net.raj.mushimushi.daos

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import net.raj.mushimushi.models.Post
import timber.log.Timber

class PostDao {
    private val db = FirebaseFirestore.getInstance()
    private val postCollection = db.collection("posts")
    private val auth = Firebase.auth
    private val userDao = UserDao()
    private val notificationDao = NotificationDao()

    suspend fun savePostForUser(postId: String){
        val currentUserId = auth.currentUser!!.uid
        val post = getPostById(postId).await().toObject(Post::class.java)!!
        post.savedByUsers.add(currentUserId)
        postCollection.document(postId).set(post)
    }

    suspend fun unSavePostForUser(postId: String){
        val currentUserId = auth.currentUser!!.uid
        val post = getPostById(postId).await().toObject(Post::class.java)!!
        post.savedByUsers.remove(currentUserId)
        postCollection.document(postId).set(post)
    }

    suspend fun addPost(text: String) {
        val user = userDao.getUserById(auth.currentUser!!.email!!).await()
            .toObject(net.raj.mushimushi.models.User::class.java)!!

        val currentTime = System.currentTimeMillis()
        val post = Post(auth.currentUser!!.email!!, user, text, currentTime)

        postCollection.document().set(post)

    }


    fun getPostCollectionReference(): CollectionReference {
        return postCollection
    }

    private fun getPostById(postId: String): Task<DocumentSnapshot> {
        return postCollection.document(postId).get()
    }

    suspend fun updateReaction(postId: String, type: String) {
        val currentUserId = auth.currentUser!!.uid
        val post = getPostById(postId).await().toObject(Post::class.java)!!
        val user = userDao.getUserById(auth.currentUser!!.email!!).await()
            .toObject(net.raj.mushimushi.models.User::class.java)!!
        val isCurrentUserNotPostOwner = post.user != user

        when (type) {
            "like" -> {
                val isLiked = post.likes.contains(currentUserId)

                if (isLiked) {
                    post.likes.remove(currentUserId)
                    if(isCurrentUserNotPostOwner){
                        notificationDao.deleteNotification(postId, type)
                    }

                } else {
                    post.likes.add(currentUserId)
                    if(isCurrentUserNotPostOwner){
                        notificationDao.addNotification(postId,type,post.user)
                    }
                }
            }

            "haha" -> {
                val isClicked = post.haHaReaction.contains(currentUserId)
                if (isClicked) {
                    post.haHaReaction.remove(currentUserId)
                    if(isCurrentUserNotPostOwner){
                        notificationDao.deleteNotification(postId, type)
                    }
                } else {
                    post.haHaReaction.add(currentUserId)
                    if(isCurrentUserNotPostOwner){
                        notificationDao.addNotification(postId,type,post.user)
                    }
                }
            }

            "sad" -> {
                val isClicked = post.sadReaction.contains(currentUserId)
                if (isClicked) {
                    post.sadReaction.remove(currentUserId)
                    if(isCurrentUserNotPostOwner){
                        notificationDao.deleteNotification(postId, type)
                    }
                } else {
                    post.sadReaction.add(currentUserId)
                    if(isCurrentUserNotPostOwner){
                        notificationDao.addNotification(postId,type,post.user)
                    }
                }
            }

            "angry" -> {
                val isClicked = post.angryReaction.contains(currentUserId)
                if (isClicked) {
                    notificationDao.deleteNotification(postId, type)
                    if(isCurrentUserNotPostOwner){
                        notificationDao.deleteNotification(postId, type)
                    }
                } else {
                    post.angryReaction.add(currentUserId)
                    if(isCurrentUserNotPostOwner){
                        notificationDao.addNotification(postId,type,post.user)
                    }
                }
            }
        }

        postCollection.document(postId).set(post)


    }

    suspend fun deletePost(postId: String) {
        notificationDao.deleteAllNotificationsForPost(postId)
        postCollection.document(postId).delete()
            .addOnSuccessListener {
                Timber.d("Post Deleted")
            }
    }

    suspend fun updateCommentCount(postId: String, type: String) {
        val post = postCollection.document(postId).get().await().toObject(Post::class.java)!!

        when (type) {
            "inc" -> {
                post.commentCount++
            }

            "dec" -> {
                post.commentCount--
            }
        }

        postCollection.document(postId).set(post)
    }
}