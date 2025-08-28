package com.clementl.whispr.ui.screens.main.components

import androidx.camera.core.ImageAnalysis
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clementl.whispr.R
import com.clementl.whispr.ui.screens.main.UiState
import java.util.concurrent.Executor

@Composable
fun CameraView(analyzer: ImageAnalysis.Analyzer, uiState: UiState, executor: Executor) {
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(analyzer = analyzer, executor = executor)

        Text(
            text = when (uiState) {
                is UiState.Standby -> stringResource(R.string.status_standby)
                is UiState.FacesDetected -> pluralStringResource(
                    R.plurals.status_faces_detected,
                    uiState.count,
                    uiState.count
                )

                is UiState.Error -> stringResource(R.string.status_error, uiState.message)
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}