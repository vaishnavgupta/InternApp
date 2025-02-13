package com.example.internshipapp.data.di

import com.example.internshipapp.data.repository.AuthRepositoryImpl
import com.example.internshipapp.data.repository.HomeScreenRepoImpl
import com.example.internshipapp.data.repository.SettingScreenRepoImpl
import com.example.internshipapp.domain.repository.AuthRepository
import com.example.internshipapp.domain.repository.HomeScreenRepository
import com.example.internshipapp.domain.repository.SettingsScreenRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindSubjectRepoWithImpl(
        impl:AuthRepositoryImpl
    ):AuthRepository

    @Singleton
    @Binds
    abstract fun bindHomeScreenRepoWithImpl(
        impl:HomeScreenRepoImpl
    ):HomeScreenRepository

    @Singleton
    @Binds
    abstract fun bindSettingsScreenRepoWithImpl(
        impl:SettingScreenRepoImpl
    ):SettingsScreenRepo
}