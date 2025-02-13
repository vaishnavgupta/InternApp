package com.example.internshipapp.presentation.login

import com.example.internshipapp.presentation.registration.RegistrationScreenEvent

sealed class LoginScreenEvent {
    data class onUserEmsilChanged(val email:String): LoginScreenEvent()
    data class onUserPasswordChanged(val password:String): LoginScreenEvent()

    data object loginUser: LoginScreenEvent()
}