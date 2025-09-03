package com.clementl.whispr.data.repository

import com.clementl.whispr.data.datasource.AudioDataSource
import com.clementl.whispr.domain.model.RecordingState
import com.clementl.whispr.domain.repository.AudioRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class AudioRepositoryImpl @Inject constructor(
    private val audioDataSource: AudioDataSource
) : AudioRepository {
    override fun startListening() = audioDataSource.startListening()

    override fun stopListening() = audioDataSource.stopListening()

    override fun getRecordingStateFlow(): Flow<RecordingState> = audioDataSource.getRecordingStateFlow()
}