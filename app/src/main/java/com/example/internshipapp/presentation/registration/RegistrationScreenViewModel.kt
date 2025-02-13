package com.example.internshipapp.presentation.registration

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.internshipapp.domain.models.Resource
import com.example.internshipapp.domain.repository.AuthRepository
import com.example.internshipapp.utils.SnackBarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
) :ViewModel() {

    private val _state = MutableStateFlow(RegisterScreenState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RegisterScreenState()
    )


    private val _snackbarEvenFlow= MutableSharedFlow<SnackBarEvent>()  //because it does not hold any value
    val snackbarEventFlow=_snackbarEvenFlow.asSharedFlow()

    //working on events
    fun manageEventChanges(event: RegistrationScreenEvent){
        when(event){
            is RegistrationScreenEvent.onUserEmsilChanged ->{
                _state.update {
                    it.copy(
                        userEmail = event.email
                    )
                }
            }
            is RegistrationScreenEvent.onUserNameChanged -> {
                _state.update {
                    it.copy(
                        userName = event.name
                    )
                }
            }
            is RegistrationScreenEvent.onUserPasswordChanged -> {
                _state.update {
                    it.copy(
                        userPassword = event.password
                    )
                }
            }
            RegistrationScreenEvent.registerUser -> registerUserInDb()
        }
    }

    private fun registerUserInDb() {
        viewModelScope.launch {
            try {
                authRepository.registerUser(
                    email = state.value.userEmail,
                    password = state.value.userPassword,
                    name = state.value.userName
                ).collectLatest { res ->
                    when (res) {
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                            _snackbarEvenFlow.emit(
                                SnackBarEvent.ShowSnackbar(
                                    msg = "Failed to register user."
                                )
                            )
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
                                    userName = "",
                                    userPassword = "",
                                    userEmail = ""
                                )
                            }
                            _snackbarEvenFlow.emit(
                                SnackBarEvent.ShowSnackbar(
                                    msg = "User Registered Successfully. Login to continue",
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _snackbarEvenFlow.emit(
                    SnackBarEvent.ShowSnackbar(
                        msg = "Failed to register user. ${e.message}.",
                        snackBarDuration = SnackbarDuration.Long
                    )
                )
            }
        }
    }
}
