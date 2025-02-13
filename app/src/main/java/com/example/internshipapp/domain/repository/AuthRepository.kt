package com.example.internshipapp.domain.repository

import com.example.internshipapp.domain.models.AddedData
import com.example.internshipapp.domain.models.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun registerUser(
         email:String,
         password:String,
         name:String
    ):Flow<Resource<AuthResult>>

    fun loginUser(
        email:String,
        password:String
    ):Flow<Resource<AuthResult>>

     fun addDestination(
         userId:String,
        addedData: AddedData
    ):Flow<Resource<Unit>>
}