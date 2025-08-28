package com.clementl.whispr.di

import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Module
@InstallIn(SingletonComponent::class)
object ProviderModule {
    @Provides
    @Singleton
    fun provideFaceDetectorOptions(): FaceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .build()

    @Provides
    @Singleton
    fun provideFaceDetector(options: FaceDetectorOptions): FaceDetector = FaceDetection.getClient(options)

    @Provides
    @Singleton
    @AnalysisExecutor
    fun provideAnalysisExecutor(): Executor = Executors.newSingleThreadExecutor()
}