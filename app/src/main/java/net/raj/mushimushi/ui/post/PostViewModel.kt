package net.raj.mushimushi.ui.post

import android.view.View
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import net.raj.mushimushi.databinding.FragmentPostBinding
import net.raj.mushimushi.models.Post
import net.raj.mushimushi.ui.Repository
import net.raj.mushimushi.ui.shared.Utils

class PostViewModel : ViewModel() {

    private val repository = Repository()
    private lateinit var commentCollection: CollectionReference

    fun addComment(text: String, postId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.addComment(text, postId)
            repository.updateCommentCountOnPost(postId, "inc")
        }
    }


    fun setCommentQuery(postId: String): Query {
        commentCollection = repository.getCommentCollection()

        return commentCollection.whereEqualTo(
            "postId",
            postId
        ).orderBy("createdAt", Query.Direction.ASCENDING)
    }

    fun getPost(postId: String, binding: FragmentPostBinding) {
        val postCollection = repository.getPostCollectionReference()
        GlobalScope.launch(Dispatchers.IO) {
            val post = postCollection.document(postId).get().await().toObject(Post::class.java)!!

            withContext(Dispatchers.Main) {
                Glide.with(binding.imgUserPostView.context).load(post.user.imageUrl)
                    .into(binding.imgUserPostView)
                binding.txtUserNamePostView.text = post.user.displayName
                binding.txtBodyPostView.text = post.textBody
                binding.txtLikeCountPostView.text = post.likes.size.toString()
                binding.txtTimeStampPostView.text = Utils.getTimeAgo(post.createdAt)
                binding.txtAngryCountPostView.text = post.angryReaction.size.toString()
                binding.txtHahaCountPostView.text = post.haHaReaction.size.toString()
                binding.txtSadCountPostView.text = post.sadReaction.size.toString()

                Glide.with(binding.imgUserCommmentPostView.context).load(post.user.imageUrl)
                    .circleCrop()
                    .into(binding.imgUserCommmentPostView)

                //Visibility
                binding.txtLikeCountPostView.visibility =
                    if (isCountZero(post.likes.size)) View.GONE else View.VISIBLE
                binding.txtAngryCountPostView.visibility =
                    if (isCountZero(post.angryReaction.size)) View.GONE else View.VISIBLE
                binding.txtSadCountPostView.visibility =
                    if (isCountZero(post.sadReaction.size)) View.GONE else View.VISIBLE
                binding.txtHahaCountPostView.visibility =
                    if (isCountZero(post.haHaReaction.size)) View.GONE else View.VISIBLE

            }
        }

    }

    private fun isCountZero(count: Int): Boolean {
        return count == 0
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