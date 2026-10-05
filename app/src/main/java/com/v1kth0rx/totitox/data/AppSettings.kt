package com.v1kth0rx.totitox.data

import com.v1kth0rx.totitox.domain.Difficulty

enum class AppPalette {
    DINAMICO,
    ESMERALDA,
    OCEANO,
    ATARDECER,
    LAVANDA,
    ROSA,
    GRAFITO
}

enum class ThemeMode {
    SISTEMA, CLARO, OSCURO
}

enum class IconStyle {
    CLASSIC, NUMBERS, SHAPES, SIGNS
}

data class AppSettings(
    val palette: AppPalette = AppPalette.DINAMICO,
    val themeMode: ThemeMode = ThemeMode.SISTEMA,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val iconStyle: IconStyle = IconStyle.CLASSIC
)
