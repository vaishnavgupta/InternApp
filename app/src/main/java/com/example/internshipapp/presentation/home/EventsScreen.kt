package com.example.internshipapp.presentation.home

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.internshipapp.presentation.components.CafesList
import com.example.internshipapp.presentation.components.UpdateDialog

@Composable
fun EventsScreen(modifier: Modifier) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isEditDestDialogOpen by rememberSaveable { mutableStateOf(false) }
    val eventsChanges = viewModel::manageEventChanges
    val context= LocalContext.current

    state.destination?.let {
        UpdateDialog(
            isOpen = isEditDestDialogOpen,
            destTitle = it.title,
            mapsLink = it.mapsLink,
            onMapLinkChange = { link->
                eventsChanges(HomeScreenEvent.onMapsLinkChange(link))
            },
            onDismissRequest = {
                isEditDestDialogOpen=false
            },
            onConfirmClick = {
                eventsChanges(HomeScreenEvent.onSaveDestClick)
                isEditDestDialogOpen=false
            }
        )
    }
    LazyColumn(
        modifier=modifier.fillMaxSize()
    ) {
        CafesList(
            sectiontitle = "Events",
            dataList = state.eventsList,
            onDestClick = {data->
                if(data!=null){
                    eventsChanges(HomeScreenEvent.onDestCardClick(data))
                    isEditDestDialogOpen=true
                }},
            emptyListMsg = "No Events Added.\nClick the + button to add new event."
        )
        if(state.isSuccess) {
            Toast.makeText(context,"Destination updated successfully", Toast.LENGTH_SHORT).show()
            eventsChanges(HomeScreenEvent.resetBooleans)
        }
        if(state.isError){
            Toast.makeText(context,"Unable to update destination", Toast.LENGTH_SHORT).show()
            eventsChanges(HomeScreenEvent.resetBooleans)
        }
    }


}