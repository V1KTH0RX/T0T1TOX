package com.v1kth0rx.totitox.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v1kth0rx.totitox.data.AppPalette
import com.v1kth0rx.totitox.data.AppSettings
import com.v1kth0rx.totitox.data.IconStyle
import com.v1kth0rx.totitox.data.SettingsRepository
import com.v1kth0rx.totitox.data.ThemeMode
import com.v1kth0rx.totitox.domain.Difficulty
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    val appSettings: StateFlow<AppSettings> = settingsRepository.appSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun onPaletteSelected(palette: AppPalette) {
        viewModelScope.launch {
            settingsRepository.updatePalette(palette)
        }
    }

    fun onThemeModeSelected(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.updateThemeMode(themeMode)
        }
    }

    fun onDifficultySelected(difficulty: Difficulty) {
        viewModelScope.launch {
            settingsRepository.updateDifficulty(difficulty)
        }
    }

    fun onIconStyleSelected(iconStyle: IconStyle) {
        viewModelScope.launch {
            settingsRepository.updateIconStyle(iconStyle)
        }
    }

    companion object {
        fun provideFactory(
            settingsRepository: SettingsRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(settingsRepository) as T
            }
        }
    }
}
