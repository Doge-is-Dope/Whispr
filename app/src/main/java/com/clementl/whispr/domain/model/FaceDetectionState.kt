package com.clementl.whispr.domain.model

sealed class FaceDetectionState {
    /**
     * No face detected
     */
    data object NoFace : FaceDetectionState()

    /**
     * Face(s) detected, with the count of faces
     * @param count Number of faces detected
     */
    data class FacesDetected(val count: Int) : FaceDetectionState() {
        init {
            require(count > 0) {
                "FacesDetected's count cannot be zero or negative. Use NoFace state instead."
            }
        }
    }

    /**
     * An error occurred during face detection
     * @param exception The exception that occurred
     */
    data class Error(val exception: Throwable) : FaceDetectionState()
}
