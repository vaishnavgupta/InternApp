package com.example.internshipapp.presentation.adddestination

import com.example.internshipapp.domain.models.Destinations

sealed class AddDestScreenEvent {
    data class onTitleChange(val title:String):AddDestScreenEvent()

    data class onDescChange(val desc:String):AddDestScreenEvent()

    data class onDateChange(val millis: Long?):AddDestScreenEvent()

    data class onMapsLinkChange(val link:String):AddDestScreenEvent()

    data class onRelatedToDestChange(val destination: Destinations):AddDestScreenEvent()


    data object saveDestination:AddDestScreenEvent()

    data object deleteDestination:AddDestScreenEvent()
}