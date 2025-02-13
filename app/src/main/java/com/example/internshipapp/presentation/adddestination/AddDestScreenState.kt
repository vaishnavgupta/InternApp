package com.example.internshipapp.presentation.adddestination

data class AddDestScreenState(
    val title:String="",
    val desc:String="",
    val mapsLink:String="",
    val date:Long?=null,
    val relatedToDest:String?=null,
    val isLoading:Boolean=false,
    val isSuccessfully:Boolean=false,
)
