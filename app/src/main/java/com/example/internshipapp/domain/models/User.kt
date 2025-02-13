package com.example.internshipapp.domain.models

data class User(
    val userName:String?="",
    val userEmail:String?="",
    val userPass:String?="",
    val userUid:String?=""
){
    constructor():this("","","","")
}
