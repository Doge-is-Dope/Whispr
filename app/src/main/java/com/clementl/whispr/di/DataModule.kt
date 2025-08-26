package com.clementl.whispr.di

import com.clementl.whispr.data.datasource.FaceDataSource
import com.clementl.whispr.data.datasource.MLKitFaceDataSource
import com.clementl.whispr.data.repository.FaceRepositoryImpl
import com.clementl.whispr.domain.repository.FaceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindFaceDataSource(mlKitFaceDataSource: MLKitFaceDataSource): FaceDataSource

    @Binds
    @Singleton
    abstract fun bindFaceRepository(impl: FaceRepositoryImpl): FaceRepository
}




