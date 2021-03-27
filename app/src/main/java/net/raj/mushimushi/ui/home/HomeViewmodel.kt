package net.raj.mushimushi.ui.home

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
import net.raj.mushimushi.models.User
import net.raj.mushimushi.ui.Repository

class HomeViewModel() : ViewModel(){

    lateinit var query: Query
    lateinit var postCollection: CollectionReference
    val firebaseUser : MutableLiveData<FirebaseUser?> by lazy { MutableLiveData<FirebaseUser?>() }
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

    fun getPostCollectionReference(){
        repository.getPostCollectionReference()
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

}