package com.clementl.whispr.data.datasource

import androidx.camera.core.ImageAnalysis
import com.clementl.whispr.domain.model.FaceDetectionState
import kotlinx.coroutines.flow.Flow

interface FaceDataSource {
    /**
     * Emits the current face detection state.
     */
    fun getFaceDetectionFlow(): Flow<FaceDetectionState>

    /**
     * Provide an ImageAnalysis.Analyzer instance for cameraX to use.
     */
    fun getImageAnalyzer(): ImageAnalysis.Analyzer
}