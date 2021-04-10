package net.raj.mushimushi.ui

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import net.raj.mushimushi.daos.*
import net.raj.mushimushi.models.PostCount
import net.raj.mushimushi.models.User

class Repository {
    private val userDao = UserDao()
    private val postDao = PostDao()
    private val commentsDao = CommentsDao()
    private val notificationDao = NotificationDao()
    private val postCountDao = PostCountDao()


    suspend fun updatePostCount(type: String){
        postCountDao.updatePostCount(type)
    }

    fun getNotificationCollection(): CollectionReference {
        return notificationDao.getNotificationCollection()
    }


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

    suspend fun deletePost(postId: String) {
        postDao.deletePost(postId)
    }

    suspend fun updateCommentCountOnPost(postId: String, type: String) {
        postDao.updateCommentCount(postId, type)
    }

    fun getUserById(id : String) : Task<DocumentSnapshot> {
        return userDao.getUserById(id)
    }

    fun getPostCountCollection(): CollectionReference {
        return postCountDao.getPostCountCollection()
    }

}