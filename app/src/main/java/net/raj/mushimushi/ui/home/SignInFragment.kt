package net.raj.mushimushi.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import net.raj.mushimushi.R
import net.raj.mushimushi.databinding.FragmentSignInBinding


class SignInFragment : Fragment() {
    private val RC_SIGN_IN: Int = 123
    private val TAG = "SignInFragment"

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: FragmentSignInBinding
    private val sharedViewModel : HomeViewModel by activityViewModels()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sign_in,container,false)

        googleSignInClient = activity?.let { GoogleSignIn.getClient(it, gso) }!!

        binding.signInButton.setOnClickListener{
            signIn()
        }


        sharedViewModel.firebaseUser.observe(viewLifecycleOwner){
            updateUI(it)
        }
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        updateUI(sharedViewModel.firebaseUser.value)
    }

    private fun signIn() {
        binding.progressBar.visibility = View.VISIBLE
        binding.signInButton.visibility = View.GONE
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
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        activity?.let {
            sharedViewModel.auth.signInWithCredential(credential)
                    .addOnCompleteListener(it) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success")

                           sharedViewModel.firebaseUser.value = sharedViewModel.auth.currentUser

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.exception)

                        }
                    }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user!=null){
            val firebaseUser = sharedViewModel.auth.currentUser
            sharedViewModel.addUser(firebaseUser)
            Log.i(TAG,"Signed in")
            Navigation.findNavController(binding.root).navigate(R.id.action_signInFragment2_to_homeFragment)
        }else{
            binding.signInButton.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            Log.e(TAG,"Not Signed in")
        }
    }
}
