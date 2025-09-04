package com.clementl.whispr.domain.usecase

import androidx.camera.core.ImageAnalysis
import com.clementl.whispr.domain.model.FaceDetectionState
import com.clementl.whispr.domain.repository.FaceRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class GetImageAnalyzerUseCase @Inject constructor(private val repository: FaceRepository) {
    operator fun invoke(): ImageAnalysis.Analyzer = repository.getImageAnalyzer()
}

@Singleton
class ObserveFaceStateUseCase @Inject constructor(private val repository: FaceRepository) {
    operator fun invoke(): Flow<FaceDetectionState> {
        return repository.getFaceDetectionStateFlow()
    }
}

@Singleton
class ReleaseFaceResourcesUseCase @Inject constructor(private val repository: FaceRepository) {
    operator fun invoke() = repository.release()
}
