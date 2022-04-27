package com.fiz.mono.on_boarding.di

import com.fiz.mono.on_boarding.data.FirstTimeLocalDataSourceImpl
import com.fiz.mono.on_boarding.domain.FirstTimeLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OnBoardingModule {

    @Binds
    @Singleton
    abstract fun provideFirstTimeLocalDataSource(firstTimeLocalDataSourceImpl: FirstTimeLocalDataSourceImpl): FirstTimeLocalDataSource

}