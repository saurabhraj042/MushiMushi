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

    suspend fun savePostForUser(postId: String){
        postDao.savePostForUser(postId)
    }

    suspend fun unSavePostForUser(postId: String){
        postDao.unSavePostForUser(postId)
    }

    suspend fun addComment(text: String, postId: String) {
        commentsDao.addComment(text, postId)
    }

    fun deleteComments(postId: String) {
        commentsDao.deleteComments(postId)
    }

    fun getCommentCollection(): CollectionReference {
        return commentsDao.getCommentCollection()
    }

    suspend fun updateReactionOnComment(docId: String, type: String) {
        commentsDao.updateReaction(docId, type)
    }

    suspend fun deleteSingleComment(docId: String) {
        commentsDao.deleteSingleComment(docId)
    }


    fun addUser(user: User) {
        userDao.addUser(user)
    }

    suspend fun addPost(text: String) {
        postDao.addPost(text)
    }

    fun getPostCollectionReference(): CollectionReference {
        return postDao.getPostCollectionReference()
    }

    suspend fun updatePostReaction(postId: String, type: String) {
        postDao.updateReaction(postId, type)
    }

    fun deletePost(postId: String) {
        postDao.deletePost(postId)
    }

    suspend fun updateCommentCountOnPost(postId: String, type: String) {
        postDao.updateCommentCount(postId, type)
    }


}