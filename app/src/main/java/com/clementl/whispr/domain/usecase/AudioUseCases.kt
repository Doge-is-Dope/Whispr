package com.clementl.whispr.domain.usecase

import com.clementl.whispr.domain.model.RecordingState
import com.clementl.whispr.domain.repository.AudioRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class StartListeningUseCase @Inject constructor(private val repository: AudioRepository) {
    operator fun invoke() = repository.startListening()
}

@Singleton
class StopListeningUseCase @Inject constructor(private val repository: AudioRepository) {
    operator fun invoke() = repository.stopListening()
}

@Singleton
class ObserveRecordingStateUseCase @Inject constructor(private val repository: AudioRepository) {
    operator fun invoke(): Flow<RecordingState> = repository.getRecordingStateFlow()
}

@Singleton
class ReleaseAudioResourcesUseCase @Inject constructor(private val repository: AudioRepository) {
    operator fun invoke() = repository.release()
}
