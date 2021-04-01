package net.raj.mushimushi.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import net.raj.mushimushi.R
import net.raj.mushimushi.databinding.FragmentSignInBinding
import timber.log.Timber


class SignInFragment : Fragment() {
    private val RC_SIGN_IN: Int = 123
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: FragmentSignInBinding
    private lateinit var viewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in, container, false)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.firebaseUser = FirebaseAuth.getInstance().currentUser
        setUpSignInClient()
        setOnCLickListeners()
        return binding.root
    }

    private fun setUpSignInClient() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = activity?.let { GoogleSignIn.getClient(it, gso) }!!
    }

    private fun setOnCLickListeners() {
        binding.btnSignIn.setOnClickListener {
            signIn()
        }
    }


    override fun onStart() {
        super.onStart()
        updateUI(viewModel.firebaseUser)
    }

    private fun signIn() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSignIn.visibility = View.GONE
        binding.imgMeme.visibility = View.GONE
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Timber.d("firebaseAuthWithGoogle: + $account.id")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Timber.e(e, "Google Sign In Failed")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        activity?.let {
            viewModel.auth.signInWithCredential(credential)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Timber.d("signInWithCredential:success")

                        viewModel.firebaseUser = viewModel.auth.currentUser
                        updateUI(viewModel.firebaseUser)

                    } else {
                        // If sign in fails, display a message to the user.
                        Timber.e(task.exception, "signInWithCredential:failed")

                    }
                }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val firebaseUser = viewModel.auth.currentUser
            viewModel.addNewUser(firebaseUser)
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_signInFragment2_to_homeFragment)
        } else {
            binding.btnSignIn.visibility = View.VISIBLE
            binding.imgMeme.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }
}
