package com.example.firebasetest


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasetest.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
/*        if (ifLogin() != null) {
            val intent = Intent(this, HomeActivity::class.java).apply {
            }
            startActivity(intent)
        } else {*/

            binding.registerbtn.setOnClickListener {
                val intent = Intent(this, VerificationActivity::class.java).apply {
                }
                startActivity(intent)
            }
/*            binding.loginbtn.setOnClickListener {
                val intent1 = Intent(this, SignIn::class.java).apply {
                }
                startActivity(intent1)
            }*/
        //}

    }

        fun ifLogin(): String? {

            auth = Firebase.auth

            var user = auth.currentUser



            return user?.uid


        }


}