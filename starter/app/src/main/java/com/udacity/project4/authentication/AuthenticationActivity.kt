package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R

import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAuthenticationBinding
    private val authenticationViewModel by viewModels<AuthenticationViewModel>()
companion object{
    const val SIGN_IN_RESULT_CODE = 1001
    const val TAG = "AuthenticationActivity"
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_authentication)

//         Done: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google
binding.login.setOnClickListener {
    launchSignInFlow()
}
        authenticationViewModel.authenticationState.observe(this, Observer {
            authenticationState ->

                when (authenticationState) {
                    AuthenticationViewModel.AuthenticationState.AUTHENTICATED -> {
                        startActivity(Intent(this, RemindersActivity::class.java))
                    }
                }

        })

//          Done: If the user was authenticated, send him to RemindersActivity

        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout


    }

    private fun launchSignInFlow(){
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build(),AuthUI.IdpConfig.GoogleBuilder().build())
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),SIGN_IN_RESULT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == SIGN_IN_RESULT_CODE){
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){
                Log.i(TAG,"Successfully signed in"+"${FirebaseAuth.getInstance().currentUser?.displayName}")
            }else{
                Log.i(TAG,"Sign in failed${response?.error?.errorCode}")
            }
        }
    }

}
