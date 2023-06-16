package com.example.chatappone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappone.databinding.ItemRecivedMessageBinding
import com.example.chatappone.databinding.ItemSentMessageBinding
import com.example.chatappone.databinding.UserItemBinding
import com.example.chatappone.models.MessagesEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(
    var context: Context,
    var messageList: ArrayList<MessagesEntity>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class SentViewHolder(val sentAdapterBinding: ItemSentMessageBinding) :
        RecyclerView.ViewHolder(sentAdapterBinding.root) {
        val sentMessage = sentAdapterBinding.tvSenderMessage
    }

    inner class ReceivedViewHolder(val receivedAdapterBinding: ItemRecivedMessageBinding) :
        RecyclerView.ViewHolder(receivedAdapterBinding.root) {
        val receivedMessage = receivedAdapterBinding.tvReceivedMessage
    }

    val ITEM_SENT =2
    val ITEM_RECEIVE=1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
     if(viewType==1){
         //inflate receive
         return ReceivedViewHolder(
             ItemRecivedMessageBinding.inflate(
                 LayoutInflater.from(parent.context),
                 parent,
                 false
             )
         )
     }
        else {
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
            holder.sentMessage.text = currentMessage.message
        } else {
            // for receivedView Holder
            val viewHolder = holder as ReceivedViewHolder
            holder.receivedMessage.text = currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        }
        else
        {
            return ITEM_RECEIVE
        }
    }


}