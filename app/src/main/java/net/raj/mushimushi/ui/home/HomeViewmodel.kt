package net.raj.mushimushi.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import net.raj.mushimushi.models.Post
import net.raj.mushimushi.models.User
import net.raj.mushimushi.ui.Repository

class HomeViewModel : ViewModel() {

    var auth: FirebaseAuth = Firebase.auth
    private val repository = Repository()
    private var _numberOfPosts: MutableLiveData<Int> = MutableLiveData(0)
    lateinit var query: Query
    private lateinit var postCollection: CollectionReference
    var numberOfPosts: LiveData<Int> = _numberOfPosts
    var firebaseUser: FirebaseUser? = null


    fun checkListSize(itemCount: Int) {
        if (itemCount > _numberOfPosts.value!!) {
            _numberOfPosts.value = itemCount
        }
    }


    fun addNewUser(user: FirebaseUser?) {
        user?.let {
            val firebaseUser =
                User(it.email!!.toString(), it.displayName!!, it.photoUrl!!.toString())

            GlobalScope.launch(Dispatchers.IO) {
                repository.addUser(firebaseUser)
            }
        }
    }

    fun addNewPost(text: String) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.addPost(text)
        }
    }

    fun updateUtilities() {
        postCollection = repository.getPostCollectionReference()
        query = postCollection.orderBy("createdAt", Query.Direction.DESCENDING)
    }

    fun updatePostReaction(postId: String, type: String) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.updatePostReaction(postId, type)
        }
    }

    fun deletePost(postId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.deletePost(postId)
            repository.deleteComments(postId)
        }
    }

    fun onSavePostBTClicked(postId: String) {
        val currentUserId = Firebase.auth.currentUser!!.uid
        val postCollection = repository.getPostCollectionReference()
        GlobalScope.launch(Dispatchers.IO) {
            val isSavedByUser = postCollection.document(postId).get().await().toObject(Post::class.java)!!.savedByUsers.contains(currentUserId)
            if (isSavedByUser){
                repository.unSavePostForUser(postId)
            }else{
                repository.savePostForUser(postId)
            }
        }
    }

}