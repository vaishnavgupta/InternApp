package com.example.internshipapp.presentation.registration

sealed class RegistrationScreenEvent {

    data class onUserNameChanged(val name:String): RegistrationScreenEvent()
    data class onUserEmsilChanged(val email:String): RegistrationScreenEvent()
    data class onUserPasswordChanged(val password:String): RegistrationScreenEvent()

    data object registerUser: RegistrationScreenEvent()

}