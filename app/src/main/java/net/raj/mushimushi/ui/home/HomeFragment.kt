package net.raj.mushimushi.ui.home

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.raj.mushimushi.R
import net.raj.mushimushi.databinding.FragmentHomeBinding
import net.raj.mushimushi.models.Post
import net.raj.mushimushi.ui.Repository

class HomeFragment : Fragment(), IPostAdapter {
    private val TAG = "HomeFrag"
    lateinit var binding: FragmentHomeBinding
    private val sharedViewModel : HomeViewModel by activityViewModels()
    private lateinit var adapter: HomeAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.scrollUpButtonHomeFrag.visibility = View.GONE


        binding.bottomAppBar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.signOutButton -> {
                    sharedViewModel.auth.signOut()
                    sharedViewModel.firebaseUser = null
                    Navigation.findNavController(binding.root).popBackStack()
                    true
                }

                R.id.searchPostButton -> {
                    Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_searchFragment)
                    true
                }
                else -> false
            }
        }

        binding.fab.setOnClickListener{
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_createPostFragment)
        }
        setUpRecyclerView()

        binding.scrollUpButtonHomeFrag.setOnClickListener {
            Handler().postDelayed({ binding.recyclerView.scrollToPosition(0) }, 200)
            it.visibility = View.GONE
        }

        sharedViewModel.getPostCollectionReference()
                .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }

                if( snapshots!!.size() != sharedViewModel.numberOfPosts.value ){
                    sharedViewModel.numberOfPosts.value = snapshots!!.size()
                }
        }

        sharedViewModel.numberOfPosts.observe(viewLifecycleOwner){
            binding.scrollUpButtonHomeFrag.visibility = View.VISIBLE
        }
        Log.d("HomeFrag", "OnCreate")

        return binding.root
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.d("HomeFrag", "OnViewStateRestored")
    }

    private fun setUpRecyclerView() {
        sharedViewModel.updateUtilities()
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(sharedViewModel.query, Post::class.java).build()
        adapter = HomeAdapter(recyclerViewOptions, this)
        val layoutManager = LinearLayoutManager(this.context)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
    }



    override fun onPostDelete(postId: String) {
        sharedViewModel.deletePost(postId,)
    }

    override fun onReactionClicked(postId: String, type: String) {
        sharedViewModel.updatePostReaction(postId, type)
    }

    override fun onCommentButtonClicked(postId: String) {
        val bundle = bundleOf("postId" to postId)
        Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_commentSectActivity, bundle)
    }



    override fun onStart() {
        super.onStart()
        Log.d("HomeFrag", "OnStart")
        adapter.startListening()

    }

    override fun onStop() {
        super.onStop()
        Log.d("HomeFrag", "OnStop")
        adapter.stopListening()
    }

}