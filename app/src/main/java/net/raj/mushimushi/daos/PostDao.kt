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


        when (type) {
            "like" -> {
                val isLiked = post.likes.contains(currentUserId)

                if (isLiked) {
                    post.likes.remove(currentUserId)
                } else {
                    post.likes.add(currentUserId)
                }
            }

            "haha" -> {
                val isClicked = post.haHaReaction.contains(currentUserId)
                if (isClicked) {
                    post.haHaReaction.remove(currentUserId)
                } else {
                    post.haHaReaction.add(currentUserId)
                }
            }

            "sad" -> {
                val isClicked = post.sadReaction.contains(currentUserId)
                if (isClicked) {
                    post.sadReaction.remove(currentUserId)
                } else {
                    post.sadReaction.add(currentUserId)
                }
            }

            "angry" -> {
                val isClicked = post.angryReaction.contains(currentUserId)
                if (isClicked) {
                    post.angryReaction.remove(currentUserId)
                } else {
                    post.angryReaction.add(currentUserId)
                }
            }
        }

        postCollection.document(postId).set(post)


    }

    fun deletePost(postId: String) {
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