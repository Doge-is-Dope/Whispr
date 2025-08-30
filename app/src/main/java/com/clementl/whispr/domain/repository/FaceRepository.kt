package com.clementl.whispr.domain.repository

import androidx.camera.core.ImageAnalysis
import com.clementl.whispr.domain.model.FaceDetectionState
import kotlinx.coroutines.flow.Flow

interface FaceRepository {
    /**
     * Provides a real-time stream of the current face detection state.
     */
    fun getFaceDetectionStateFlow(): Flow<FaceDetectionState>

    /**
     * Provides an ImageAnalysis.Analyzer instance for CameraX to use.
     */
    fun getImageAnalyzer(): ImageAnalysis.Analyzer
}