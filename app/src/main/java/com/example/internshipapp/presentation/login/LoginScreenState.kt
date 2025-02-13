package com.example.internshipapp.presentation.login

data class LoginScreenState(
    val userEmail:String = "",
    val userPassword:String = "",
    val isLoading:Boolean=false,
    val isSuccess:Boolean=false
)
