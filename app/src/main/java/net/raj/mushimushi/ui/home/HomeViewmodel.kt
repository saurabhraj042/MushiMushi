package net.raj.mushimushi.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import net.raj.mushimushi.models.Post
import net.raj.mushimushi.models.User
import net.raj.mushimushi.ui.Repository
import timber.log.Timber

class HomeViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    var auth: FirebaseAuth = Firebase.auth
    private val repository = Repository()
    lateinit var query: Query
    private lateinit var postCollection: CollectionReference
    var firebaseUser: FirebaseUser? = null
    var postCount: Long =
        if (savedStateHandle.get<Long>("postCount") == null) 0 else savedStateHandle.get<Long>("postCount")!!
    var isNewPost: MutableLiveData<Boolean> = MutableLiveData(false)





    fun newPostsListener() {
        val postCountCollection = repository.getPostCountCollection()

        GlobalScope.launch(Dispatchers.IO) {
            postCountCollection.document("COUNT")
                .addSnapshotListener { value, error ->
                    error?.let {
                        Timber.d(error)
                    }

                    value?.let { DocumentSnapshot ->
                        val newPostCount: Long? = DocumentSnapshot.get("postCount") as Long?
                        newPostCount?.let {
                            if (it > postCount) {
                                Timber.d("NEW Post")
                                isNewPost.value = true
                            }
                            savedStateHandle["postCount"] = newPostCount
                            postCount = it
                        }
                    }
                }
        }
    }

    fun notificationListener() {
        GlobalScope.launch(Dispatchers.IO) {
            val currentUserId = auth.currentUser!!.email
            val userObject = getUser(currentUserId!!).await().toObject(User::class.java)
            val notificationCollection = getNotificationCollectionRef()

            notificationCollection
                .whereEqualTo("parentPostOwner", userObject)
                .addSnapshotListener { snapshot, e ->
                    e?.let {
                        Timber.d("Listen failed.")
                        return@addSnapshotListener
                    }

                    for (dc in snapshot!!.documentChanges) {

                        if (dc.type == DocumentChange.Type.ADDED) {
                            // Not Working for now :(

                            break
                        }
                    }
                }
        }
    }


    private fun getNotificationCollectionRef(): CollectionReference {
        return repository.getNotificationCollection()
    }

    private fun getUser(id: String): Task<DocumentSnapshot> {
        return repository.getUserById(id)
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
            repository.updatePostCount("add")
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
            repository.updatePostCount("del")
            repository.deletePost(postId)
            repository.deleteComments(postId)
        }
    }

    fun onSavePostBTClicked(postId: String) {
        val currentUserId = Firebase.auth.currentUser!!.uid
        val postCollection = repository.getPostCollectionReference()
        GlobalScope.launch(Dispatchers.IO) {
            val isSavedByUser = postCollection.document(postId).get().await()
                .toObject(Post::class.java)!!.savedByUsers.contains(currentUserId)
            if (isSavedByUser) {
                repository.unSavePostForUser(postId)
            } else {
                repository.savePostForUser(postId)
            }
        }
    }


}