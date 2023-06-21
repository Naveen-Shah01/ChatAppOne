package com.example.chatappone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappone.databinding.ItemRecivedMessageBinding
import com.example.chatappone.databinding.ItemSentMessageBinding
import com.example.chatappone.models.MessagesEntity
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

// TODO add a message delete feature
// adpater senderoom and adapterroom both string type 1.54
class MessageAdapter(
    var context: Context,
    var messageList: ArrayList<MessagesEntity>,
    var friendImageUrl: String,
    var userImageUrl: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class SentViewHolder(val sentAdapterBinding: ItemSentMessageBinding) :
        RecyclerView.ViewHolder(sentAdapterBinding.root) {
        val sentMessage = sentAdapterBinding.tvSenderMessage
        val userImage = sentAdapterBinding.ivUserImageItem
    }

    inner class ReceivedViewHolder(val receivedAdapterBinding: ItemRecivedMessageBinding) :
        RecyclerView.ViewHolder(receivedAdapterBinding.root) {
        val receivedMessage = receivedAdapterBinding.tvReceiverMessage
        val friendImage = receivedAdapterBinding.ivFriendImageItem
    }

    val ITEM_SENT = 2
    val ITEM_RECEIVE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            //inflate receive
            return ReceivedViewHolder(
                ItemRecivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            // inflate sent
            return SentViewHolder(
                ItemSentMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]
        if (holder.javaClass == SentViewHolder::class.java) {
            val viewHolder = holder as SentViewHolder
            viewHolder.sentMessage.text = currentMessage.message

             if (userImageUrl.isNotEmpty()) {
                Picasso.get().load(userImageUrl).into(viewHolder.userImage)
            }
        } else {
            // for receivedView Holder
            val viewHolder = holder as ReceivedViewHolder
            viewHolder.receivedMessage.text = currentMessage.message
            if (friendImageUrl.isNotEmpty()) {
                Picasso.get().load(friendImageUrl).into(viewHolder.friendImage)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            return ITEM_SENT
        } else {
            return ITEM_RECEIVE
        }
    }


}