package com.clementl.whispr.data.datasource.remote.api

import com.clementl.whispr.data.datasource.remote.dto.TranscriptionResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OpenAiService {
    @Multipart
    @POST("v1/audio/transcriptions")
    suspend fun getTranscription(
        @Part file: MultipartBody.Part,
        @Part("model") model: String = "gpt-4o-transcribe"
    ): TranscriptionResponse
}