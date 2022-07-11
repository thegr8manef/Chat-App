package com.example.firebasetest

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebasetest.databinding.DeleteLayoutBinding
import com.example.firebasetest.databinding.SendMsgBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MessagesAdapter(
    var context: Context,
    messages: ArrayList<Message>?,
    senderRoom:String?,
    receiversRoom: String?
):RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    var messages=  ArrayList<Message>()
    val ITEM_SENT = 1
    val ITEM_RECEIVER = 2
    lateinit var senderRoom: String
    lateinit var receiversRoom: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT){
            val view:View = LayoutInflater.from(context).inflate(R.layout.send_msg,parent,false)
            SentMsgHolder(view)
        }else{
            val view:View = LayoutInflater.from(context).inflate(R.layout.receive_msg,parent,false)
            SentMsgHolder(view)
        }

    }

    override fun getItemViewType(position: Int): Int {
val messages = messages[position]
        return if (FirebaseAuth.getInstance().uid == messages.senderId){
            ITEM_SENT
        }else{
            ITEM_RECEIVER
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder.javaClass == SentMsgHolder::class.java) {
            val viewHolder = holder as SentMsgHolder
            if (message.message.equals("photo")) {
                viewHolder.binding.image.visibility = View.VISIBLE
                viewHolder.binding.message.visibility = View.GONE
                viewHolder.binding.mLinear.visibility = View.GONE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(viewHolder.binding.image)
            }
            viewHolder.binding.message.text = message.message
            Log.println(Log.ASSERT,"==============================message of the adapter is >", message.message.toString())

            viewHolder.itemView.setOnLongClickListener {
                val view: View = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
                val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)
                val dialog = AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setView(binding.root)
                    .create()
                binding.everyone.setOnClickListener {
                    message.message = "This message is removed"
                    message.messageId?.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom)
                            .child("message")
                            .child(it1).setValue(message)
                    }
                    message.messageId.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(receiversRoom)
                            .child("message")
                            .child(it1!!).setValue(message)
                    }
                    dialog.dismiss()
                }
                binding.delete.setOnClickListener {
                    message.messageId.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom)
                            .child("message")
                            .child(it1!!).setValue(null)
                    }
                    dialog.dismiss()
                }
                binding.cancel.setOnClickListener { dialog.dismiss() }
                dialog.show()
                false
            }

        } else {
            val viewHolder = holder as SentMsgHolder
            if (message.message.equals("photo")) {
                viewHolder.binding.image.visibility = View.VISIBLE
                viewHolder.binding.message.visibility = View.GONE
                viewHolder.binding.mLinear.visibility = View.GONE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(viewHolder.binding.image)
            }
            viewHolder.binding.message.text = message.message
            viewHolder.itemView.setOnLongClickListener {
                val view: View = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
                val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)
                val dialog = AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setView(binding.root)
                    .create()
                binding.everyone.setOnClickListener {
                    message.message = "This message is removed"
                    message.messageId?.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom)
                            .child("message")
                            .child(it1).setValue(message)
                    }
                    message.messageId.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(receiversRoom)
                            .child("message")
                            .child(it1!!).setValue(message)
                    }
                    dialog.dismiss()
                }
                binding.delete.setOnClickListener {
                    message.messageId.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom)
                            .child("message")
                            .child(it1!!).setValue(null)
                    }
                    dialog.dismiss()
                }
                binding.cancel.setOnClickListener { dialog.dismiss() }
                dialog.show()
                false
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class SentMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: SendMsgBinding = SendMsgBinding.bind(itemView)

        inner class ReceiveMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var binding: SendMsgBinding = SendMsgBinding.bind(itemView)
        }

        init {
            if (messages != null) {
                this@MessagesAdapter.messages = messages
            }
            this@MessagesAdapter.senderRoom = senderRoom
            this@MessagesAdapter.receiversRoom = receiversRoom


        }

    }
}
