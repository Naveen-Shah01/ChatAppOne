package com.example.chatappone

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatappone.Adapter.UsersAdapter
import com.example.chatappone.authentication.LoginActivity
import com.example.chatappone.databinding.ActivityChatUsersBinding
import com.example.chatappone.models.UsersEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ChatUsersActivity : AppCompatActivity() {

    //TODO Image Changes from profile activity are not reflecting

    //TODO add a loader of refresh button to load the profile changes that have been done in profile activity

    //TODO create a latest message feature
    private lateinit var chatUsersBinding: ActivityChatUsersBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference.child("Users")

    private val usersList = ArrayList<UsersEntity>()
    lateinit var usersAdapter: UsersAdapter
    private var userProfileImageUrl: String = ""
    private var imageName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatUsersBinding = ActivityChatUsersBinding.inflate(layoutInflater)
        val view = chatUsersBinding.root
        setContentView(view)

        toolBarSetUp()
        retrieveDataFromFirebaseDatabase()

        chatUsersBinding.iBtnLogout.setOnClickListener {
            //TODO Finish all previous activities when logout
            showAlertDialogLogOut()

        }

        chatUsersBinding.iBtnUserChatUsers.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)

//            intent.putExtra("imageUrl",allUsers.url)
//            intent.putExtra("imageName",allUsers.imageName)
            startActivity(intent)
        }


    }

    private fun retrieveUserProfileImage() {
        myReference.child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UsersEntity::class.java)
                    if (user != null) {
                        if (user.profileImageUrl != "") {

                            Picasso.get().load(user.profileImageUrl)
                                .into(chatUsersBinding.iBtnUserChatUsers)
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun retrieveDataFromFirebaseDatabase() {
        //retrieveUserProfileImage()
        myReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersList.clear()
                for (eachUser in snapshot.children) {
                    val user = eachUser.getValue(UsersEntity::class.java)

                    if (user != null) {
                        if (auth.currentUser?.uid != user.userId)
                            usersList.add(user)
                        else {
                            userProfileImageUrl = user.profileImageUrl
                            imageName = user.imageName
                            if (user.profileImageUrl != "") {

                                Picasso.get().load(user.profileImageUrl)
                                    .into(chatUsersBinding.iBtnUserChatUsers)
                            }
                        }
                    }

                }
                setUpRecyclerView()

                usersAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }
        })
        // setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        Log.d("error", "Link : $userProfileImageUrl")
        chatUsersBinding.rvRecyclerView.layoutManager = LinearLayoutManager(this@ChatUsersActivity)
        usersAdapter = UsersAdapter(this@ChatUsersActivity, usersList, userProfileImageUrl)
        chatUsersBinding.rvRecyclerView.adapter = usersAdapter
    }

    /** alert Dialog for logout */
    private fun showAlertDialogLogOut() {
        val logOutDialog = AlertDialog.Builder(this)
        logOutDialog.setTitle("Logout")
        logOutDialog.setIcon(R.drawable.logout_icon)
        logOutDialog.setMessage("Do you really want to log out")
        logOutDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
            auth.signOut()

            startLoginActivity()

            Toast.makeText(applicationContext, "Logged Out", Toast.LENGTH_SHORT)
                .show()
        })
        logOutDialog.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel()
        })

        logOutDialog.create().show()
    }


    /** when logout it will move to login activity */
    private fun startLoginActivity() {
        // TODO direct goto start activity and destroy all previous one

        val intent = Intent(this, LoginActivity::class.java)
    // set the new task and clear flags
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun toolBarSetUp() {
        // helps to use our own action bar that is toolbar
        setSupportActionBar(chatUsersBinding.toolBarUsersActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // this will lead us to the main screen as exercise activity is already finished
        chatUsersBinding.toolBarUsersActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}