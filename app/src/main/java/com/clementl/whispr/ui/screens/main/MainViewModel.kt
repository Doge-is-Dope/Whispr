package com.clementl.whispr.ui.screens.main

import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementl.whispr.di.AnalysisExecutor
import com.clementl.whispr.domain.model.FaceDetectionState
import com.clementl.whispr.domain.usecase.GetImageAnalyzerUseCase
import com.clementl.whispr.domain.usecase.ObserveFaceStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.concurrent.Executor

@HiltViewModel
class MainViewModel @Inject constructor(
    observeFaceStateUseCase: ObserveFaceStateUseCase,
    private val getImageAnalyzerUseCase: GetImageAnalyzerUseCase,
    @param:AnalysisExecutor private val analysisExecutor: Executor
) : ViewModel() {

    val uiState: StateFlow<UiState> = observeFaceStateUseCase()
        .map { faceState ->
            when (faceState) {
                is FaceDetectionState.NoFace -> UiState.Standby
                is FaceDetectionState.FacesDetected -> UiState.FacesDetected(faceState.count)
                is FaceDetectionState.Error -> UiState.Error(faceState.message)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Standby
        )

    fun getImageAnalyzer(): ImageAnalysis.Analyzer = getImageAnalyzerUseCase()

    fun getAnalysisExecutor(): Executor = analysisExecutor
}


sealed class UiState {
    data object Standby : UiState()
    data class FacesDetected(val count: Int) : UiState()
    data class Error(val message: String) : UiState()
}