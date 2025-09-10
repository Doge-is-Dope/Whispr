package com.clementl.whispr.di

import com.clementl.whispr.data.datasource.device.AudioDataSource
import com.clementl.whispr.data.datasource.device.AudioRecorderDataSource
import com.clementl.whispr.data.datasource.device.FaceDataSource
import com.clementl.whispr.data.datasource.device.MLKitFaceDataSource
import com.clementl.whispr.data.repository.AudioRepositoryImpl
import com.clementl.whispr.data.repository.FaceRepositoryImpl
import com.clementl.whispr.data.repository.TranscriptionRepositoryImpl
import com.clementl.whispr.domain.repository.AudioRepository
import com.clementl.whispr.domain.repository.FaceRepository
import com.clementl.whispr.domain.repository.TranscriptionRepository
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
    abstract fun bindFaceDataSource(dataSource: MLKitFaceDataSource): FaceDataSource

    @Binds
    @Singleton
    abstract fun bindFaceRepository(impl: FaceRepositoryImpl): FaceRepository

    @Binds
    @Singleton
    abstract fun bindAudioDataSource(dataSource: AudioRecorderDataSource): AudioDataSource

    @Binds
    @Singleton
    abstract fun bindAudioRepository(impl: AudioRepositoryImpl): AudioRepository

    @Binds
    @Singleton
    abstract fun bindTranscriptionRepository(impl: TranscriptionRepositoryImpl): TranscriptionRepository
}




