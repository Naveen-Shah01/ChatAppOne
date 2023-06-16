package com.example.chatappone

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.chatappone.authentication.LoginActivity
import com.example.chatappone.databinding.ActivityMain2Binding
import com.google.firebase.auth.FirebaseAuth

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btn.setOnClickListener {
            showAlertDialogLogOut()
        }

        binding.btnChat.setOnClickListener {
            val intent = Intent(this, ChatUsersActivity::class.java)
            startActivity(intent)
        }
    }
// TODO fix the color of positive button and negative button
    private fun showAlertDialogLogOut() {
        val logOutDialog = AlertDialog.Builder(this)
        logOutDialog.setTitle("Logout")
        logOutDialog.setIcon(R.drawable.name_icon)
        logOutDialog.setMessage("Do you really want to log out")
        logOutDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
            FirebaseAuth.getInstance().signOut()

            startLoginActivity()

            Toast.makeText(applicationContext, "Logged Out", Toast.LENGTH_SHORT)
                .show()
        })
        logOutDialog.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel()
        })

        logOutDialog.create().show()
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}