package net.raj.mushimushi.ui.home

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import net.raj.mushimushi.R
import net.raj.mushimushi.databinding.FragmentCreatePostBinding


class CreatePostFragment : Fragment() {

    private lateinit var binding: FragmentCreatePostBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_post, container, false)
        setOnClickListeners()
        return binding.root
    }

    private fun setOnClickListeners() {
        binding.btnNewPost.setOnClickListener {
            onClickPostBT()
        }

    }

    private fun hideSoftKBD() {
        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun onClickPostBT() {
        hideSoftKBD()
        val text = binding.etInputPost.text.toString().trim()
        if (text.isEmpty()) {
            Snackbar.make(binding.root, "Cannot make a Empty post :(", Snackbar.LENGTH_LONG)
                .setTextColor(Color.WHITE)
                .setBackgroundTint(Color.parseColor("#fd5e53"))
                .show()
        } else {
            viewModel.addNewPost(text)
            Navigation.findNavController(binding.root).popBackStack()
        }

    }


}