package com.clementl.whispr.ui.components.voiceorb

import androidx.compose.ui.graphics.Color

data class OrbTheme(
    val colorA: Color,       // inner glow
    val colorB: Color,       // outer/base
    val speedMin: Float,     // cloud flow speed range (min..max)
    val speedMax: Float,
    val gainMin: Float,      // contrast range (min..max)
    val gainMax: Float,
    val haloStrength: Float  // outer halo brightness
)

enum class OrbMode { Listening, Thinking, Speaking }

fun OrbMode.toTheme(): OrbTheme = when (this) {
    OrbMode.Listening -> OrbTheme(
        colorA = Color(0xFFE8FBFF),
        colorB = Color(0xFF2AA6FF),
        speedMin = 0.40f, speedMax = 1.40f,
        gainMin = 1.00f, gainMax = 1.60f,
        haloStrength = 0.03f
    )

    OrbMode.Thinking -> OrbTheme(
        colorA = Color(0xFFE5DEFF),
        colorB = Color(0xFF7C3AED), // violet
        speedMin = 0.35f, speedMax = 1.20f,
        gainMin = 0.95f, gainMax = 1.45f,
        haloStrength = 0.05f
    )

    OrbMode.Speaking -> OrbTheme(
        colorA = Color(0xFFFFE5E5),
        colorB = Color(0xFFEF4444), // red
        speedMin = 0.50f, speedMax = 1.80f,
        gainMin = 1.10f, gainMax = 1.80f,
        haloStrength = 0.08f
    )
}

