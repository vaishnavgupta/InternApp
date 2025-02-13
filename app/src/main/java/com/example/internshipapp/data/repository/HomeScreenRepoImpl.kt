package com.example.internshipapp.data.repository

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.example.internshipapp.domain.models.AddedData
import com.example.internshipapp.domain.models.Resource
import com.example.internshipapp.domain.repository.HomeScreenRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

 class HomeScreenRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore
):HomeScreenRepository {

     private val cafesListFlow = MutableStateFlow<List<AddedData>>(emptyList())
     private val eventsListFlow = MutableStateFlow<List<AddedData>>(emptyList())
     private val placesListFlow = MutableStateFlow<List<AddedData>>(emptyList())

     init {
         fetchCafesList()
         fetchEventsList()
         fetchPlacesList()
     }

     private fun fetchCafesList() {
         firestore.collection("All Destinations")
             .document("Cafe") // This is your "Cafe" document
             .collection("All") // This contains all cafes
             .addSnapshotListener { value, error ->
                 if (error != null) {
                     Log.e("Firestore", "Error fetching cafes", error)
                     return@addSnapshotListener
                 }

                 val cafes = value?.documents?.mapNotNull {document->
                     document.toObject(AddedData::class.java) ?. copy(destId = document.id)
                 } ?: emptyList()
                 cafesListFlow.value = cafes
             }
     }
     private fun fetchEventsList() {
         firestore.collection("All Destinations")
             .document("Event") // This is your "Cafe" document
             .collection("All") // This contains all cafes
             .addSnapshotListener { value, error ->
                 if (error != null) {
                     Log.e("Firestore", "Error fetching event", error)
                     return@addSnapshotListener
                 }

                 val cafes = value?.documents?.mapNotNull { it.toObject(AddedData::class.java)?. copy(destId = it.id) } ?: emptyList()
                 eventsListFlow.value = cafes
             }
     }
     private fun fetchPlacesList() {
         firestore.collection("All Destinations")
             .document("Place") // This is your "Cafe" document
             .collection("All") // This contains all cafes
             .addSnapshotListener { value, error ->
                 if (error != null) {
                     Log.e("Firestore", "Error fetching place", error)
                     return@addSnapshotListener
                 }

                 val cafes = value?.documents?.mapNotNull { it.toObject(AddedData::class.java)?. copy(destId = it.id) } ?: emptyList()
                 placesListFlow.value = cafes
             }
     }

     override  fun getCafesList(): Flow<List<AddedData>> {
         return cafesListFlow
     }

     override fun getEventsList(): Flow<List<AddedData>> {
         return eventsListFlow
     }

     override fun getPlacesList(): Flow<List<AddedData>> {
         return placesListFlow
     }

     override fun updateDestination(newData: AddedData): Flow<Resource<Unit>> {
         return flow {
             emit(Resource.Loading())
             firestore.collection("All Destinations")
                 .document(newData.destination)
                 .collection("All")
                 .document(newData.destId)
                 .update("mapsLink",newData.mapsLink)
                 .await()
             emit(Resource.Success(Unit))
         }.catch { e ->
             emit(Resource.Error(e.message ?: "Unknown error"))
         }
     }
 }
