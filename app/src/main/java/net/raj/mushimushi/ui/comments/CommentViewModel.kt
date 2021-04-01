package net.raj.mushimushi.ui.comments

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.raj.mushimushi.ui.Repository

class CommentViewModel : ViewModel() {

    private val repository = Repository()


    fun addComment(text: String, postId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.addComment(text, postId)
            repository.updateCommentCountOnPost(postId, "inc")
        }
    }

    fun getCommentCollectionRef(): CollectionReference {
        return repository.getCommentCollection()
    }

    fun updateReactionOnComment(docId: String, type: String) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.updateReactionOnComment(docId, type)
        }
    }

    fun deleteSingleComment(docId: String, postId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.deleteSingleComment(docId)
            repository.updateCommentCountOnPost(postId, "dec")
        }
    }
}