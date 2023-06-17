package com.example.chatappone.models

class UsersEntity {
    var userId: String ?=null
    var userName: String?=null
    var userEmail: String ?=null
    var url: String?=null
    var userProfile : String?=null

    constructor()
    constructor(userName:String?,userEmail:String?,userId:String?){
        this.userName=userName
        this.userEmail=userEmail
        this.userId = userId
    }
    constructor(userId:String?,userProfile:String?){
    }
}
