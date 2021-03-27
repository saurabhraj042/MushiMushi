package net.raj.mushimushi.ui.comments

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import net.raj.mushimushi.databinding.ActivityCommentSectBinding
import net.raj.mushimushi.models.Comments

class CommentSectActivity : AppCompatActivity() {
    private lateinit var binding :ActivityCommentSectBinding
    private lateinit var adapter: CommentAdapter
    private val viewModel : CommentViewModel by viewModels()
    private lateinit var postId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentSectBinding.inflate(layoutInflater)
        postId = CommentSectActivityArgs.fromBundle(intent?.extras!!).postId

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        binding.postCommentButton.setOnClickListener{
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
            val text = binding.commentInput.text.toString()
            viewModel.addComment(text, postId)
            binding.commentInput.text.clear()
        }

        setUpRecyclerView()

        setContentView(binding.root)

    }



    private fun setUpRecyclerView() {
        val query = viewModel.commentCollection.whereEqualTo(
            "postId",
            postId
        ).orderBy("createdAt", Query.Direction.ASCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Comments>().setQuery(
            query,
            Comments::class.java
        ).build()
        adapter = CommentAdapter(recyclerViewOptions)

        binding.recyclerViewCommentActivity.adapter = adapter
        binding.recyclerViewCommentActivity.layoutManager = LinearLayoutManager(this)
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