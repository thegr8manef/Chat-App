/*
package com.example.firebasetest


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasetest.databinding.SignUpBinding
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class SignUp : AppCompatActivity() {

    private lateinit var binding: SignUpBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        binding.loginbtn.setOnClickListener {

            //SignUp(binding.email.text.toString(), binding.password.text.toString())
            val intentDataUser = Intent(this, VerificationActivity::class.java).apply {
                putExtra("email",binding.email.text.toString())
                putExtra("password",binding.password.text.toString())
            }
            startActivity(intentDataUser)
        }

    }

    fun logout() {
        auth = Firebase.auth
        auth.signOut()
    }

    fun ifLogin(): String? {

        auth = Firebase.auth

        var user = auth.currentUser

        if (user != null) {
            return user.uid
        }

        return null
    }



}*/
