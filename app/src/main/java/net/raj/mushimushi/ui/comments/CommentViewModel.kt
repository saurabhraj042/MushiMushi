package net.raj.mushimushi.ui.comments

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.raj.mushimushi.ui.Repository

class CommentViewModel : ViewModel() {
    private val repository = Repository()
    val commentCollection = repository.getCommentCollection()

    fun addComment(text : String,postId : String){
        GlobalScope.launch(Dispatchers.IO){
            repository.addComment(text,postId)
        }
    }
}