package com.example.chatappone

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatappone.Adapter.MessageAdapter
import com.example.chatappone.databinding.ActivityChatBinding
import com.example.chatappone.models.MessagesEntity
import com.example.chatappone.models.UsersEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {
    private lateinit var chatBinding: ActivityChatBinding

    private val messageList = ArrayList<MessagesEntity>()
    lateinit var messageAdapter: MessageAdapter

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference

    var senderRoom: String? = null
    var receiverRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatBinding = ActivityChatBinding.inflate(layoutInflater)
        val view = chatBinding.root
        setContentView(view)

// val
        val name = intent.getStringExtra("name")
        val receiverUserId = intent.getStringExtra("id")
        val senderUserId = FirebaseAuth.getInstance().currentUser?.uid

        senderRoom = receiverUserId + senderUserId
        receiverRoom = senderUserId + receiverUserId
        // helps to use our own action bar that is toolbar
        setSupportActionBar(chatBinding.toolBarChatActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = name

        // this will lead us to the main screen as exercise activity is already finished
        chatBinding.toolBarChatActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setUpRecyclerView()

        // adding the message to database
        chatBinding.iBtnSendMessage.setOnClickListener {
            val message = chatBinding.etMessageBox.text.toString()
            if (message.isNotEmpty()) {
                val messageObject = MessagesEntity(message, senderUserId)

                myReference.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        myReference.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
                chatBinding.etMessageBox.text.clear()
            }
        }
    }

    private fun setUpRecyclerView() {
        chatBinding.rvChatRecyclerView.layoutManager = LinearLayoutManager(this@ChatActivity)
        messageAdapter = MessageAdapter(this@ChatActivity, messageList)
        chatBinding.rvChatRecyclerView.adapter = messageAdapter


        myReference.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (eachMessage in snapshot.children) {
                        val message = eachMessage.getValue(MessagesEntity::class.java)

                        if (message != null)
                                messageList.add(message)
                        }


                messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "Cannot Display", Toast.LENGTH_LONG).show()
                }
            })
    }
}