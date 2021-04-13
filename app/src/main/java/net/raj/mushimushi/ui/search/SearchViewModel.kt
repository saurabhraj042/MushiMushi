package net.raj.mushimushi.ui.search

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
import timber.log.Timber

class SearchViewModel : ViewModel() {
    private val repository = Repository()

    fun setupQuery(textForSearch: String): Query {

        return if (textForSearch.isEmpty()) {
            getPostCollectionRef()
                .orderBy("createdAt", Query.Direction.DESCENDING)
        } else {
            Timber.d("Searching for $textForSearch")
            getPostCollectionRef()
                .whereGreaterThanOrEqualTo("textBody", textForSearch)
                .whereLessThanOrEqualTo("textBody", textForSearch + '\uf8ff')
        }
    }

    private fun getPostCollectionRef(): CollectionReference {
        return repository.getPostCollectionReference()
    }

    fun updatePostReaction(postId: String, type: String) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.updatePostReaction(postId, type)
        }
    }

    fun deletePost(postId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.deletePost(postId)
        }
    }

    fun onSavePostByUser(postId: String) {
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