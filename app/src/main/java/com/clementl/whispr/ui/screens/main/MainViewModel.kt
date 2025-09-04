package com.clementl.whispr.ui.screens.main

import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementl.whispr.di.AnalysisExecutor
import com.clementl.whispr.domain.model.FaceDetectionState
import com.clementl.whispr.domain.model.RecordingState
import com.clementl.whispr.domain.usecase.GetImageAnalyzerUseCase
import com.clementl.whispr.domain.usecase.ObserveFaceStateUseCase
import com.clementl.whispr.domain.usecase.ObserveRecordingStateUseCase
import com.clementl.whispr.domain.usecase.StartListeningUseCase
import com.clementl.whispr.domain.usecase.StopListeningUseCase
import com.clementl.whispr.domain.usecase.ReleaseAudioResourcesUseCase
import com.clementl.whispr.ui.screens.main.UiState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

@HiltViewModel
class MainViewModel @Inject constructor(
    observeFaceStateUseCase: ObserveFaceStateUseCase,
    observeRecordingStateUseCase: ObserveRecordingStateUseCase,
    private val getImageAnalyzerUseCase: GetImageAnalyzerUseCase,
    @param:AnalysisExecutor private val analysisExecutor: Executor,
    private val startListeningUseCase: StartListeningUseCase,
    private val stopListeningUseCase: StopListeningUseCase,
    private val releaseAudioResourcesUseCase: ReleaseAudioResourcesUseCase
) : ViewModel() {

    private val faceStateFlow = observeFaceStateUseCase()
    private val recordingStateFlow = observeRecordingStateUseCase()
    val uiState: StateFlow<UiState> =
        combine(faceStateFlow, recordingStateFlow) { faceState, recordingState ->
            when (faceState) {
                is FaceDetectionState.NoFace -> Standby
                is FaceDetectionState.Error -> Error("Face detection error: ${faceState.throwable.message}")
                is FaceDetectionState.FacesDetected -> {
                    when (recordingState) {
                        is RecordingState.Idle -> FacesDetected(faceState.count)
                        is RecordingState.Recording -> Listening(isSpeaking = false)
                        is RecordingState.Speech -> Listening(isSpeaking = true)
                        is RecordingState.Silence -> Listening(isSpeaking = false)
                        is RecordingState.Error -> Error("Recording error: ${recordingState.throwable.message}")
                    }
                }
            }
        }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Standby
            )

    init {
        viewModelScope.launch {
            faceStateFlow
                .map { it is FaceDetectionState.FacesDetected }
                .distinctUntilChanged()
                .collect { detected ->
                    if (detected) startListeningUseCase() else stopListeningUseCase()
                }
        }
    }

    fun getImageAnalyzer(): ImageAnalysis.Analyzer = getImageAnalyzerUseCase()

    fun getAnalysisExecutor(): Executor = analysisExecutor

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
