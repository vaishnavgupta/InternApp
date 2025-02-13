package com.example.internshipapp.presentation.settings

import com.example.internshipapp.domain.models.User

data class SettingsScreenState(
    val newUserPassword:String="",
    val friendMobile:String="",
    val userData:User?=null,
    val newUserNmae:String="",
    val isSignOut:Boolean=false
)
