package com.example.firebasetest

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.firebasetest.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {
    lateinit var binding : ActivityChatBinding
    var adapter : MessagesAdapter? = null
    var messages : ArrayList<Message>? = null
    var senderRoom:String? = null
    var receiversRoom:String? = null
    private lateinit var database: FirebaseDatabase
    var storage  : FirebaseStorage? = null
    private lateinit var dialog:ProgressDialog
    var senderUid : String? = null
    var receiverUid : String? =null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        dialog = ProgressDialog(this)
        dialog.setMessage("Uploading image ...")
        dialog.setCancelable(false)
        messages = ArrayList()


        val name = intent.getStringExtra("name")
        val profile = intent.getStringExtra("image")
        binding.name.text = name
        Glide.with(this).load(profile)
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.profile01)
        binding.imageview.setOnClickListener{ finish()}

        receiverUid = intent.getStringExtra("uid")
        senderUid = FirebaseAuth.getInstance().uid
        database.reference.child("Presence").child(receiverUid!!)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val status = snapshot.getValue(String::class.java)
                        if (status == "Offline"){
                            binding.status.visibility = View.GONE
                        }else{
                            binding.status.setText(status)
                            binding.status.visibility = View.VISIBLE
                        }
                    }
                    }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        senderRoom = senderUid + receiverUid
        receiversRoom = receiverUid + senderUid
        adapter = MessagesAdapter(this@ChatActivity, messages,senderRoom,receiversRoom)

        binding.recycleView.layoutManager = LinearLayoutManager(this@ChatActivity)
        binding.recycleView.adapter = adapter
        database.reference.child("chats")
            .child(senderRoom!!)
            .child("message")
            .addValueEventListener(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages!!.clear()

                for(snapshot1 in snapshot.children){
                    val message : Message? = snapshot1.getValue(Message::class.java)
                    message!!.messageId = snapshot1.key
                    messages!!.add(message)
                    Log.println(Log.ASSERT,"==============================message of chat activity is >",message.message.toString())
                    //binding.messageBox.setText(message.message)
                }
                    adapter!!.notifyDataSetChanged()

                                       }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        binding.send.setOnClickListener{
            val messageTxt:String = binding.messageBox.text.toString()
            val date = Date()
            val message = Message(messageTxt,senderUid,date.time)
            binding.messageBox.setText(" ")
            val randomKey = database.reference.push().key
            val lastMashOng = HashMap<String,Any>()
            lastMashOng["lastMsg"] = message.message!!
            lastMashOng["lastMsgTime"] = date.time
            database.reference.child("chats").child(senderRoom!!)
                .updateChildren(lastMashOng)
            database.reference.child("chats").child(receiversRoom!!)
                .updateChildren(lastMashOng)

            database.reference.child("chats").child(senderRoom!!)
                .child("message")
                .child(randomKey!!)
                .setValue(message).addOnSuccessListener {
                    database.reference.child("chats")
                        .child(receiversRoom!!)
                        .child("message")
                        .child(randomKey)
                        .setValue(message)
                        .addOnSuccessListener {
                            Toast.makeText(this,"sender" + message.message,Toast.LENGTH_LONG).show()
                            val datatest = database.getReference("chats").child(receiversRoom!!).child("lastMsg")
                            Log.println(Log.ASSERT,"=========> data from firebase",datatest.toString())
                        }
                }

        }
        binding.attachment.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,25)
        }

        val handler = Handler()
        binding.messageBox.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                database.reference.child("Presence")
                    .child(senderUid!!)
                    .setValue("typing...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping,1000)
            }
                var userStoppedTyping = Runnable {
                    database.reference.child("Presence")
                        .child(senderUid!!)
                        .setValue("Online")
}
        })
        supportActionBar?.setDisplayShowTitleEnabled(false)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 25){
            if (data != null){
                if(data.data != null){
                    val selectedImage = data.data
                    val calendar = Calendar.getInstance()
                    val refence = storage!!.reference.child("chats")
                        .child(calendar.timeInMillis.toString()+"")
                    dialog.show()
                    refence.putFile(selectedImage!!).addOnCompleteListener{ task->
                        dialog.dismiss()
                        if (task.isSuccessful){
                            refence.downloadUrl.addOnSuccessListener {uri->
                                val filePath = uri.toString()
                                val messageTxt : String = binding.messageBox.text.toString()
                                val date = Date()
                                val message = Message(messageTxt,senderUid,date.time)
                                message.message = "photo"
                                message.imageUrl = filePath
                                binding.messageBox.setText("")
                                val randomKey = database.reference.push().key
                                val lastMsgObj = HashMap<String,Any>()
                                lastMsgObj["lastMsg"] = message.message!!
                                lastMsgObj["lastMsgTime"] = date.time
                                database.reference.child("chats")
                                    .updateChildren(lastMsgObj)
                                database.reference.child("chats")
                                    .child(receiversRoom!!)
                                    .updateChildren(lastMsgObj)
                                database.reference.child("chats")
                                    .child(senderRoom!!)
                                    .child("message")
                                    .child(randomKey!!)
                                    .setValue(message).addOnSuccessListener {
                                        database.reference.child("chats")
                                            .child(receiversRoom!!)
                                            .child("message")
                                            .child(randomKey)
                                            .setValue(message)
                                            .addOnSuccessListener {


                                            }
                                    }
                            }
                        }

                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database.reference.child("Presence")
            .child(currentId!!)
            .setValue("Online")

    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database.reference.child("Presence")
            .child(currentId!!)
            .setValue("Offline")

    }
}