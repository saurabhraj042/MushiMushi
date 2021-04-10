package net.raj.mushimushi.ui.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.transition.MaterialSharedAxis
import net.raj.mushimushi.R
import net.raj.mushimushi.databinding.FragmentNotificationBinding
import net.raj.mushimushi.models.Notification


class NotificationFragment : Fragment(), NotificationAdapter.INotification {

    private lateinit var binding: FragmentNotificationBinding
    private lateinit var adapter: NotificationAdapter
    private lateinit var viewModel: NotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = DataBindingUtil.inflate(layoutInflater,R.layout.fragment_notification,container,false)
        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)

        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        val query = viewModel.query
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Notification>().setQuery(
            query,
            Notification::class.java
        ).build()

        adapter = NotificationAdapter(recyclerViewOptions,this,binding.txtTitleNotiication)
        binding.recyclerNotification.adapter = adapter
        binding.recyclerNotification.layoutManager = LinearLayoutManager(this.context)
    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onItemClick(postId: String) {
        val bundle = bundleOf("postId" to postId)
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_notificationFragment_to_postFragment, bundle)
    }

}