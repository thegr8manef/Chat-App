package com.example.firebasetest


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasetest.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.nameuser.setText("welcome "+ifLogin().toString())

        binding.logout.setOnClickListener{
            logout()
        }

    }

    fun ifLogin(): String? {

        auth = Firebase.auth

        var user = auth.currentUser

        if (user != null&& user.email!=null) {
            return user.email
        }
        else{
            return user?.phoneNumber
        }

        return null
    }
    fun logout() {
        auth = Firebase.auth
        auth.signOut()
        val intent1 = Intent(this, MainActivity::class.java).apply {
        }
        startActivity(intent1)
    }

}