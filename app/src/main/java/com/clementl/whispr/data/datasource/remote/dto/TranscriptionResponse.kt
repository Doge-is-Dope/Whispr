package com.clementl.whispr.data.datasource.remote.dto

import com.clementl.whispr.domain.model.Transcription
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TranscriptionResponse(
    @SerialName("text")
    val text: String
)

fun TranscriptionResponse.toDomain(): Transcription = Transcription(
    text = this.text
)