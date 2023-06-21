//package com.example.chatappone
//
//import android.content.Intent
//import android.net.Uri
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.provider.MediaStore
//import android.view.View
//import android.widget.Toast
//import androidx.activity.result.ActivityResultCallback
//import androidx.activity.result.ActivityResultLauncher
//import androidx.activity.result.contract.ActivityResultContracts
//import com.example.fbone.databinding.ActivityUpdateUserBinding
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.StorageReference
//import com.squareup.picasso.Picasso
//
//class UpdateUserActivity : AppCompatActivity() {
//    lateinit var updateUserBinding: ActivityUpdateUserBinding
//
//    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
//    val Myreference: DatabaseReference = database.reference.child("NewUsers")
//
//    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
//    private var imageUri: Uri? = null
//    private val fireBaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
//    private val storageReference: StorageReference = fireBaseStorage.reference
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        updateUserBinding = ActivityUpdateUserBinding.inflate(layoutInflater)
//        val view = updateUserBinding.root
//        setContentView(view)
//
//        // helps to use our own action bar that is toolbar
//        setSupportActionBar(updateUserBinding.toolBarUpdateUserActivity)
//
//        if (supportActionBar != null) {
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//            supportActionBar?.title = "UPDATE USER" // will change the title of action bar
//        }
//        // this will lead us to the main screen as exercise activity is already finished
//        updateUserBinding.toolBarUpdateUserActivity.setNavigationOnClickListener {
//            onBackPressed()
//        }
//
//
//        //register the launcher
//        registerActivityForResult()
//
//
//        getUpdateDataValue()
//
//
//        updateUserBinding.ivUpdateImage.setOnClickListener {
//            chosePhotoFromGallery()
//        }
//
//        updateUserBinding.btUpdateButton.setOnClickListener {
//            uploadImage()
//        }
//
//    }
//
//
//    private fun chosePhotoFromGallery() {
//        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
//        getIntent.type = "image/*"
//
//        val pickIntent =
//            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        // pickIntent.type = "image/*"
//
//        val chooserIntent = Intent.createChooser(getIntent, "Select Image from...")
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
//
//        activityResultLauncher.launch(chooserIntent)
//
//    }
//    private fun uploadImage() {
//        updateUserBinding.btUpdateButton.isClickable = false
//        updateUserBinding.pbUploadUpdateData.visibility = View.VISIBLE
//
//        //UUID
//        val imageName = intent.getStringExtra("imageName").toString()
//
//        val imageReference = storageReference.child("images").child(imageName)
//        imageUri?.let { uri ->
//            imageReference.putFile(uri).addOnSuccessListener {
//
//                Toast.makeText(applicationContext, "Image Updated", Toast.LENGTH_SHORT).show()
//
//                //downloadable url
//                val myUploadedImageReference = storageReference.child("images").child(imageName)
//                myUploadedImageReference.downloadUrl.addOnSuccessListener { url ->
//                    val imageUrl = url.toString()
//
//                    updateData(imageUrl,imageName)
//
//                }
//            }
//
//        }?.addOnFailureListener {
//            Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
//        }
//
//    }
//
//    private fun registerActivityForResult() {
//        activityResultLauncher =
//            registerForActivityResult(
//                ActivityResultContracts.StartActivityForResult(), ActivityResultCallback { result ->
//                    val resultCode = result.resultCode
//                    val imageData = result.data
//                    if (resultCode == RESULT_OK && imageData != null) {
//                        imageUri = imageData.data
//                        imageUri?.let {
//                            Picasso.get().load(it)
//                                .into(updateUserBinding.ivUpdateImage)
//                        }
//
//                    }
//                })
//
//    }
//
//
//    private fun updateData(imageUrl : String,imageName: String) {
//        val updatedName = updateUserBinding.etUpdateName.text.toString()
//        val updateMail = updateUserBinding.etUpdateMail.text.toString()
//        val updatePhoneNumber = updateUserBinding.etUpdatePhoneNumber.text.toString()
//        val userId = intent.getStringExtra("id").toString()
//
//
//        val userMap = mutableMapOf<String, Any>()
//
//        userMap["userId"] = userId
//        userMap["userName"] = updatedName
//        userMap["userEmail"] = updateMail
//        userMap["userPhoneNumber"] = updatePhoneNumber
//        userMap["url"] = imageUrl
//        userMap["imageName"] = imageName
//
//        Myreference.child(userId).updateChildren(userMap).addOnCompleteListener { task->
//            if (task.isSuccessful) {
//                Toast.makeText(applicationContext, "User Updated To Firebase", Toast.LENGTH_LONG)
//                    .show()
//                updateUserBinding.btUpdateButton.isClickable = true
//                updateUserBinding.pbUploadUpdateData.visibility = View.INVISIBLE
//                finish()
//            } else {
//                Toast.makeText(applicationContext, "Not Updated", Toast.LENGTH_LONG)
//                    .show()
//            }
//        }
//
//
//    }
//
//    private fun getUpdateDataValue() {
//        val name = intent.getStringExtra("name")
//        val mail = intent.getStringExtra("mail")
//        val phoneNumber = intent.getStringExtra("phoneNumber")
//        val imageUrl = intent.getStringExtra("imageUrl").toString()
//        Picasso.get().load(imageUrl).into(updateUserBinding.ivUpdateImage)
//
//
//        updateUserBinding.etUpdateName.setText(name)
//        updateUserBinding.etUpdateMail.setText(mail)
//        updateUserBinding.etUpdatePhoneNumber.setText(phoneNumber)
//    }
//}