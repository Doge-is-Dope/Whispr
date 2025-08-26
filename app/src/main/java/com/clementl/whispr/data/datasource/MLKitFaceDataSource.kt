package com.clementl.whispr.data.datasource


import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import com.clementl.whispr.di.AnalysisExecutor
import com.clementl.whispr.domain.model.FaceDetectionState
import com.clementl.whispr.domain.model.FaceDetectionState.NoFace
import com.google.mlkit.vision.face.FaceDetector
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.Executor

@Singleton
class MLKitFaceDataSource @Inject constructor(
    private val detector: FaceDetector,
    @param:AnalysisExecutor private val executor: Executor
) : FaceDataSource {
    private val _faceDetectionState = MutableStateFlow<FaceDetectionState>(NoFace)

    private val mlKitAnalyzer = MlKitAnalyzer(
        listOf(detector),
        ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
        executor
    ) { result: MlKitAnalyzer.Result ->

        val faces = result.getValue(detector)

        val newState = if (faces.isNullOrEmpty()) NoFace
        else FaceDetectionState.FaceDetected(faces.size)
        _faceDetectionState.value = newState
    }

    override fun getFaceDetectionFlow(): Flow<FaceDetectionState> =
        _faceDetectionState.asStateFlow()

    override fun getImageAnalyzer(): ImageAnalysis.Analyzer = mlKitAnalyzer
}