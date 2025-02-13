package com.example.internshipapp.domain.repository

import com.example.internshipapp.domain.models.Resource
import com.example.internshipapp.domain.models.User
import kotlinx.coroutines.flow.Flow

interface SettingsScreenRepo {
    fun fetchUserDetails():Flow<User>

    fun updateUserDetails(newUser: User):Flow<Resource<Unit>>

    fun sendInviteSMS(number:String):Flow<Resource<Unit>>

    fun logout():Flow<Resource<Unit>>

}