package com.clementl.whispr.data.repository

import com.clementl.whispr.data.datasource.remote.api.OpenAiService
import com.clementl.whispr.data.datasource.remote.dto.toDomain
import com.clementl.whispr.domain.model.Transcription
import com.clementl.whispr.domain.repository.TranscriptionRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Singleton
class TranscriptionRepositoryImpl @Inject constructor(
    private val openAiService: OpenAiService
) : TranscriptionRepository {
    override suspend fun getTranscription(audioFile: File): Result<Transcription> {
        return try {
            // Prepare the file part using MultipartBody.Part
            val requestFile = audioFile.asRequestBody("audio/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", audioFile.name, requestFile)

            val response = openAiService.getTranscription(file = body)

            Result.success(response.toDomain())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
