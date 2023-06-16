package com.example.chatappone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatappone.databinding.ActivityChatUsersBinding
import com.example.chatappone.databinding.ActivityProfileBinding
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    lateinit var galleryActivityResultLauncher: ActivityResultLauncher<Intent>
    //TODO edit name,profile in this activity
    private lateinit var profileBinding: ActivityProfileBinding
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        val view = profileBinding.root
        setContentView(view)
        toolBarSetUp()

        //register the launcher
        registerActivityForResult()

        profileBinding.iBtnChooseImage.setOnClickListener {
            //TODO ask gallery permission
            chooseImageFromGallery()
        }

    }

    private fun chooseImageFromGallery() {

        // TODO handle the event when user denies the permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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