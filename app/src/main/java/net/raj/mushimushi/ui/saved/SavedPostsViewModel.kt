package net.raj.mushimushi.ui.saved

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import net.raj.mushimushi.models.Post
import net.raj.mushimushi.ui.Repository

class SavedPostsViewModel : ViewModel() {
    private val repository = Repository()
    private val currentUserId = Firebase.auth.currentUser!!.uid

    fun setupQuery(): Query {
        val query = getPostCollectionRef()
            .whereArrayContains("savedByUsers", currentUserId)
            .orderBy("createdAt", Query.Direction.DESCENDING)

        return query
    }

    fun getPostCollectionRef(): CollectionReference {
        return repository.getPostCollectionReference()
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

    fun deletePost(postId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.deletePost(postId)
            repository.deleteComments(postId)
        }
    }

    fun updatePostReaction(postId: String, type: String) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.updatePostReaction(postId, type)
        }
    }

}