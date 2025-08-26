package com.clementl.whispr.data.repository

import androidx.camera.core.ImageAnalysis
import com.clementl.whispr.data.datasource.FaceDataSource
import com.clementl.whispr.domain.model.FaceDetectionState
import com.clementl.whispr.domain.repository.FaceRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class FaceRepositoryImpl @Inject constructor(
    private val faceDataSource: FaceDataSource
) : FaceRepository {

    override fun getFaceDetectionFlow(): Flow<FaceDetectionState> {
        return faceDataSource.getFaceDetectionFlow()
    }

    override fun getImageAnalyzer(): ImageAnalysis.Analyzer {
        return faceDataSource.getImageAnalyzer()
    }
}