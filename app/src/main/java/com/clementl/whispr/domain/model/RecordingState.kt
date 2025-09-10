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

    /**
     * Speech detected
     */
    data object Speech : RecordingState()

    /**
     * Silence detected
     */
    data object Silence : RecordingState()

    /**
     * An error occurred during initialization or recording
     */
    data class Error(val throwable: Throwable) : RecordingState()
}