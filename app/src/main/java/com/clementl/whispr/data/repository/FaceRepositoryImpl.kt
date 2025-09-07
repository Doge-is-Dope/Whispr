package com.clementl.whispr.data.repository

import androidx.camera.core.ImageAnalysis
import com.clementl.whispr.data.datasource.FaceDataSource
import com.clementl.whispr.domain.model.FaceDetectionState
import com.clementl.whispr.domain.repository.FaceRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class FaceRepositoryImpl @Inject constructor(
    private val faceDataSource: FaceDataSource
) : FaceRepository {

    override fun getFaceDetectionStateFlow(): Flow<FaceDetectionState> = faceDataSource.getFaceDetectionStateFlow()

    override fun getImageAnalyzer(): ImageAnalysis.Analyzer = faceDataSource.getImageAnalyzer()

    override fun release() = faceDataSource.release()
}
