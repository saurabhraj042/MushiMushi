package net.raj.mushimushi.daos

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import net.raj.mushimushi.models.Comments

class CommentsDao {
    private val db = FirebaseFirestore.getInstance()
    private val commentsCollection  = db.collection("comments")
    private val auth = Firebase.auth

    suspend fun addComment(text : String,postId : String){
        val time = System.currentTimeMillis()
        val userDao = UserDao()
        val user = userDao.getUserById(auth.currentUser.email).await().toObject(net.raj.mushimushi.models.User::class.java)!!
        val comment = Comments(text,time,user,postId)
        commentsCollection.document().set(comment)
    }

    fun getCommentCollection(): CollectionReference {
        return commentsCollection
    }
}