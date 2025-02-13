package com.example.internshipapp.domain.repository

import com.example.internshipapp.domain.models.AddedData
import com.example.internshipapp.domain.models.Resource
import kotlinx.coroutines.flow.Flow

interface HomeScreenRepository {

     fun getCafesList(): Flow<List<AddedData>>

    fun getEventsList():Flow<List<AddedData>>

    fun getPlacesList():Flow<List<AddedData>>

    fun updateDestination(newData:AddedData):Flow<Resource<Unit>>

}