package com.example.internshipapp.utils

import androidx.compose.material3.SnackbarDuration
import com.example.internshipapp.domain.models.Destinations
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

sealed class SnackBarEvent{
    data class ShowSnackbar(
        val msg:String,
        val snackBarDuration: SnackbarDuration = SnackbarDuration.Short
    ):SnackBarEvent()

    data object navigateUp:SnackBarEvent()
}

fun Long?.changeMillisToString():String{
    val date: LocalDate =this?.let{
        Instant
            .ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }?: LocalDate.now()   //if fails to convert return today date
    return date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
}