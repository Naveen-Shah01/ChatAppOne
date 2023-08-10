package com.example.chatappone.authentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.chatappone.MainActivity2
import com.example.chatappone.R
import com.example.chatappone.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : AppCompatActivity() {
    lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var loginBinding: ActivityLoginBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        setContentView(view)

        googleButtonDesignSetup()

        //register the launcher
        registerActivityForGoogleSignIn()

        loginBinding.btLogin.setOnClickListener {
            val loginEmail: String = loginBinding.etLoginEmail.text.toString()
            val loginPassword: String = loginBinding.etLoginPassword.text.toString()

            if (isDetailsNotEmpty(loginEmail, loginPassword)) {
                signInWithFireBase(loginEmail, loginPassword)
            }
        }
        loginBinding.btSgnGoogle.setOnClickListener {
           
            signInWithGoogle()
        }
        loginBinding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // when clicked on forgot password
        loginBinding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }
    }
    private fun registerActivityForGoogleSignIn() {
        activityResultLauncher =
            registerForActivityResult(
               ActivityResultContracts.StartActivityForResult(), ActivityResultCallback { result ->
                    val resultCode = result.resultCode
                    val data = result.data
                    if (resultCode == RESULT_OK && data != null) {
                        val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

                        fireBaseSignInWithGoogle(task)
                   }
               })

    }

    private fun fireBaseSignInWithGoogle(task: Task<GoogleSignInAccount>) {
            try {
                val googleAccount : GoogleSignInAccount = task.getResult(ApiException::class.java)
                Toast.makeText(applicationContext,"Login By Google",Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
                finish()
                fireBaseGoogleAccount(googleAccount)
            }
            catch (e : ApiException){
                Toast.makeText(applicationContext,e.localizedMessage,Toast.LENGTH_LONG).show()
            }
    }

    private fun fireBaseGoogleAccount(googleAccount: GoogleSignInAccount) {
         val authCredential = GoogleAuthProvider.getCredential(googleAccount.idToken,null)
        auth.signInWithCredential(authCredential).addOnCompleteListener { task->
            if(task.isSuccessful){
              
            }
            else {

            }
        }
    }

    private fun signInWithGoogle() {
       
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("722312502463-5l9b7485ri89fl950ldvh6au295bb3rs.apps.googleusercontent.com")
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        signIn()
    }

    private fun signIn() {
        val signInIntent : Intent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)

    }

    private fun googleButtonDesignSetup() {
        val textOfGoogleButton = loginBinding.btSgnGoogle.getChildAt(0) as TextView
        textOfGoogleButton.text = "Google"
        textOfGoogleButton.textSize=17F
        val typeface = ResourcesCompat.getFont(this, R.font.google_sans_medium)
        textOfGoogleButton.typeface = typeface
    }

    private fun isDetailsNotEmpty(
        loginEmail: String,
        loginPassword: String
    ): Boolean {
        var valid = true
        if (TextUtils.isEmpty(loginEmail)) {
            loginBinding.etLoginEmail.error = "Email cannot be empty"
            valid = false
        }

        if (TextUtils.isEmpty(loginPassword)) {
            loginBinding.etLoginPassword.error = "Password cannot be empty"
            valid = false
        }
        return valid
    }

    private fun signInWithFireBase(userEmail: String, userPassword: String) {
        loginBinding.btLogin.isClickable = false
        loginBinding.pbProgressLogin.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Login Successful.", Toast.LENGTH_SHORT,
                    ).show()
                    val intent = Intent(this@LoginActivity, MainActivity2::class.java)
                    startActivity(intent)
                    finish()

                } else {
                   
                    Toast.makeText(
                        applicationContext,
                        task.exception?.localizedMessage,Toast.LENGTH_SHORT,
                    ).show()

                }
            }
    }
    // will help to make the user directly go to main screen if already login
    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null) {
            val intent = Intent(this@LoginActivity, MainActivity2::class.java)
            startActivity(intent)
            finish()
        }
    }

}
