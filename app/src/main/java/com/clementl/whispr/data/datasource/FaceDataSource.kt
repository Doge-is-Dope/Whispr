package com.clementl.whispr.data.datasource

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.clementl.whispr.domain.model.FaceDetectionState
import com.clementl.whispr.domain.model.FaceDetectionState.NoFace
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

interface FaceDataSource {
    /**
     * Emits the current face detection state.
     */
    fun getFaceDetectionStateFlow(): Flow<FaceDetectionState>

    /**
     * Provide an ImageAnalysis.Analyzer instance for cameraX to use.
     */
    fun getImageAnalyzer(): ImageAnalysis.Analyzer

    /**
     * Releases underlying ML Kit resources.
     */
    fun release()
}

@Singleton
class MLKitFaceDataSource @Inject constructor(
    private val detector: FaceDetector
) : FaceDataSource {
    private val _faceDetectionState = MutableStateFlow<FaceDetectionState>(NoFace)

    private val mlKitAnalyzer = ManualFaceAnalyzer { faces ->
        _faceDetectionState.value = if (faces.isEmpty()) NoFace
        else FaceDetectionState.FacesDetected(faces.size)
    }

    override fun getFaceDetectionStateFlow(): Flow<FaceDetectionState> =
        _faceDetectionState.asStateFlow()

    override fun getImageAnalyzer(): ImageAnalysis.Analyzer = mlKitAnalyzer

    override fun release() {
        try {
            detector.close()
        } catch (_: Exception) {
            // Ignore; best-effort cleanup
        }
    }

    private inner class ManualFaceAnalyzer(private val onResults: (List<Face>) -> Unit) :
        ImageAnalysis.Analyzer {

        @OptIn(ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {
            imageProxy.image?.let { mediaImage ->
                val image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                detector.process(image)
                    .addOnSuccessListener(onResults)
                    .addOnFailureListener { e ->
                        Timber.e(e, "Face detection failed")
                        _faceDetectionState.value = FaceDetectionState.Error(e)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } ?: imageProxy.close()
        }
    }
}
