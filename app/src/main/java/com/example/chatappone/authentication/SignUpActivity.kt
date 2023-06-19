package com.example.chatappone.authentication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatappone.models.UsersEntity
import com.example.chatappone.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.UUID


//TODO add method for google signup in signup activity also change google sign in button text
// TODO add a method to check if mail id are valid or not

// TODO add internet permission check separately

class SignUpActivity : AppCompatActivity() {
    private lateinit var signUpBinding: ActivitySignUpBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference.child("Users")
    private val fireBaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = fireBaseStorage.reference
    lateinit var galleryActivityResultLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null
    private var profileImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = signUpBinding.root
        setContentView(view)

        toolBarSetUp()

        //register the launcher
        registerActivityForResult()

        signUpBinding.btSignUp.setOnClickListener {
            signUpStart()
        }
        signUpBinding.tvLoginFromSignUp.setOnClickListener {
            // it will automatically destroy activity.
            onBackPressedDispatcher.onBackPressed()
        }
        signUpBinding.iBtnChooseImageRegister.setOnClickListener {
            chooseImageFromGallery()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // pickIntent.type = "image/*"

            // help to choose different apps available to select image
            val chooserIntent = Intent.createChooser(getIntent, "Select Image from...")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            galleryActivityResultLauncher.launch(chooserIntent)
        }
    }

    private fun registerActivityForResult() {
        galleryActivityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(), ActivityResultCallback { result ->
                    val resultCode = result.resultCode
                    val imageData = result.data
                    if (resultCode == RESULT_OK && imageData != null) {
                        imageUri = imageData.data
                        imageUri?.let {
                            Picasso.get().load(it)
                                .into(signUpBinding.iBtnChooseImageRegister)
                        }
                    }
                })
    }

    private fun chooseImageFromGallery() {

        // TODO handle the event when user denies the permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
            )
        } else {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // pickIntent.type = "image/*"
            val chooserIntent = Intent.createChooser(getIntent, "Select Image from...")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            galleryActivityResultLauncher.launch(chooserIntent)
        }
    }

    private fun signUpStart() {
        val signUpEmail: String = signUpBinding.etEmailSignUp.text.toString()
        val signUpName: String = signUpBinding.etNameSignUp.text.toString()
        val signUpPassword: String = signUpBinding.etPasswordSignUp.text.toString()

        if (isDetailsNotEmpty(signUpEmail, signUpName, signUpPassword)) {
            signUpWithFireBase(signUpEmail, signUpPassword, signUpName)
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

                    val userId: String = auth.currentUser?.uid!!

                    uploadImage(userId,userName,userEmail)

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

    private fun addUserToDatabase(userId: String, userName: String, userEmail: String, profileImageUrl: String) {
        val user = UsersEntity(userId, userName, userEmail,profileImageUrl)
        myReference.child(userId).setValue(user)

    }

    private fun uploadImage(userId: String, userName: String, userEmail: String) {
        if (imageUri == null) {
            Log.e("error", "File link: $profileImageUrl")
            addUserToDatabase(userId, userName, userEmail,profileImageUrl)
            return
        }
        //UUID
        val imageName = UUID.randomUUID().toString()
        // var urlsend: String = ""
        val imageReference = storageReference.child("images").child(imageName)

        imageReference.putFile(imageUri!!).addOnSuccessListener {
//            Toast.makeText(applicationContext, "Image Uploaded", Toast.LENGTH_SHORT).show()
            imageReference.downloadUrl.addOnSuccessListener { uri ->
                profileImageUrl = uri.toString()
                Log.d("error", "File link: $profileImageUrl")
                addUserToDatabase(userId, userName, userEmail,profileImageUrl)
            }
        }


    }


//    private fun addUserToDatabase(userId: String, userMap: MutableMap<String, String>) {
//        myReference.child(userId).setValue(userMap)
//    }


}