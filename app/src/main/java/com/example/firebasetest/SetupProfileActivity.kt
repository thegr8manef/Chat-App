package com.example.firebasetest

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebasetest.databinding.ActivitySetupProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.HashMap

class SetupProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetupProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var selectedImage : Uri
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Updating Profile...")
        progressDialog.setCancelable(false)
        binding.profileImage.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type ="image/*"
            startActivityForResult(intent,45)
        }

        binding.idBtnContinue.setOnClickListener {
            val name : String = binding.idEdtNameUser.text.toString()
            if (name.isEmpty()){
                binding.idBtnContinue.setError("Please type your name")

            }
            progressDialog.show()
            if (selectedImage != null){
                val reference = storage.reference.child("Profile")
                    .child(auth.uid!!)
                reference.putFile(selectedImage).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        reference.downloadUrl.addOnCompleteListener { uri ->
                            val imageUrl = uri.toString()
                            val uid = auth.uid
                            val phone = auth.currentUser!!.phoneNumber
                            val name :String = binding.idEdtNameUser.text.toString()
                            val user = User(uid,name,phone,imageUrl)
                            database.reference
                                .child("users")
                                .child(uid!!)
                                .setValue(user)
                                .addOnCompleteListener {
                                    progressDialog.dismiss()
                                    val intent = Intent(this,MainActivity2::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                        }
                    }
                    else{
                        val uid = auth.uid
                        val phone = auth.currentUser!!.phoneNumber
                        val name :String = binding.idEdtNameUser.text.toString()
                        val user = User(uid,name,phone,"No Image")
                        database.reference
                            .child("users")
                            .child(uid!!)
                            .setValue(user)
                            .addOnCompleteListener {
                                progressDialog.dismiss()
                                val intent = Intent(this,MainActivity2::class.java)
                                startActivity(intent)
                                finish()
                            }

                    }
                }

            }
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null){
            if (data.data != null){
                val uri = data.data
                val storage = FirebaseStorage.getInstance()
                val time = Date().time
                val reference = storage.reference
                    .child("Profile")
                    .child(time.toString() + "")
                reference.putFile(uri!!).addOnCompleteListener{task->
                    if (task.isSuccessful){
                        reference.downloadUrl.addOnCompleteListener { uri->
                            val filePath = uri.toString()
                            val obj = HashMap<String,Any>()
                            obj["image"] = filePath
                            database.reference
                                .child("users")
                                .child(FirebaseAuth.getInstance().uid!!)
                                .updateChildren(obj).addOnCompleteListener {  }
                        }
                    }
                }
                    binding.profileImage.setImageURI(data.data)
                    selectedImage = data.data!!
            }
        }
    }

}