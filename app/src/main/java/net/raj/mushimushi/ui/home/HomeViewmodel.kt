package net.raj.mushimushi.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import net.raj.mushimushi.models.User
import net.raj.mushimushi.ui.Repository

class HomeViewModel() : ViewModel(){



    lateinit var query: Query
    lateinit var postCollection: CollectionReference
    val numberOfPosts : MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    var firebaseUser : FirebaseUser? = null
    var auth: FirebaseAuth = Firebase.auth
    private val repository = Repository()

    fun addUser(user : FirebaseUser?){
        user?.let {
            val currentUserId = user.email!!.toString()
            val firebaseUser = User(currentUserId,user.displayName,user.photoUrl!!.toString())

            GlobalScope.launch(Dispatchers.IO) {
                repository.addUser(firebaseUser)
            }
        }
    }

    fun getUserByID(uId : String){
        repository.getUserById(uId)
    }

    fun addPost(text : String){
        GlobalScope.launch(Dispatchers.IO){
            repository.addPost(text)
        }
    }

    fun getPostCollectionReference(): CollectionReference {
         return repository.getPostCollectionReference()
    }

    fun updateUtilities(){
        postCollection = repository.getPostCollectionReference()
        query = postCollection.orderBy("createdAt", Query.Direction.DESCENDING)
    }

    fun updatePostReaction(postId : String,type : String){
        GlobalScope.launch(Dispatchers.IO) {
            repository.updatePostReaction(postId,type)
        }
    }

    fun deletePost(postId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.deletePost(postId)
        }
    }

    fun commentCount(postId: String): String {
        var ans = ""
        GlobalScope.launch(Dispatchers.IO) {
            val commentsOnPost = repository.getCommentCollection().whereEqualTo("postId",postId).get().await()

            ans = commentsOnPost?.size()?.toString() ?: "0"

        }

        Log.d("Comment",ans)
        return ans
    }

}