package net.raj.mushimushi.ui.home

import android.os.Bundle
import android.os.Handler
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
import net.raj.mushimushi.databinding.FragmentHomeBinding
import net.raj.mushimushi.models.Post
import net.raj.mushimushi.ui.shared.IPostAdapter
import net.raj.mushimushi.ui.shared.PostAdapter

class HomeFragment : Fragment(), IPostAdapter {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: PostAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.btnNewPostsScroll.visibility = View.GONE
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        setupOnClickListeners()
        setUpRecyclerView()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelObservers()
    }

    private fun viewModelObservers() {
        viewModel.numberOfPosts.observe(viewLifecycleOwner) {
            binding.btnNewPostsScroll.visibility = View.VISIBLE
        }

    }

    private fun setupOnClickListeners() {
        binding.btnAddNewPost.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_homeFragment_to_createPostFragment)
        }


        binding.btnNewPostsScroll.setOnClickListener {
            Handler().postDelayed({ binding.recyclerView.scrollToPosition(0) }, 200)
            it.visibility = View.GONE
        }


        binding.bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btn_sign_out -> {
                    viewModel.auth.signOut()
                    viewModel.firebaseUser = null
                    Navigation.findNavController(binding.root).popBackStack()
                    true
                }

                R.id.btn_search_post -> {
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_homeFragment_to_searchFragment)
                    true
                }

                R.id.btn_saved_post_view -> {
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_homeFragment_to_savedPostsFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setUpRecyclerView() {
        viewModel.updateUtilities()
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(viewModel.query, Post::class.java).build()
        adapter = PostAdapter(recyclerViewOptions, this)
        val layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
    }

    override fun onSaveBTClicked(postId: String) {
        viewModel.onSavePostBTClicked(postId)
    }

    override fun changeInListSize(itemCount: Int) {
        viewModel.checkListSize(itemCount)
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
            .navigate(R.id.action_homeFragment_to_commentFragment, bundle)
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