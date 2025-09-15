package com.clementl.whispr.ui.screens.main

import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementl.whispr.domain.model.RecordingState
import com.clementl.whispr.domain.usecase.ObserveRecordingStateUseCase
import com.clementl.whispr.domain.usecase.ReleaseAudioResourcesUseCase
import com.clementl.whispr.domain.usecase.StartListeningUseCase
import com.clementl.whispr.domain.usecase.StopListeningUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainViewModel @Inject constructor(
    observeRecordingStateUseCase: ObserveRecordingStateUseCase,
    private val startListeningUseCase: StartListeningUseCase,
    private val stopListeningUseCase: StopListeningUseCase,
    private val releaseAudioResourcesUseCase: ReleaseAudioResourcesUseCase,
) : ViewModel() {
    private val recordingStateFlow = observeRecordingStateUseCase()
    val uiState: StateFlow<UiState> =
        recordingStateFlow.map { recordingState ->
            when (recordingState) {
                is RecordingState.Idle -> UiState.Standby
                is RecordingState.Recording -> UiState.Listening(isSpeaking = false)
                is RecordingState.Speech -> UiState.Listening(isSpeaking = true)
                is RecordingState.Silence -> UiState.Listening(isSpeaking = false)
                is RecordingState.Error -> UiState.Error("Recording error: ${recordingState.throwable.message}")
            }
        }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Standby
            )

    init {
        // Voice-only: listening is started from UI after permissions are granted
    }

    fun startListening() = startListeningUseCase()

    fun stopListening() = stopListeningUseCase()

    override fun onCleared() {
        releaseAudioResourcesUseCase()
        super.onCleared()
    }
}


sealed class UiState {
    data object Standby : UiState()
    data class FacesDetected(val count: Int) : UiState()
    data class Listening(val isSpeaking: Boolean) : UiState()
    data class Error(val message: String) : UiState()
}
