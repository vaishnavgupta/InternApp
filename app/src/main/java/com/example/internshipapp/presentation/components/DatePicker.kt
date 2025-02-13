package com.example.internshipapp.presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    state: DatePickerState,
    isOpen:Boolean,
    confirmBtnText:String="OK",
    cancelBtnText:String="Cancel",
    onDismissRequest:()->Unit,
    onConfirmBtnClick:()->Unit
) {
    if(isOpen){
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = onConfirmBtnClick) {
                    Text(text = confirmBtnText)
                }
            },
            dismissButton = {
                TextButton(onClick = onConfirmBtnClick) {
                    Text(text = cancelBtnText)
                }
            },
            content = { DatePicker(
                state=state
            ) }
        )
    }
}