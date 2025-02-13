package com.example.internshipapp.data.repository

import com.example.internshipapp.domain.models.AddedData
import com.example.internshipapp.domain.models.Resource
import com.example.internshipapp.domain.models.User
import com.example.internshipapp.domain.repository.AuthRepository
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth:FirebaseAuth,
    private val firestore: FirebaseFirestore
):AuthRepository {
    override fun registerUser(email: String, password: String, name:String): Flow<Resource<AuthResult>> {
        return flow {
            emit(value = Resource.Loading())
            val res = auth.createUserWithEmailAndPassword(email,password).await()
            val userUid=res.user?.uid ?: throw Exception("User UID is null")
            val user = User(
                userEmail = email,
                userName = name,
                userPass = password,
                userUid = userUid
            )
            firestore.collection("Users").document(userUid).set(user).await()
            emit(value = Resource.Success(res))
        }.catch {
            emit(value = Resource.Error(it.message.toString()))
        }
    }

    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(value = Resource.Loading())
            val res = auth.signInWithEmailAndPassword(email,password).await()
            emit(value = Resource.Success(res))
        }.catch {
            emit(value = Resource.Error(it.message.toString()))
        }
    }

    override fun addDestination( userId:String,addedData: AddedData):Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading()) // Emit loading state
            firestore.collection("All Destinations").document(addedData.destination)
                .collection("All").add(addedData).await()
            emit(Resource.Success(Unit)) // Emit success state
        }.catch { e ->
            emit(Resource.Error(e.message ?: "Unknown error")) // Emit error state
        }

    }


}