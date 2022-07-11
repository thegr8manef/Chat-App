package com.example.firebasetest

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.firebasetest.databinding.ActivityMain2Binding
import com.example.firebasetest.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity2 : AppCompatActivity() {

    var binding : ActivityMain2Binding? = null
    var database : FirebaseDatabase? = null
    var users : ArrayList<User>? = null
    var usersAdapter : UserAdapter? = null
    var dialog : ProgressDialog? = null
    var user : User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding!!.root)
        dialog = ProgressDialog(this)
        dialog!!.setMessage("Uploading Image...")
        dialog!!.setCancelable(false)
        database = FirebaseDatabase.getInstance()
        users = ArrayList<User>()
        usersAdapter = UserAdapter(this,users!!)
        val layoutManager = GridLayoutManager(this,2)
        binding!!.mRec.layoutManager = layoutManager
        database!!.reference.child("users")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        binding!!.mRec.adapter = usersAdapter
        database!!.reference.child("users").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                users!!.clear()
                for (snapshot1 in snapshot.children){
                    val user : User? = snapshot1.getValue(User::class.java)
                    if (!user!!.uid.equals(FirebaseAuth.getInstance().uid)) users!!.add(user)
                }
                usersAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



    }

    override fun onResume() {
        super.onResume()
        val  currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence")
            .child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val  currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence")
            .child(currentId!!).setValue("Offline")

    }

}