package com.clementl.whispr.ui.screens.main.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.clementl.whispr.R


@Composable
fun RecordingButton(
    isRunning: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconRes = if (isRunning) R.drawable.ic_stop else R.drawable.ic_record

    val containerColor = if (isRunning) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    val contentColor = if (isRunning) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        shape = FloatingActionButtonDefaults.largeShape,
        content = {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = if (isRunning) "Stop recording" else "Start recording"
            )
        }
    )
}

@Preview
@Composable
private fun RecordingButtonPreview() {
    RecordingButton(
        isRunning = true,
        onClick = { }
    )
}