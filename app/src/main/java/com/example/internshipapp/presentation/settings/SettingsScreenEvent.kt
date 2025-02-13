package com.example.internshipapp.presentation.settings

sealed class SettingsScreenEvent {
    data class onUserNameChange(val userName:String):SettingsScreenEvent()
    data class onFriendNumChange(val num:String):SettingsScreenEvent()
    data object updateDetails:SettingsScreenEvent()
    data object sendInviteSms:SettingsScreenEvent()
    data object onLogoutClick:SettingsScreenEvent()
}