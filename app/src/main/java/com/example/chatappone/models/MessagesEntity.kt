package com.example.chatappone.models

class MessagesEntity {
    var message: String? = null
    var senderId: String? = null
    var id: String? = null
    var receiverId: String? = null
    var timeStamp: Long?=null

    constructor()
    constructor(id:String?,message: String?, senderId: String?,receiverId: String?,timeStamp: Long?) {
        this.message = message
        this.senderId = senderId
        this.id=id
        this.receiverId=receiverId
        this.timeStamp=timeStamp

    }
}