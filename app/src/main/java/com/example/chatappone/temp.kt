package com.example.chatappone
//
//import android.Manifest
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.Uri
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.provider.MediaStore
//import android.view.View
//import android.widget.Toast
//import androidx.activity.result.ActivityResultCallback
//import androidx.activity.result.ActivityResultLauncher
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.example.fbone.databinding.ActivityUserBinding
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.StorageReference
//import com.squareup.picasso.Picasso
//import java.util.UUID
//
//class UserActivity : AppCompatActivity() {
//    lateinit var binding: ActivityUserBinding
//    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
//    private val myReference: DatabaseReference = database.reference.child("NewUsers")
//
//    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
//    private var imageUri: Uri? = null
//    private val fireBaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
//    private val storageReference: StorageReference = fireBaseStorage.reference
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityUserBinding.inflate(layoutInflater)
//        val view = binding.root
//        setContentView(view)
//
//        // helps to use our own action bar that is toolbar
//        setSupportActionBar(binding.toolBarUsersActivity)
//
//        if (supportActionBar != null) {
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//            supportActionBar?.title = "ADD USER" // will change the title of action bar
//        }
//
//        //register the launcher
//        registerActivityForResult()
//
//
//        // this will lead us to the main screen as exercise activity is already finished
//        binding.toolBarUsersActivity.setNavigationOnClickListener {
//            onBackPressed()
//        }
//        binding.btButton.setOnClickListener {
//            uploadImage()
//        }
//        binding.ivAddImage.setOnClickListener {
//            chosePhotoFromGallery()
//        }
//
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
//                                .into(binding.ivAddImage)
//                        }
//
//                    }
//                })
//
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
//            getIntent.type = "image/*"
//
//            val pickIntent =
//                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            // pickIntent.type = "image/*"
//
//            // help to choose different apps available to select image
//            val chooserIntent = Intent.createChooser(getIntent, "Select Image from...")
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
//
//            activityResultLauncher.launch(chooserIntent)
//        }
//    }
//
//    private fun chosePhotoFromGallery() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                1
//            )
//        } else {
//            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
//            getIntent.type = "image/*"
//
//            val pickIntent =
//                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            // pickIntent.type = "image/*"
//
//            val chooserIntent = Intent.createChooser(getIntent, "Select Image from...")
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
//
//            activityResultLauncher.launch(chooserIntent)
//
//
//        }
//    }
//
//    private fun addUserToFirebaseDatabase(url: String ,imageName : String) {
//        val name: String = binding.etName.text.toString()
//        val mail: String = binding.etMail.text.toString()
//        val number: String = binding.etPhoneNumber.text.toString()
//        val id: String = myReference.push().key.toString()
//
//        val user = UsersEntity(id, name, mail, number, url,imageName)
//        myReference.child(id).setValue(user).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Toast.makeText(applicationContext, "User Added To Firebase", Toast.LENGTH_LONG)
//                    .show()
//
//                binding.btButton.isClickable = true
//                binding.pbUploadData.visibility = View.INVISIBLE
//
//                finish()
//            } else {
//                Toast.makeText(applicationContext, task.exception.toString(), Toast.LENGTH_LONG)
//                    .show()
//            }
//        }
//    }
//
//    private fun uploadImage() {
//        binding.btButton.isClickable = false
//        binding.pbUploadData.visibility = View.VISIBLE
//
//        //UUID
//        val imageName = UUID.randomUUID().toString()
//
//        val imageReference = storageReference.child("images").child(imageName)
//        imageUri?.let { uri ->
//            imageReference.putFile(uri).addOnSuccessListener {
//
//                Toast.makeText(applicationContext, "Image Uploaded", Toast.LENGTH_SHORT).show()
//
//                //downloadable url
//                val myUploadedImageReference = storageReference.child("images").child(imageName)
//                myUploadedImageReference.downloadUrl.addOnSuccessListener { url ->
//                    val imageUrl = url.toString()
//
//                    addUserToFirebaseDatabase(imageUrl,imageName)
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
//}