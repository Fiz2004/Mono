package com.fiz.mono.data.di

import com.fiz.mono.data.data_source.CategoryLocalDataSource
import com.fiz.mono.data.data_source.CategoryLocalDataSourceImpl
import com.fiz.mono.data.data_source.TransactionLocalDataSource
import com.fiz.mono.data.data_source.TransactionLocalDataSourceImpl
import com.fiz.mono.data.repositories.CategoryIconsRepositoryImpl
import com.fiz.mono.data.repositories.SettingsRepositoryImpl
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


    @Binds
    @Singleton
    abstract fun provideCategoryLocalDataSource(CategoryLocalDataSource: CategoryLocalDataSourceImpl): CategoryLocalDataSource

    @Binds
    @Singleton
    abstract fun provideTransactionLocalDataSource(TransactionLocalDataSource: TransactionLocalDataSourceImpl): TransactionLocalDataSource
}
