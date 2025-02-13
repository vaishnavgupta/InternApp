package com.example.internshipapp.data.repository

import android.telephony.SmsManager
import android.util.Log
import com.example.internshipapp.domain.models.Resource
import com.example.internshipapp.domain.models.User
import com.example.internshipapp.domain.repository.SettingsScreenRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SettingScreenRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
):SettingsScreenRepo{

    private val userDetails = MutableStateFlow<User?>(null)
    private val userUid= auth.currentUser?.uid
    private var userListener:ListenerRegistration?=null

    init {
        fetchDetails()
    }


    private fun fetchDetails(){
        userListener?.remove()
        if (userUid != null) {
            firestore.collection("Users").document(userUid)
                .addSnapshotListener{ docx , error ->
                    if (error != null) {
                        Log.e("Firestore", "Error fetching event", error)
                        return@addSnapshotListener
                    }
                    if(docx!=null && docx.exists()){
                        val user = docx.toObject(User::class.java)
                        userDetails.value = user
                    }
                }
        }
    }

    override fun fetchUserDetails(): Flow<User> {
        return userDetails.filterNotNull()
    }

    override fun updateUserDetails(newUser: User): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())
            if (userUid != null) {
                firestore.collection("Users").document(userUid)
                    .update("userName", newUser.userName)
                    .await()
                emit(Resource.Success(Unit))
            }
        }.catch { e ->
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override fun sendInviteSMS(number: String): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())
            val smsManager = SmsManager.getDefault()
            val msg = "Come and add your favourite destinations on our app.\nApp Link: Available Soon\nFrom: Internship App"
            smsManager.sendTextMessage(
                "+91$number",null,msg,null,null
            )
            emit(Resource.Success(Unit))
        }.catch { e ->
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override fun logout(): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())
            Log.d("Logout", "Before signOut: ${auth.currentUser?.uid}")
            auth.signOut()
            Log.d("Logout", "After signOut: ${auth.currentUser?.uid}")
            emit(Resource.Success(Unit))
        }.catch { e ->
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }
}