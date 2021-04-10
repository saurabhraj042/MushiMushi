package net.raj.mushimushi.ui.comments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.Query
import net.raj.mushimushi.R
import net.raj.mushimushi.databinding.FragmentCommentBinding
import net.raj.mushimushi.models.Comments
import net.raj.mushimushi.ui.comments.CommentAdapter.ICommentAdapter


class CommentFragment : Fragment(), ICommentAdapter {

    private lateinit var binding: FragmentCommentBinding
    private lateinit var adapter: CommentAdapter
    private lateinit var viewModel: CommentViewModel
    private lateinit var postId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment, container, false)
        viewModel = ViewModelProvider(this).get(CommentViewModel::class.java)
        postId = arguments?.getString("postId").toString()

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        binding.viewOnEmpty.visibility = View.GONE


        setClickListeners()
        setUpRecyclerView()

        return binding.root
    }


    private fun setClickListeners() {
        binding.btnPostComment.setOnClickListener {
            hideSoftKBD()
            onCLickPostComment()
        }

    }

    private fun onCLickPostComment() {
        val text = binding.etCommentInput.text.toString()
        if(text.isEmpty()){
            Snackbar.make(binding.root,"Cannot post a Empty comment :(", Snackbar.LENGTH_LONG)
                .setTextColor(Color.WHITE)
                .setBackgroundTint(Color.parseColor("#fd5e53"))
                .show()
        }else{
            viewModel.addComment(text, postId)
            binding.etCommentInput.text.clear()
        }
    }


    private fun hideSoftKBD() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun setUpRecyclerView() {
        val query = viewModel.getCommentCollectionRef().whereEqualTo(
            "postId",
            postId
        ).orderBy("createdAt", Query.Direction.ASCENDING)


        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Comments>().setQuery(
            query,
            Comments::class.java
        ).build()
        adapter = CommentAdapter(recyclerViewOptions, this, binding.viewOnEmpty,binding.txtTitleComment)
        binding.recyclerviewComment.adapter = adapter
        binding.recyclerviewComment.layoutManager = LinearLayoutManager(this.context)
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