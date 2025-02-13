package com.example.internshipapp.presentation.home

import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.internshipapp.domain.models.Resource
import com.example.internshipapp.domain.repository.HomeScreenRepository
import com.example.internshipapp.utils.SnackBarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val homeScreenRepository: HomeScreenRepository
):ViewModel() {
    private val _state= MutableStateFlow(HomeScreenState())
    val state = combine(
        _state,
        homeScreenRepository.getCafesList(),
        homeScreenRepository.getEventsList(),
        homeScreenRepository.getPlacesList()
    ){ _state,cafelist,eventsList,placesList->
        _state.copy(
            cafesList = cafelist,
            eventsList = eventsList,
            placesList = placesList
        )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeScreenState()
    )

    fun manageEventChanges(event: HomeScreenEvent){
        when(event){
            is HomeScreenEvent.onMapsLinkChange -> {
                _state.update {
                    it.copy(
                        destination = it.destination?.copy(
                            mapsLink = event.link
                        )
                    )
                }
            }
            HomeScreenEvent.onSaveDestClick -> updateDestInDb()
            is HomeScreenEvent.onDestCardClick -> {
                _state.update {
                    it.copy(
                        destination = event.data
                    )
                }
            }

            HomeScreenEvent.resetBooleans -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        isError = false
                    )
                }
            }
        }
    }

    private fun updateDestInDb() {
        viewModelScope.launch {
            try {
                homeScreenRepository.updateDestination(
                    newData = state.value.destination!!
                ).collectLatest { res->
                    when(res){
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    isError = true
                                )
                            }

                        }
                        is Resource.Loading -> {
                            _state.update {
                                it.copy(
                                    isLoading = true
                                )
                            }
                        }
                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    isSuccess = true
                                )
                            }
                        }
                    }
                }
            }catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isError = true
                    )
                }
            }
        }
    }


}