package com.example.internshipapp.presentation.registration

data class RegisterScreenState(
    val userName:String = "",
    val userEmail:String = "",
    val userPassword:String = "",
    val isSuccessful : Boolean = false,
    val isError : Boolean = false,
    val isLoading:Boolean=false,

    )
