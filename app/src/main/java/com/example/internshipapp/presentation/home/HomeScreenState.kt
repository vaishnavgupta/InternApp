package com.example.internshipapp.presentation.home

import com.example.internshipapp.domain.models.AddedData

data class HomeScreenState(
    val cafesList:List<AddedData> = emptyList(),
    val eventsList:List<AddedData> = emptyList(),
    val placesList:List<AddedData> = emptyList(),
    val destination:AddedData?=null,
    val isLoading:Boolean=false,
    val isError:Boolean=false,
    val isSuccess:Boolean=false
)
