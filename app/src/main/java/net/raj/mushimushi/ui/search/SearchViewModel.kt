package net.raj.mushimushi.ui.search

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.raj.mushimushi.ui.Repository

class SearchViewModel : ViewModel() {
    private val repository = Repository()

    fun getPostCollectionRef(): CollectionReference {
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

}