package com.chargescopixel.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColors = darkColorScheme(
    primary = SkyBlue,
    secondary = MintGlow,
    tertiary = WarmAlert,
    background = DeepNavy,
    surface = SurfaceDark
)

private val LightColors = lightColorScheme(
    primary = ColorTokens.LightPrimary,
    secondary = ColorTokens.LightSecondary,
    tertiary = ColorTokens.LightTertiary
)

private object ColorTokens {
    val LightPrimary = androidx.compose.ui.graphics.Color(0xFF0A5FA0)
    val LightSecondary = androidx.compose.ui.graphics.Color(0xFF006875)
    val LightTertiary = androidx.compose.ui.graphics.Color(0xFF9C4300)
}

@Composable
fun ChargeScopeTheme(
    dynamicColor: Boolean = true,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
