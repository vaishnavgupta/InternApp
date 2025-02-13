package com.example.internshipapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.internshipapp.domain.models.Resource
import com.example.internshipapp.domain.repository.SettingsScreenRepo
import com.example.internshipapp.utils.SnackBarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val settingsScreenRepo: SettingsScreenRepo
):ViewModel() {
    private val _state= MutableStateFlow(SettingsScreenState())
    val state  = combine(
        _state,
        settingsScreenRepo.fetchUserDetails()
    ){ _state,userDetail->
        _state.copy(
            userData = userDetail
        )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsScreenState()
    )
    private val _snackbarEvenFlow= MutableSharedFlow<SnackBarEvent>()  //because it does not hold any value
    val snackbarEventFlow=_snackbarEvenFlow.asSharedFlow()

    fun manageEventChanges(event: SettingsScreenEvent){
        when(event){
            is SettingsScreenEvent.onFriendNumChange -> {
                _state.update {
                    it.copy(friendMobile = event.num)
                }
            }
            SettingsScreenEvent.onLogoutClick -> signOutApp()
            is SettingsScreenEvent.onUserNameChange -> {
                _state.update {
                    it.copy(newUserNmae = event.userName)
                }
            }
            SettingsScreenEvent.sendInviteSms -> sendSMS()
            SettingsScreenEvent.updateDetails -> updateUserDetInDb()
        }
    }

    private fun signOutApp() {
        viewModelScope.launch {
            try {
                settingsScreenRepo.logout().collect {
                    when(it){
                        is Resource.Error -> {
                            _snackbarEvenFlow.emit(
                                SnackBarEvent.ShowSnackbar(
                                    msg = "Unable to Logout",
                                )
                            )
                        }
                        is Resource.Loading -> {}
                        is Resource.Success -> {

                            _snackbarEvenFlow.emit(
                                SnackBarEvent.ShowSnackbar(
                                    msg = "Logged out successfully",
                                )
                            )
                            _state.update {
                                it.copy(
                                    isSignOut = true
                                )
                            }

                        }
                    }
                }
            }catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSignOut = false
                    )
                }
                _snackbarEvenFlow.emit(
                    SnackBarEvent.ShowSnackbar(
                        msg = "Failed to logout.",
                    )
                )
            }
        }
    }

    private fun sendSMS() {
        viewModelScope.launch {
            try {
                settingsScreenRepo.sendInviteSMS(
                    number = state.value.friendMobile
                ).collectLatest { res->
                    when(res){
                        is Resource.Error -> {
                            _snackbarEvenFlow.emit(
                                SnackBarEvent.ShowSnackbar(
                                    msg = res.message!!,
                                )
                            )
                        }
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _snackbarEvenFlow.emit(
                                SnackBarEvent.ShowSnackbar(
                                    msg = "SMS sent successfully",
                                )
                            )
                        }
                    }
                }
            }catch (e: Exception) {
                _state.update {
                    it.copy(
                        friendMobile = ""
                    )
                }
                _snackbarEvenFlow.emit(
                    SnackBarEvent.ShowSnackbar(
                        msg = "Failed to send invite.",
                    )
                )
            }
        }
    }

    private fun updateUserDetInDb() {
        viewModelScope.launch {
            try {
                settingsScreenRepo.updateUserDetails(
                    newUser = state.value.userData?.copy(
                        userName = state.value.newUserNmae
                    )!!
                ).collectLatest{ res->
                    when(res){
                        is Resource.Error -> {
                            _snackbarEvenFlow.emit(
                                SnackBarEvent.ShowSnackbar(
                                    msg = res.message!!,
                                )
                            )
                        }
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _snackbarEvenFlow.emit(
                                SnackBarEvent.ShowSnackbar(
                                    msg = "User details updated successfully",
                                )
                            )
                        }
                    }
                }
            }catch (e: Exception) {
                _state.update {
                    it.copy(
                        newUserNmae = ""
                    )
                }
                _snackbarEvenFlow.emit(
                    SnackBarEvent.ShowSnackbar(
                        msg = "Failed to update user details.",
                    )
                )
            }
        }
    }
}

