package com.clementl.whispr.domain.repository

import com.clementl.whispr.domain.model.RecordingState
import kotlinx.coroutines.flow.Flow

interface AudioRepository {
    /**
     * Starts listening to audio input
     */
    fun startListening()

    /**
     * Stops listening to audio input
     */
    fun stopListening()

    /**
     * Provides a real-time stream of the current recording state.
     */
    fun getRecordingStateFlow(): Flow<RecordingState>
}