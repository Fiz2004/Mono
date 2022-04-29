package com.fiz.mono.database.di

import com.fiz.mono.database.repositories.CategoryIconsRepositoryImpl
import com.fiz.mono.database.repositories.SettingsRepositoryImpl
import com.fiz.mono.domain.repositories.CategoryIconsRepository
import com.fiz.mono.domain.repositories.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun provideCategoryIconsRepository(CategoryIconsRepositoryImpl: CategoryIconsRepositoryImpl): CategoryIconsRepository

}
