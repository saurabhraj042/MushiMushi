package net.raj.mushimushi.ui.home

import android.content.Context.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import net.raj.mushimushi.R
import net.raj.mushimushi.databinding.FragmentCreatePostBinding


class CreatePostFragment : Fragment() {

    private lateinit var binding: FragmentCreatePostBinding
    private val sharedViewModel : HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_post, container, false)

        binding.postButton.setOnClickListener{
            val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(binding.root.windowToken, 0)

            fetchInputText()
        }

        return binding.root
    }

    private fun fetchInputText() {
        val text = binding.postInput.text.toString().trim()
        sharedViewModel.addPost(text)
        Navigation.findNavController(binding.root).popBackStack()
    }


}