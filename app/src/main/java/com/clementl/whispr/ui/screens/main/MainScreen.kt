package com.clementl.whispr.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.clementl.whispr.ui.screens.main.components.RecordingButton
import com.clementl.whispr.ui.screens.main.components.RequestPermissions
import com.clementl.whispr.utils.mainPermissions

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    contentPadding: PaddingValues = PaddingValues()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiState.Error -> snackbarHostState.showSnackbar(state.message)
            else -> Unit
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        RequestPermissions(permissions = mainPermissions) {
            LaunchedEffect(Unit) { viewModel.startListening() }
        }

        if (uiState !is UiState.Standby && uiState !is UiState.Error) {
            RecordingButton(
                uiState is UiState.Listening,
                onClick = {
                    if (uiState is UiState.Listening) viewModel.stopListening()
                    else if (uiState is UiState.FacesDetected) viewModel.startListening()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            )
        }
    }
}
