package com.example.chatappone

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatappone.authentication.LoginActivity
import com.example.chatappone.databinding.ActivityProfileBinding
import com.example.chatappone.models.UsersEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private lateinit var galleryActivityResultLauncher: ActivityResultLauncher<Intent>

    //TODO edit name,profile in this activity
    // TODO add delete account functionality
    private lateinit var profileBinding: ActivityProfileBinding

    private var imageUri: Uri? = null
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference.child("Users")
    private val fireBaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = fireBaseStorage.reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        val view = profileBinding.root
        setContentView(view)
        toolBarSetUp()

        //register the launcher
        registerActivityForResult()

        retrieveData()


        profileBinding.iBtnChooseImage.setOnClickListener {
            //TODO ask gallery permission
            chooseImageFromGallery()
        }

        profileBinding.iBtnLogoutProfile.setOnClickListener {
            showAlertDialogLogOut()
        }
        profileBinding.btUpdate.setOnClickListener {
//            val userNameProfile = profileBinding.etProfileUserName.text.toString()
//
//                updateData(userNameProfile)
            }

profileBinding.btDeleteProfile.setOnClickListener {
    // TODO delete user account
}
    }

    private fun showAlertDialogLogOut() {
        val logOutDialog = AlertDialog.Builder(this)
        logOutDialog.setTitle("Logout")
        logOutDialog.setIcon(R.drawable.logout_icon)
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
        // TODO direct goto start activity and destroy all previous one

        val intent = Intent(this, LoginActivity::class.java)
// set the new task and clear flags
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun retrieveData() {
        currentUser = auth.currentUser!!

        myReference.child(currentUser.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UsersEntity::class.java)
                if (user != null) {
                    profileBinding.etProfileUserName.setText(user.userName)

                    if (user.profileImageUrl != "") {
                        Picasso.get().load(user.profileImageUrl)
                            .into(profileBinding.iBtnChooseImage)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun updateData(userNameProfile: String) {
        // if image and name is empty or name is empty
        if(imageUri==null&&userNameProfile.isEmpty()||userNameProfile.isEmpty()){
            return
        }
        if(userNameProfile.isNotEmpty()&&imageUri==null){
            // TODO update user Name
        }
        if(imageUri!=null && userNameProfile.isNotEmpty()){
            // TODO update username and image
        }
//        profileBinding.btUpdate.isClickable = false
//        profileBinding.pbPrgressBarProfile.visibility = View.VISIBLE


        //UUID
        val imageName = intent.getStringExtra("imageName").toString()

        val imageReference = storageReference.child("images").child(imageName)
        imageUri?.let { uri ->
            imageReference.putFile(uri).addOnSuccessListener {

                Toast.makeText(applicationContext, "Image Updated", Toast.LENGTH_SHORT).show()

                //downloadable url
                val myUploadedImageReference = storageReference.child("images").child(imageName)
                myUploadedImageReference.downloadUrl.addOnSuccessListener { url ->
                    val imageUrl = url.toString()

                    //updateData(imageUrl,imageName)

                }
            }

        }?.addOnFailureListener {
            Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }

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
                                .into(profileBinding.iBtnChooseImage)
                        }
                    }
                })
    }

    private fun toolBarSetUp() {
        // helps to use our own action bar that is toolbar
        setSupportActionBar(profileBinding.toolBarProfileActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // this will lead us to the main screen as exercise activity is already finished
        profileBinding.toolBarProfileActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}