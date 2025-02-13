package com.example.internshipapp.presentation.login

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
class LoginScreenViewModel@Inject constructor(
    private val authRepository:AuthRepository
) :ViewModel(){
    private val _state= MutableStateFlow(LoginScreenState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LoginScreenState()
    )
    private val _snackbarEvenFlow= MutableSharedFlow<SnackBarEvent>()  //because it does not hold any value
    val snackbarEventFlow=_snackbarEvenFlow.asSharedFlow()

    fun manageEventChanges(event: LoginScreenEvent){
        when(event){
            LoginScreenEvent.loginUser -> loginUserFromDb()
            is LoginScreenEvent.onUserEmsilChanged -> {
                _state.update {
                    it.copy(
                        userEmail = event.email
                    )
                }
            }
            is LoginScreenEvent.onUserPasswordChanged -> {
                _state.update {
                    it.copy(
                        userPassword = event.password
                    )
                }
            }
        }
    }

    private fun loginUserFromDb() {
        viewModelScope.launch {
            try {
                authRepository.loginUser(
                    email = state.value.userEmail,
                    password = state.value.userPassword
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
                                    msg = "Failed to login user."
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
                                    userPassword = "",
                                    userEmail = "",
                                    isSuccess = true
                                )
                            }
                            _snackbarEvenFlow.emit(
                                SnackBarEvent.ShowSnackbar(
                                    msg = "User Logged In Successfully.",
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _snackbarEvenFlow.emit(
                    SnackBarEvent.ShowSnackbar(
                        msg = "Failed to login user. ${e.message}.",
                        snackBarDuration = SnackbarDuration.Long
                    )
                )
            }
        }
    }
}