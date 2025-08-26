package com.clementl.whispr.domain.usecase

import androidx.camera.core.ImageAnalysis
import com.clementl.whispr.domain.repository.FaceRepository
import jakarta.inject.Inject

class GetImageAnalyzerUseCase @Inject constructor(
    private val faceRepository: FaceRepository
) {
    operator fun invoke(): ImageAnalysis.Analyzer {
        return faceRepository.getImageAnalyzer()
    }
}