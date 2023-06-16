package com.example.chatappone.authentication

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatappone.models.UsersEntity
import com.example.chatappone.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


//TODO add method for google signup in signup activity also change google sign in button text
// TODO add a method to check if mail id are valid or not

// TODO add internet permission check separately

class SignUpActivity : AppCompatActivity() {
    private lateinit var signUpBinding: ActivitySignUpBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference.child("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = signUpBinding.root
        setContentView(view)

        toolBarSetUp()

        signUpBinding.btSignUp.setOnClickListener {
            val signUpEmail: String = signUpBinding.etEmailSignUp.text.toString()
            val signUpName: String = signUpBinding.etNameSignUp.text.toString()
            val signUpPassword: String = signUpBinding.etPasswordSignUp.text.toString()

            if (isDetailsNotEmpty(signUpEmail, signUpName, signUpPassword)) {
                signUpWithFireBase(signUpEmail, signUpPassword, signUpName)
            }

        }
        signUpBinding.tvLoginFromSignUp.setOnClickListener {
            // it will automatically destroy activity.
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun toolBarSetUp() {
        // helps to use our own action bar that is toolbar
        setSupportActionBar(signUpBinding.toolBarSignUpActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // this will lead us to the main screen as exercise activity is already finished
        signUpBinding.toolBarSignUpActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun isDetailsNotEmpty(
        signUpEmail: String,
        signUpName: String,
        signUpPassword: String
    ): Boolean {
        var valid = true
        if (TextUtils.isEmpty(signUpEmail)) {
            signUpBinding.etEmailSignUp.error = "Email cannot be empty"
            valid = false
        }

        if (TextUtils.isEmpty(signUpName)) {
            signUpBinding.etNameSignUp.error = "Name cannot be empty"
            valid = false
        }
// TODO check if password is more than or equal to 8 character long
        if (TextUtils.isEmpty(signUpPassword)) {
            signUpBinding.etPasswordSignUp.error = "Password cannot be empty"
            valid = false
        }

        return valid
    }


    private fun signUpWithFireBase(userEmail: String, userPassword: String, userName: String) {
        // TODO add name section in authentication that is add name as well
        signUpBinding.pbProgressSignup.visibility = View.VISIBLE
        signUpBinding.btSignUp.isClickable = false
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //TODO add user to database
                    // user id is in our auth
                    addUserToDatabase(userEmail,auth.currentUser?.uid!!,userName)
                    Toast.makeText(
                        applicationContext,
                        "Your account created", Toast.LENGTH_SHORT,
                    ).show()
                    finish()

                } else {
                    //TODO handle the progressbar and button if authentication fails
                    Toast.makeText(
                        applicationContext,
                        task.exception?.localizedMessage, Toast.LENGTH_SHORT,
                    ).show()
                    signUpBinding.pbProgressSignup.visibility = View.GONE
                    signUpBinding.btSignUp.isClickable = true

                }
            }
    }

    private fun addUserToDatabase(userName: String, userEmail: String, userId: String) {
        val user = UsersEntity(userId, userName, userEmail)
        myReference.child(userId).setValue(user)
    }


}