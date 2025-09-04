package com.clementl.whispr.domain.usecase

import com.clementl.whispr.domain.repository.AudioRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ReleaseAudioResourcesUseCase @Inject constructor(private val repository: AudioRepository) {
    operator fun invoke() = repository.release()
}

