package com.clementl.whispr.domain.model

sealed class RecordingState {
    /**
     * Not recording
     */
    data object Idle : RecordingState()

    /**
     * Currently recording
     */
    data object Recording : RecordingState()
}