package com.fiz.mono.database.di

import com.fiz.mono.database.repositories.SettingsLocalDataSourceImpl
import com.fiz.mono.domain.repositories.SettingsLocalDataSource
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
    abstract fun provideSettingsLocalDataSource(settingsLocalDataSourceImpl: SettingsLocalDataSourceImpl): SettingsLocalDataSource


}
