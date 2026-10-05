package com.v1kth0rx.totitox.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.v1kth0rx.totitox.data.AppPalette
import com.v1kth0rx.totitox.data.ThemeMode

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TotitoTheme(
    palette: AppPalette = AppPalette.DINAMICO,
    themeMode: ThemeMode = ThemeMode.SISTEMA,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SISTEMA -> isSystemInDarkTheme()
        ThemeMode.CLARO -> false
        ThemeMode.OSCURO -> true
    }
    
    val colorScheme = when (palette) {
        AppPalette.DINAMICO -> {
            val context = LocalContext.current
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (darkTheme) darkColorScheme(primary = EmeraldDarkPrimary) else expressiveLightColorScheme().copy(primary = EmeraldLightPrimary)
            }
        }
        AppPalette.ESMERALDA -> if (darkTheme) darkColorScheme(primary = EmeraldDarkPrimary) else expressiveLightColorScheme().copy(primary = EmeraldLightPrimary)
        AppPalette.OCEANO -> if (darkTheme) darkColorScheme(primary = OceanDarkPrimary) else expressiveLightColorScheme().copy(primary = OceanLightPrimary)
        AppPalette.ATARDECER -> if (darkTheme) darkColorScheme(primary = SunsetDarkPrimary) else expressiveLightColorScheme().copy(primary = SunsetLightPrimary)
        AppPalette.LAVANDA -> if (darkTheme) darkColorScheme(primary = LavenderDarkPrimary) else expressiveLightColorScheme().copy(primary = LavenderLightPrimary)
        AppPalette.ROSA -> if (darkTheme) darkColorScheme(primary = RoseDarkPrimary) else expressiveLightColorScheme().copy(primary = RoseLightPrimary)
        AppPalette.GRAFITO -> if (darkTheme) darkColorScheme(primary = GraphiteDarkPrimary) else expressiveLightColorScheme().copy(primary = GraphiteLightPrimary)
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}