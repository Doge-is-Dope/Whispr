package com.clementl.whispr.domain.usecase

import com.clementl.whispr.domain.model.FaceDetectionState
import com.clementl.whispr.domain.repository.FaceRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveFaceStateUseCase @Inject constructor(private val faceRepository: FaceRepository) {
    operator fun invoke(): Flow<FaceDetectionState> {
        return faceRepository.getFaceDetectionFlow()
    }
}