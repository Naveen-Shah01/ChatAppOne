package com.example.chatappone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatappone.databinding.ActivityChatBinding
import com.example.chatappone.databinding.ActivityChatUsersBinding
import com.example.chatappone.databinding.ActivitySignUpBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var chatBinding:ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatBinding = ActivityChatBinding.inflate(layoutInflater)
        val view = chatBinding.root
        setContentView(view)

// val
        val name = intent.getStringExtra("name")
        val userId = intent.getStringExtra("id")
 val username = name
        // helps to use our own action bar that is toolbar
        setSupportActionBar(chatBinding.toolBarChatActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title=name

        // this will lead us to the main screen as exercise activity is already finished
        chatBinding.toolBarChatActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }




}