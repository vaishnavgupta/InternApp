package com.example.internshipapp.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun EditDialog(
    isOpen:Boolean,
    bodyText:String,
    onDismissRequest:()->Unit,
    onConfirmClick:()->Unit
) {

    if(isOpen){
        AlertDialog(onDismissRequest=onDismissRequest,
            title = { Text(text = "Edit Destination") },
            text = {
                Text(text = bodyText)
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmClick,
                ) {
                    Text(text = "Save")
                }
            }
        )
    }
}