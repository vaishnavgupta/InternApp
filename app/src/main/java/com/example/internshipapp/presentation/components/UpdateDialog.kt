package com.example.internshipapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun UpdateDialog(
    isOpen:Boolean,
    title:String="Update Destination",
    destTitle:String,
    mapsLink:String,
    onMapLinkChange:(String)->Unit,
    onDismissRequest:()->Unit,
    onConfirmClick:()->Unit,
    isLoading:Boolean=false,
) {

    var mapsLinkError by rememberSaveable { mutableStateOf<String?>(null) }
    mapsLinkError =when{
        mapsLink.isBlank() -> "Please enter the link."
        !isValidMapsUrl(mapsLink) -> "Enter valid Link."
        else -> null
    }

    if(isOpen){
        AlertDialog(onDismissRequest=onDismissRequest,
            title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Text(
                        text = destTitle,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = mapsLink,
                        onValueChange = onMapLinkChange,
                        label = { Text(text = "Maps Link") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        isError = (mapsLink.isNotBlank() && mapsLinkError!=null),
                        supportingText = { Text(text = mapsLinkError.orEmpty()) }
                    )
                    if(isLoading){
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(80.dp)
                                .padding(10.dp)
                                .align(Alignment.CenterHorizontally),
                            strokeCap = StrokeCap.Round,
                            strokeWidth = 6.dp
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest, enabled = (!isLoading)) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmClick,
                    enabled = (mapsLinkError==null && (!isLoading))
                ) {
                    Text(text = "Save")
                }
            }
        )
    }
}

private fun isValidMapsUrl(link:String):Boolean{
    val urlRegex="""^(https?|ftp)://[\w\-]+(\.[\w\-]+)+[/#?]?.*$""".toRegex()
    return urlRegex.matches(link)
}