package com.example.chatappone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatappone.authentication.LoginActivity
import com.example.chatappone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // TODO implement logout feature
    // TODO change all authentication process from activities to one fragment
    // TODO add feature to chat with chatgpt
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.btLogin1.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}