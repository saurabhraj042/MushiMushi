package net.raj.mushimushi.ui

import com.google.firebase.firestore.CollectionReference
import net.raj.mushimushi.daos.CommentsDao
import net.raj.mushimushi.daos.PostDao
import net.raj.mushimushi.daos.UserDao
import net.raj.mushimushi.models.User

class Repository {
    private val userDao = UserDao()
    private val postDao = PostDao()
    private val commentsDao = CommentsDao()

    suspend fun addComment(text: String,postId: String){
        commentsDao.addComment(text, postId)
    }

    fun getCommentCollection(): CollectionReference {
        return commentsDao.getCommentCollection()
    }
    suspend fun addUser(user: User){
        userDao.addUser(user)
    }

    fun getUserById(uId : String){
        userDao.getUserById(uId)
    }

    suspend fun addPost(text : String){
        postDao.addPost(text)
    }

    fun getPostCollectionReference(): CollectionReference {
        return postDao.getPostCollectionReference()
    }

    suspend fun updatePostReaction(postId : String,type : String){
        postDao.updateReaction(postId,type)
    }

}