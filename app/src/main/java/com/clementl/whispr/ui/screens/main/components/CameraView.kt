package com.clementl.whispr.ui.screens.main.components

import androidx.camera.core.ImageAnalysis
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.clementl.whispr.R
import com.clementl.whispr.ui.screens.main.UiState
import com.clementl.whispr.ui.theme.WhisprTheme
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Composable
fun CameraView(analyzer: ImageAnalysis.Analyzer, uiState: UiState, executor: Executor) {
    val topScrim = remember {
        Brush.verticalGradient(
            0f to Color.Black.copy(alpha = 0.60f),
            1f to Color.Transparent
        )
    }

    val haptic = LocalHapticFeedback.current
    var hadFace by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        val hasFace = uiState is UiState.FacesDetected
        if (!hadFace && hasFace) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        hadFace = hasFace
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera feed
        CameraPreview(
            analyzer = analyzer,
            executor = executor,
            modifier = Modifier.fillMaxSize()
        )

        // Center guide ring
        GuideRing(
            modifier = Modifier.align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.TopCenter)
                .background(topScrim)
        )

        // Status chip (top-left, under status bar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Crossfade(targetState = uiState, label = "statusCrossfade") { state ->
                StatusChip(text = statusText(state))
            }
        }

        // Bottom instruction
        if (uiState is UiState.Standby) {
            Surface(
                tonalElevation = 6.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.camera_instruction),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun StatusChip(text: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.28f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun GuideRing(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        val ringSize = remember(maxWidth, maxHeight) {
            val base = 0.56f * minOf(maxWidth, maxHeight).value
            base.coerceIn(180f, 300f).dp
        }
        val strokePx = with(LocalDensity.current) { 3.dp.toPx() }

        Canvas(
            modifier = Modifier.size(ringSize)
        ) {
            drawCircle(
                color = Color.White.copy(alpha = 0.35f),
                style = Stroke(width = strokePx),
                alpha = 0.6f
            )
        }
    }
}

@Composable
private fun statusText(uiState: UiState): String = when (uiState) {
    is UiState.Standby -> stringResource(R.string.status_standby)
    is UiState.FacesDetected -> pluralStringResource(
        R.plurals.status_faces_detected,
        uiState.count,
        uiState.count
    )
    is UiState.Listening -> if (uiState.isSpeaking) stringResource(R.string.status_speaking)
    else stringResource(R.string.status_listening)
    is UiState.Error -> stringResource(R.string.status_error, uiState.message)
}


@Preview(showBackground = true)
@Composable
private fun Preview_Standby() {
    WhisprTheme {
        CameraView(
            analyzer = { },
            uiState = UiState.Standby,
            executor = Executors.newSingleThreadExecutor()
        )
    }
}
