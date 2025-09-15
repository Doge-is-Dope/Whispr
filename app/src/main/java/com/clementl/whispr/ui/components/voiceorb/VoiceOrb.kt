package com.clementl.whispr.ui.components.voiceorb

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.isActive

/**
 * Animated voice orb driven by `energy` (0f..1f). Android 13+ (RuntimeShader).
 * @param modifier Layout modifier for positioning/styling.
 * @param energy Normalized intensity controlling motion/contrast.
 * @param size Orb diameter.
 * @param mode Visual theme (Listening/Thinking/Speaking).
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun VoiceOrb(
    modifier: Modifier = Modifier,
    energy: Float,
    size: Dp = 220.dp,
    mode: OrbMode = OrbMode.Listening
) {
    val theme = mode.toTheme()
    val runtimeShader = remember { RuntimeShader(shaderSrc) }
    val smoothEnergy = remember { Animatable(0f) }
    LaunchedEffect(energy) {
        smoothEnergy.animateTo(energy.coerceIn(0f, 1f), animationSpec = tween(160))
    }
    
    var timeSec by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        var last = 0L
        while (isActive) {
            withFrameNanos { now ->
                if (last != 0L) {
                    val dt = (now - last) / 1_000_000_000f
                    timeSec += dt
                }
                last = now
            }
        }
    }

    Canvas(modifier.size(size)) {
        // uniforms
        runtimeShader.setFloatUniform("iResolution", this.size.width, this.size.height)
        runtimeShader.setFloatUniform("iTime", timeSec % 1000f)
        runtimeShader.setFloatUniform("uEnergy", smoothEnergy.value)

        // Use float uniforms for RGBA to avoid setColorUniform/type mismatch issues on RuntimeShader
        runtimeShader.setFloatUniform(
            "uColorA",
            theme.colorA.red,
            theme.colorA.green,
            theme.colorA.blue,
            theme.colorA.alpha
        )
        runtimeShader.setFloatUniform(
            "uColorB",
            theme.colorB.red,
            theme.colorB.green,
            theme.colorB.blue,
            theme.colorB.alpha
        )

        runtimeShader.setFloatUniform("uSpeedMin", theme.speedMin)
        runtimeShader.setFloatUniform("uSpeedMax", theme.speedMax)
        runtimeShader.setFloatUniform("uGainMin", theme.gainMin)
        runtimeShader.setFloatUniform("uGainMax", theme.gainMax)
        runtimeShader.setFloatUniform("uHaloStrength", theme.haloStrength)

        drawRect(brush = ShaderBrush(runtimeShader))
    }
}

@Preview(
    name = "VoiceOrb • Listening",
    showBackground = true,
    backgroundColor = 0xFF0B1220
)
@Composable
private fun Preview_VoiceOrb_Listening() {
    VoiceOrb(
        energy = 0.35f,
        size = 220.dp,
        mode = OrbMode.Listening
    )
}

@Preview(
    name = "VoiceOrb • Thinking",
    showBackground = true,
    backgroundColor = 0xFF0B1220
)
@Composable
private fun Preview_VoiceOrb_Thinking() {
    VoiceOrb(
        energy = 0.25f,
        size = 220.dp,
        mode = OrbMode.Thinking
    )
}

@Preview(
    name = "VoiceOrb • Speaking (animated energy)",
    showBackground = true,
    backgroundColor = 0xFF0B1220
)
@Composable
private fun Preview_VoiceOrb_Speaking() {
    VoiceOrb(
        energy = 1f,
        size = 220.dp,
        mode = OrbMode.Speaking
    )
}
