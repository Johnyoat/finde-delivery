package com.finde.deliveryapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.finde.deliveryapp.MainActivity
import com.finde.deliveryapp.R
import com.finde.deliveryapp.databinding.ActivityLoginSplashBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar

class LoginSplashActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginSplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val providers = arrayListOf(AuthUI.IdpConfig.PhoneBuilder().build())


        binding.getStartedBtn.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setTheme(R.style.AppTheme)
                    .build(), 100
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == AppCompatActivity.RESULT_OK) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            } else {
                Snackbar.make(
                    binding.root,
                    response!!.error!!.localizedMessage!!,
                    Snackbar.LENGTH_LONG
                )
            }
        }
    }
}