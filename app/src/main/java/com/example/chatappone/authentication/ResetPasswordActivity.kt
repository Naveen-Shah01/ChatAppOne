package com.example.chatappone.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chatappone.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {
    // TODO setup a standard mail for resetpassword
    private lateinit var resetBinding: ActivityResetPasswordBinding
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetBinding = ActivityResetPasswordBinding.inflate(layoutInflater)
        val view = resetBinding.root
        setContentView(view)

        toolBarSetUp()
        resetBinding.btResetPassword.setOnClickListener {
            val email = resetBinding.etResetEmail.text.toString()
            if(email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            applicationContext,
                            "Reset Link has been sent on registered mail",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            task.exception?.localizedMessage, Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
        }
        resetBinding.tvResetLogin.setOnClickListener {
            // it will automatically destroy activity.
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun toolBarSetUp() {
        // helps to use our own action bar that is toolbar
        setSupportActionBar(resetBinding.toolBarResetPasswordActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // this will lead us to the main screen as exercise activity is already finished
        resetBinding.toolBarResetPasswordActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}