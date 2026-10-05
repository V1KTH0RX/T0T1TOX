package com.v1kth0rx.totitox.ui.settings

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.v1kth0rx.totitox.R
import com.v1kth0rx.totitox.data.AppPalette
import com.v1kth0rx.totitox.data.IconStyle
import com.v1kth0rx.totitox.data.ThemeMode
import com.v1kth0rx.totitox.domain.Difficulty
import com.v1kth0rx.totitox.domain.Player
import com.v1kth0rx.totitox.ui.game.PlayerMark
import com.v1kth0rx.totitox.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val settings by viewModel.appSettings.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Color
        SettingsSection(title = stringResource(R.string.settings_color)) {
            val palettes = AppPalette.values()
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val dynamicEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    palettes.forEach { palette ->
                        if (palette == AppPalette.DINAMICO) return@forEach // Handle separately
                        val color = getPaletteColor(palette)
                        val name = getPaletteName(palette)
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (settings.palette == palette) 3.dp else 0.dp,
                                    color = if (settings.palette == palette) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { viewModel.onPaletteSelected(palette) }
                                .semantics { contentDescription = "Seleccionar paleta $name" }
                        )
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = settings.palette == AppPalette.DINAMICO,
                        onClick = if (dynamicEnabled) { { viewModel.onPaletteSelected(AppPalette.DINAMICO) } } else null,
                        enabled = dynamicEnabled
                    )
                    Text(stringResource(R.string.palette_dynamic))
                }
                if (!dynamicEnabled) {
                    Text(
                        text = stringResource(R.string.dynamic_color_unavailable),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 48.dp)
                    )
                }
            }
        }

        // Theme Mode
        SettingsSection(title = stringResource(R.string.settings_theme_mode)) {
            val modes = ThemeMode.values()
            val modeStrings = listOf(
                stringResource(R.string.theme_system),
                stringResource(R.string.theme_light),
                stringResource(R.string.theme_dark)
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                modes.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = settings.themeMode == mode,
                        onClick = { viewModel.onThemeModeSelected(mode) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size)
                    ) {
                        Text(modeStrings[index])
                    }
                }
            }
        }

        // Difficulty
        SettingsSection(title = stringResource(R.string.settings_difficulty)) {
            val diffs = listOf(Difficulty.BEGINNER, Difficulty.MEDIUM, Difficulty.EXPERT)
            val diffStrings = listOf(
                stringResource(R.string.difficulty_beginner),
                stringResource(R.string.difficulty_medium),
                stringResource(R.string.difficulty_expert)
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                diffs.forEachIndexed { index, diff ->
                    SegmentedButton(
                        selected = settings.difficulty == diff,
                        onClick = { viewModel.onDifficultySelected(diff) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = diffs.size)
                    ) {
                        Text(diffStrings[index])
                    }
                }
            }
        }

        // Icon Style
        SettingsSection(title = stringResource(R.string.settings_icon_style)) {
            val styles = IconStyle.values()
            val styleStrings = listOf(
                stringResource(R.string.icon_classic),
                stringResource(R.string.icon_numbers),
                stringResource(R.string.icon_shapes),
                stringResource(R.string.icon_signs)
            )
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                styles.forEachIndexed { index, style ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { viewModel.onIconStyleSelected(style) }
                            .background(if (settings.iconStyle == style) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = settings.iconStyle == style,
                            onClick = { viewModel.onIconStyleSelected(style) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(styleStrings[index], modifier = Modifier.weight(1f))
                        Box(modifier = Modifier.size(48.dp)) {
                            PlayerMark(player = Player.X, style = style)
                        }
                        Box(modifier = Modifier.size(48.dp)) {
                            PlayerMark(player = Player.O, style = style, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        content()
    }
}

@Composable
fun getPaletteColor(palette: AppPalette): Color {
    return when (palette) {
        AppPalette.ESMERALDA -> EmeraldLightPrimary
        AppPalette.OCEANO -> OceanLightPrimary
        AppPalette.ATARDECER -> SunsetLightPrimary
        AppPalette.LAVANDA -> LavenderLightPrimary
        AppPalette.ROSA -> RoseLightPrimary
        AppPalette.GRAFITO -> GraphiteLightPrimary
        else -> Color.Transparent
    }
}

@Composable
fun getPaletteName(palette: AppPalette): String {
    return when (palette) {
        AppPalette.ESMERALDA -> stringResource(R.string.palette_emerald)
        AppPalette.OCEANO -> stringResource(R.string.palette_ocean)
        AppPalette.ATARDECER -> stringResource(R.string.palette_sunset)
        AppPalette.LAVANDA -> stringResource(R.string.palette_lavender)
        AppPalette.ROSA -> stringResource(R.string.palette_rose)
        AppPalette.GRAFITO -> stringResource(R.string.palette_graphite)
        AppPalette.DINAMICO -> stringResource(R.string.palette_dynamic)
    }
}
