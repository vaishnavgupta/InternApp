package com.example.internshipapp.domain.models

data class AddedData(
    val destId:String="",
    val destination:String="",
    val title:String="",
    val desc:String="",
    val mapsLink:String="",
    val date:String=""
){
    constructor() : this("", "", "", "", "","")
}

