package net.raj.mushimushi.ui.search

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import net.raj.mushimushi.R
import net.raj.mushimushi.databinding.SearchFragmentBinding
import net.raj.mushimushi.models.Post
import net.raj.mushimushi.ui.home.HomeAdapter
import net.raj.mushimushi.ui.home.IPostAdapter

class SearchFragment : Fragment(), IPostAdapter {

    private lateinit var adapter:HomeAdapter
    private lateinit var binding: SearchFragmentBinding
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(layoutInflater,R.layout.search_fragment,container,false)

        setupClickListeners()
        setupRecyclerView()

        return binding.root
    }

    private fun setupClickListeners() {

        binding.searchButtonSearchFragment.setOnClickListener {
            setupRecyclerView()
            hideKeyBoard()
            adapter.startListening()
        }
    }

    private fun hideKeyBoard() {
        val imm: InputMethodManager =
                requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun setupRecyclerView() {
        var searchText = binding.searchTextSearchFragment.text.toString()


        var query = if( searchText.isEmpty()){
            viewModel.getPostCollectionRef()
                .orderBy("createdAt",Query.Direction.DESCENDING)
        }else{
            Log.d("Search","Searching for "+ searchText)
            viewModel.getPostCollectionRef()
                    .whereGreaterThanOrEqualTo("textBody",searchText)
                    .whereLessThanOrEqualTo("textBody",searchText+'\uf8ff')
        }

        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(query, Post::class.java).build()

        adapter = HomeAdapter(recyclerViewOptions,this)

        binding.recyclerViewSearchFragment.adapter = adapter
        binding.recyclerViewSearchFragment.layoutManager = LinearLayoutManager(this.context)

    }

    override fun onReactionClicked(postId: String, type: String) {
        viewModel.updatePostReaction(postId,type)
    }

    override fun onCommentButtonClicked(postId: String) {
        val bundle = bundleOf("postId" to postId)
        Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_commentSectActivity,bundle)
    }


    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


}