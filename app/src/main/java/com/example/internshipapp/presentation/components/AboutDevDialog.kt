package com.example.internshipapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AboutDevDialog(
    isOpen:Boolean,
    title:String="About Me",
    onDismissRequest:()->Unit,
    onConfirmClick:()->Unit,

) {

    if(isOpen){
        AlertDialog(onDismissRequest=onDismissRequest,
            title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Text(
                        text = "Hi, I am Vaishnav Gupta.\n" +
                                "Passionate Android App Developer with about 1 year of experience. Currently working on the" +
                                " Recruitments App and having a good hands on technologies like Android, Kotlin, Firebase, Retrofit" +
                                " and many more. Proven ability to provide guidance and support, while continuously enhancing" +
                                " technical skills through practical application. Eager to contribute expertise and collaborate on" +
                                " challenging projects within a dynamic team environment.",
                        style = MaterialTheme.typography.titleMedium
                    )

                }
            },

            confirmButton = {
                TextButton(
                    onClick = onConfirmClick,
                ) {
                    Text(text = "OK")
                }
            }
        )
    }
}