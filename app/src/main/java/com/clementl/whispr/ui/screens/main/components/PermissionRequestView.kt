package com.clementl.whispr.ui.screens.main.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.clementl.whispr.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissions(
    permissions: List<String>,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val permissionsState = rememberMultiplePermissionsState(permissions)

    var hasRequestedPermissions by rememberSaveable {
        mutableStateOf(false)
    }

    // If the user has denied permissions before,
    // we consider that we've requested them
    LaunchedEffect(permissionsState.shouldShowRationale) {
        if (permissionsState.shouldShowRationale) {
            hasRequestedPermissions = true
        }
    }

    if (permissionsState.allPermissionsGranted) {
        // All permissions are granted, show the content
        content()
    } else {
        val isPermanentlyDenied = !permissionsState.shouldShowRationale && hasRequestedPermissions

        PermissionRequestView(
            onRequestPermission = { permissionsState.launchMultiplePermissionRequest() },
            isPermanentlyDenied = isPermanentlyDenied,
            onOpenSettings = { openAppSettings(context) }
        )
    }
}

// Open the app settings screen
private fun openAppSettings(context: Context) {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).also {
        context.startActivity(it)
    }
}

@Composable
fun PermissionRequestView(
    onRequestPermission: () -> Unit,
    isPermanentlyDenied: Boolean = false,
    onOpenSettings: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_record),
            contentDescription = "Microphone permission required",
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isPermanentlyDenied) {
                stringResource(R.string.permission_settings_title)
            } else {
                stringResource(R.string.permission_request_title)
            },
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isPermanentlyDenied) {
                stringResource(R.string.permission_settings_desc)
            } else {
                stringResource(R.string.permission_request_desc)
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isPermanentlyDenied) onOpenSettings?.invoke() else onRequestPermission()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isPermanentlyDenied) {
                    stringResource(R.string.permission_settings_action)
                } else {
                    stringResource(R.string.permission_request_action)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionRequestPreview() {
    PermissionRequestView(onRequestPermission = {})
}

@Preview(showBackground = true)
@Composable
fun PermissionRequest_PermanentlyDenied_Preview() {
    PermissionRequestView(onRequestPermission = {}, isPermanentlyDenied = true)
}
