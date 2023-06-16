package com.example.chatappone.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappone.ChatActivity
import com.example.chatappone.models.UsersEntity
import com.example.chatappone.databinding.UserItemBinding


class UsersAdapter(
    var context: Context,
    var userList: ArrayList<UsersEntity>) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    inner class ViewHolder(val adapterBinding: UserItemBinding) :
        RecyclerView.ViewHolder(adapterBinding.root) {
        val tvName = adapterBinding.tvName
        val tvDescription = adapterBinding.tvDescription
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            UserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentUser = userList[position]

        holder.tvName.text = currentUser.userName

        holder.adapterBinding.cdCardViewItems.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("id", currentUser.userId)
            intent.putExtra("name", currentUser.userName)
//            intent.putExtra("mail", allUsers.userEmail)
//            intent.putExtra("phoneNumber", allUsers.userPhoneNumber)
//            intent.putExtra("imageUrl",allUsers.url)
//            intent.putExtra("imageName",allUsers.imageName)
//
            context.startActivity(intent)
       }
    }

    override fun getItemCount(): Int {
        return userList.size
    }


//    fun getUserId(position: Int): String {
//        return userList[position].userId
//    }

}










