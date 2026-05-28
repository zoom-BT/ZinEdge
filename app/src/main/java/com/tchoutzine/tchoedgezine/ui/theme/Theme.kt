package com.tchoutzine.tchoedgezine.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary            = ForestGreen,
    onPrimary          = Color.White,
    primaryContainer   = LightGreen,
    onPrimaryContainer = Color(0xFF0E2C1F),
    secondary          = MidGreen,
    onSecondary        = Color.White,
    background         = BgLight,
    onBackground       = TextLight,
    surface            = SurfaceLight,
    onSurface          = TextLight,
    surfaceVariant     = SurfaceVarLight,
    onSurfaceVariant   = TextSecLight,
    outline            = BorderLight,
    error              = ErrorRed,
    onError            = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary            = LightGreen,
    onPrimary          = BgDark,
    primaryContainer   = Color(0xFF1F3D2E),
    onPrimaryContainer = Color(0xFFB7E4C7),
    secondary          = MidGreen,
    onSecondary        = Color.White,
    background         = BgDark,
    onBackground       = TextDark,
    surface            = SurfaceDark,
    onSurface          = TextDark,
    surfaceVariant     = SurfaceVarDark,
    onSurfaceVariant   = TextSecDark,
    outline            = BorderDark,
    error              = ErrorRedDark,
    onError            = Color.White,
)

@Composable
fun TchoEdgeZineTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = TchoTypography,
        content     = content,
    )
}
