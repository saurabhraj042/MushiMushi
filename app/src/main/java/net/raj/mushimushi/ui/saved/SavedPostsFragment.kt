package net.raj.mushimushi.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import net.raj.mushimushi.R
import net.raj.mushimushi.databinding.FragmentSavedPostsBinding
import net.raj.mushimushi.models.Post
import net.raj.mushimushi.ui.shared.IPostAdapter
import net.raj.mushimushi.ui.shared.PostAdapter

class SavedPostsFragment : Fragment(), IPostAdapter {

    private lateinit var viewModel: SavedPostsViewModel
    private lateinit var binding: FragmentSavedPostsBinding
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_saved_posts, container, false)
        viewModel = ViewModelProvider(this).get(SavedPostsViewModel::class.java)
        setupRecyclerview()
        return binding.root
    }

    private fun setupRecyclerview() {

        val query = viewModel.setupQuery()

        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(query, Post::class.java).build()

        adapter = PostAdapter(
            recyclerViewOptions,
            this,
            binding.viewOnEmptySaved,
            binding.txtTitleSavedView
        )
        val layoutManager = LinearLayoutManager(this.context)
        binding.recyclerSaved.adapter = adapter
        binding.recyclerSaved.layoutManager = layoutManager
    }


    override fun onSaveBTClicked(postId: String) {
        viewModel.onSavePostBTClicked(postId)
    }


    override fun onPostDeleteBTClicked(postId: String) {
        viewModel.deletePost(postId)
    }

    override fun onReactionBTClicked(postId: String, type: String) {
        viewModel.updatePostReaction(postId, type)
    }

    override fun onCommentBTClicked(postId: String) {
        val bundle = bundleOf("postId" to postId)
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_savedPostsFragment_to_commentFragment, bundle)
    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()

    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

}