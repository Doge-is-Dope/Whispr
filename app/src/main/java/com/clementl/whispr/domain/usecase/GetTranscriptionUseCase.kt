package com.clementl.whispr.domain.usecase

import com.clementl.whispr.domain.model.Transcription
import com.clementl.whispr.domain.repository.TranscriptionRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.io.File

@Singleton
class GetTranscriptionUseCase @Inject constructor(private val repository: TranscriptionRepository) {
    suspend operator fun invoke(audioFile: File): Result<Transcription> {
        return repository.getTranscription(audioFile)
    }
}
