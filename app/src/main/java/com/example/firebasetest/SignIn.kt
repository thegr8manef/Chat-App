/*
package com.example.firebasetest


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasetest.databinding.SignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignIn : AppCompatActivity() {

    private lateinit var binding: SignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        binding.signinbtn.setOnClickListener {
Log.println(Log.ASSERT,"-------------------","temchi")
            login(binding.email.text.toString(), binding.password.text.toString())
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


    fun login(email: String, password: String) {


        try {
            auth = Firebase.auth
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"sign In successully",Toast.LENGTH_LONG).show()

                    val intent = Intent(this, HomeActivity::class.java).apply {
                    }
                    startActivity(intent)


                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this,"sorry your parameters not completed",Toast.LENGTH_LONG).show()

            }

        } catch (e: Exception) {
            Toast.makeText(this,"please, write email and password",Toast.LENGTH_LONG).show()


        }


    }
}*/
