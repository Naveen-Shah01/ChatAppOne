package com.example.chatappone

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatappone.Adapter.MessageAdapter
import com.example.chatappone.databinding.ActivityChatBinding
import com.example.chatappone.models.MessagesEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.Date
import java.util.Objects

class ChatActivity : AppCompatActivity() {

 
    private lateinit var chatBinding: ActivityChatBinding


    private val messageList = ArrayList<MessagesEntity>()
    lateinit var messageAdapter: MessageAdapter


    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference
    private val toReference: DatabaseReference = database.reference

    private var senderUserId: String? = null
    private var receiverUserId: String? = null
    private var friendProfileImageUrl: String? = null
    private var userProfileImageUrl: String? = null
    var senderRoom: String? = null
    var receiverRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatBinding = ActivityChatBinding.inflate(layoutInflater)
        val view = chatBinding.root
        setContentView(view)

        // getting the name of receiver
        val name = intent.getStringExtra("name")

        // taking friend image from previous intent
        friendProfileImageUrl = intent.getStringExtra("profileImageUrl")

        toolbarSetup()

        setFriendImage(friendProfileImageUrl, name)

        // getting the receiver user id
        receiverUserId = intent.getStringExtra("id")
        // getting the sender user id from firebase
        senderUserId = FirebaseAuth.getInstance().currentUser?.uid




//       senderRoom = receiverUserId + senderUserId
//       receiverRoom = senderUserId + receiverUserId

        // taking user image from previous intent
        userProfileImageUrl = intent.getStringExtra("userImageUrl")

        Log.d("error", "Link : $userProfileImageUrl")
        setUpRecyclerView(userProfileImageUrl)


        // adding the message to database
        chatBinding.btSendMessage.setOnClickListener {
            val message = chatBinding.etMessageBox.text.toString()
            if (message.isNotEmpty()) {
                val id = myReference.child("chats").push().key
                val timeStamp = Date()
                val messageObject = MessagesEntity(
                    id,
                    message,
                    senderUserId,
                    receiverUserId,
                    timeStamp.time
                )



                myReference.child("chats/$senderUserId/$receiverUserId").push()
                    .setValue(messageObject).addOnSuccessListener {
                        chatBinding.etMessageBox.text.clear()
                        chatBinding.rvChatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                    }

                toReference.child("chats/$receiverUserId/$senderUserId").push()
                    .setValue(messageObject)

            }
        }



    }

        private fun toolbarSetup() {
            // helps to use our own action bar that is toolbar
            setSupportActionBar(chatBinding.toolBarChatActivity)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)

            // this will lead us to the main screen as exercise activity is already finished
            chatBinding.toolBarChatActivity.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        private fun setFriendImage(profileImageUrl: String?, name: String?) {
            chatBinding.tvFriendNameChat.text = name
            if (profileImageUrl != "") {
                Picasso.get().load(profileImageUrl).into(chatBinding.ivFriendImageChat)
            }
        }


        private fun setUpRecyclerView(userProfileImageUrl: String?) {
            chatBinding.rvChatRecyclerView.layoutManager = LinearLayoutManager(this@ChatActivity)
            messageAdapter = MessageAdapter(
                this@ChatActivity,
                messageList,
                friendProfileImageUrl!!,
                userProfileImageUrl!!
            )
            chatBinding.rvChatRecyclerView.adapter = messageAdapter

            myReference.child("chats/$senderUserId/$receiverUserId")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        messageList.clear()
                        for (eachMessage in snapshot.children) {
                            val message = eachMessage.getValue(MessagesEntity::class.java)

                            if (message != null)
                                messageList.add(message)
                        }

                        messageAdapter.notifyDataSetChanged()
                        chatBinding.rvChatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(applicationContext, "Cannot Display", Toast.LENGTH_LONG)
                            .show()
                    }
                })
        }
    }
