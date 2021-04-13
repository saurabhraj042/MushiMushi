package net.raj.mushimushi.ui.post

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import net.raj.mushimushi.R
import net.raj.mushimushi.databinding.FragmentPostBinding
import net.raj.mushimushi.models.Comments
import net.raj.mushimushi.ui.shared.CommentAdapter
import timber.log.Timber

class PostFragment : Fragment(), CommentAdapter.ICommentAdapter {

    private lateinit var adapter: CommentAdapter
    private lateinit var binding: FragmentPostBinding
    private lateinit var viewModel: PostViewModel
    private lateinit var postId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post, container, false)
        viewModel = ViewModelProvider(this).get(PostViewModel::class.java)
        Timber.plant(Timber.DebugTree())
        postId = arguments?.getString("postId").toString()
        setUpViews()
        setupClickListeners()
        setupRecyclerView()
        return binding.root
    }

    private fun setupClickListeners() {
        binding.btnEtAddCommentPostView.setOnClickListener {
            val text = binding.etAddCommentPostView.text.toString()
            if (text.isEmpty()) {
                Timber.d("Empty Comment")
                Snackbar.make(binding.root, "Cannot post empty comment :(", Snackbar.LENGTH_SHORT)
                    .setTextColor(Color.WHITE)
                    .setBackgroundTint(Color.parseColor("#fd5e53"))
                    .show()
            } else {
                viewModel.addComment(text, postId)
                binding.etAddCommentPostView.text.clear()
            }
            hideSoftKBD()
        }
    }

    private fun setUpViews() {
        viewModel.getPost(postId, binding)
    }

    private fun setupRecyclerView() {
        val query = viewModel.setCommentQuery(postId)

        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Comments>().setQuery(
            query,
            Comments::class.java
        ).build()
        adapter = CommentAdapter(recyclerViewOptions, this, binding.viewOnEmptyPost)
        binding.recyclerCommentPostView.adapter = adapter
        binding.recyclerCommentPostView.layoutManager = LinearLayoutManager(this.context)

    }

    private fun hideSoftKBD() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onDeleteCommentClicked(docId: String, postId: String) {
        viewModel.deleteSingleComment(docId, postId)
    }

    override fun onReactionClicked(docId: String, type: String) {
        viewModel.updateReactionOnComment(docId, type)
    }


}