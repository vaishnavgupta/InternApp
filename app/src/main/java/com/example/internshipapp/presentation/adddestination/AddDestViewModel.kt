package com.example.internshipapp.presentation.adddestination

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.internshipapp.domain.models.AddedData
import com.example.internshipapp.domain.models.Resource
import com.example.internshipapp.domain.repository.AuthRepository
import com.example.internshipapp.utils.SnackBarEvent
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDestViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val auth:FirebaseAuth
):ViewModel(){
    private val _state= MutableStateFlow(AddDestScreenState())

    val state=_state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AddDestScreenState()
    )

    private val _snackbarEvenFlow= MutableSharedFlow<SnackBarEvent>()  //because it does not hold any value
    val snackbarEventFlow=_snackbarEvenFlow.asSharedFlow()

    fun manageEventChanges(event:AddDestScreenEvent){
        when(event){
            AddDestScreenEvent.deleteDestination -> {}
            is AddDestScreenEvent.onDateChange -> {
                _state.update {
                    it.copy(
                        date = event.millis
                    )
                }
            }
            is AddDestScreenEvent.onDescChange -> {
                _state.update {
                    it.copy(
                        desc = event.desc
                    )
                }
            }
            is AddDestScreenEvent.onMapsLinkChange -> {
                _state.update {
                    it.copy(
                        mapsLink = event.link
                    )
                }
            }
            is AddDestScreenEvent.onRelatedToDestChange -> {
                _state.update {
                    it.copy(
                        relatedToDest = event.destination.name
                    )
                }
            }
            is AddDestScreenEvent.onTitleChange -> {
                _state.update {
                    it.copy(
                        title = event.title
                    )
                }
            }
            AddDestScreenEvent.saveDestination -> saveDestinationInDb()
        }
    }

    private fun saveDestinationInDb() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            if (auth.currentUser == null || state.value.relatedToDest == null) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }

            authRepository.addDestination(
                userId = auth.currentUser!!.uid,
                addedData = AddedData(
                    destination = state.value.relatedToDest!!,
                    title = state.value.title,
                    desc = state.value.desc,
                    mapsLink = state.value.mapsLink,
                    date = state.value.date.changeMillisToString()
                )
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false, isSuccessfully = true) }
                        _snackbarEvenFlow.emit(
                            SnackBarEvent.ShowSnackbar(
                                msg = "Destination added successfully."
                            )
                        )
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false) }
                        _snackbarEvenFlow.emit(
                            SnackBarEvent.ShowSnackbar(
                                msg = "Failed to add destination. ${result.message}",
                                snackBarDuration = SnackbarDuration.Long
                            )
                        )
                    }
                }
            }
        }
    }
}