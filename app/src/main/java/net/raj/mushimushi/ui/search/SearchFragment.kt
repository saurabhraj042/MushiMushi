package net.raj.mushimushi.ui.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import net.raj.mushimushi.R
import net.raj.mushimushi.databinding.FragmentSearchBinding
import net.raj.mushimushi.models.Post
import net.raj.mushimushi.ui.shared.IPostAdapter
import net.raj.mushimushi.ui.shared.PostAdapter
import timber.log.Timber

class SearchFragment : Fragment(), IPostAdapter {

    private lateinit var adapter: PostAdapter
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_search, container, false)

        setupClickListeners()
        setupRecyclerView()

        return binding.root
    }

    private fun setupClickListeners() {

        binding.btnSearchForPost.setOnClickListener {
            setupRecyclerView()
            hideKBD()
            adapter.startListening()
        }
    }

    private fun hideKBD() {
        val imm: InputMethodManager =
            requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun setupRecyclerView() {
        val searchText = binding.etSearch.text.toString()


        val query = if (searchText.isEmpty()) {
            viewModel.getPostCollectionRef()
                .orderBy("createdAt", Query.Direction.DESCENDING)
        } else {
            Timber.d("Searching for $searchText")
            viewModel.getPostCollectionRef()
                .whereGreaterThanOrEqualTo("textBody", searchText)
                .whereLessThanOrEqualTo("textBody", searchText + '\uf8ff')
        }

        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(query, Post::class.java).build()

        adapter = PostAdapter(recyclerViewOptions, this)

        binding.recyclerSearch.adapter = adapter
        binding.recyclerSearch.layoutManager = LinearLayoutManager(this.context)

    }

    override fun changeInListSize(itemCount: Int) {
        Timber.d("Doing Nothing bruh chill ;)")
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
            .navigate(R.id.action_searchFragment_to_commentFragment, bundle)
    }


    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


}