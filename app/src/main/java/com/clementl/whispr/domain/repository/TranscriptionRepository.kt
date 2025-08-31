package com.clementl.whispr.domain.repository

import com.clementl.whispr.domain.model.Transcription
import java.io.File

interface TranscriptionRepository {
    /**
     * Get the transcription of an audio file.
     * @param audioFile The audio file to transcribe.
     * @return The transcription result.
     */
    suspend fun getTranscription(audioFile: File): Result<Transcription>
}
