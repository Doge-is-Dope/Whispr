package com.clementl.whispr.domain.usecase

import com.clementl.whispr.domain.model.RecordingState
import com.clementl.whispr.domain.repository.AudioRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class StartListeningUseCase @Inject constructor(private val audioRepository: AudioRepository) {
    suspend operator fun invoke() = audioRepository.startListening()
}

@Singleton
class StopListeningUseCase @Inject constructor(private val audioRepository: AudioRepository) {
    suspend operator fun invoke() = audioRepository.stopListening()
}

@Singleton
class ObserveRecordingStateUseCase @Inject constructor(private val audioRepository: AudioRepository) {
    operator fun invoke(): Flow<RecordingState> = audioRepository.getRecordingStateFlow()
}