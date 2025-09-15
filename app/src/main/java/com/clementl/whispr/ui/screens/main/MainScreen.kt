package com.clementl.whispr.ui.screens.main

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.clementl.whispr.ui.screens.main.components.PermissionRequestView
import com.clementl.whispr.ui.screens.main.components.RecordingButton
import com.clementl.whispr.utils.mainPermissions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
fun MainScreen(
    viewModel: MainViewModel,
    contentPadding: PaddingValues = PaddingValues()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Permission state for microphone
    val permissionsState = rememberMultiplePermissionsState(mainPermissions)
    var hasRequestedPermissions by rememberSaveable { mutableStateOf(false) }
    var pendingStartAfterPermission by rememberSaveable { mutableStateOf(false) }
    var showPermissionSheet by rememberSaveable { mutableStateOf(false) }

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
        val showRecordingButton = uiState !is UiState.Error

        if (showRecordingButton) {
            RecordingButton(
                uiState is UiState.Listening,
                onClick = {
                    val granted = permissionsState.allPermissionsGranted
                    if (uiState is UiState.Listening) {
                        viewModel.stopListening()
                    } else if (granted) {
                        viewModel.startListening()
                    } else {
                        val permanentlyDenied =
                            !permissionsState.shouldShowRationale && hasRequestedPermissions
                        if (permanentlyDenied) {
                            showPermissionSheet = true
                        } else {
                            hasRequestedPermissions = true
                            pendingStartAfterPermission = true
                            permissionsState.launchMultiplePermissionRequest()
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            )
        }

        if (showPermissionSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showPermissionSheet = false },
                sheetState = sheetState
            ) {
                PermissionRequestView(
                    onRequestPermission = {
                        showPermissionSheet = false
                        permissionsState.launchMultiplePermissionRequest()
                    },
                    isPermanentlyDenied = true,
                    onOpenSettings = {
                        showPermissionSheet = false
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        ).also { context.startActivity(it) }
                    }
                )
            }
        }

        // Auto-start listening right after permission is granted via system dialog
        LaunchedEffect(permissionsState.allPermissionsGranted, pendingStartAfterPermission) {
            if (pendingStartAfterPermission && permissionsState.allPermissionsGranted && uiState !is UiState.Listening) {
                pendingStartAfterPermission = false
                viewModel.startListening()
            }
        }
    }
}
