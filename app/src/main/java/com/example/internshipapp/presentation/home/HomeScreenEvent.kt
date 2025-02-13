package com.example.internshipapp.presentation.home

import com.example.internshipapp.domain.models.AddedData

sealed class HomeScreenEvent {
    data object onSaveDestClick:HomeScreenEvent()
    data class onMapsLinkChange(val link:String):HomeScreenEvent()
    data class onDestCardClick(val data:AddedData):HomeScreenEvent()
    data object resetBooleans:HomeScreenEvent()
}