package net.raj.mushimushi.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import net.raj.mushimushi.R
import net.raj.mushimushi.databinding.FragmentHomeBinding
import net.raj.mushimushi.models.Post

class HomeFragment : Fragment(), IPostAdapter {
    lateinit var binding: FragmentHomeBinding
    private val sharedViewModel : HomeViewModel by activityViewModels()
    private lateinit var adapter: HomeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)

        sharedViewModel.firebaseUser.observe(viewLifecycleOwner){
            if(it==null){
                Navigation.findNavController(binding.root).popBackStack()
            }
        }

        binding.bottomAppBar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.signOutButton ->{
                    sharedViewModel.auth.signOut()
                    sharedViewModel.firebaseUser.value = null
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

        return binding.root
    }


    private fun setUpRecyclerView() {
        sharedViewModel.updateUtilities()
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(sharedViewModel.query, Post::class.java).build()
        adapter = HomeAdapter(recyclerViewOptions,this)
        val layoutManager = LinearLayoutManager(this.context)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
    }

    override fun onReactionClicked(postId: String,type : String) {
        sharedViewModel.updatePostReaction(postId,type)
    }

    override fun onCommentButtonClicked(postId: String) {
        val bundle = bundleOf("postId" to postId)
        Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_commentSectActivity,bundle)
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