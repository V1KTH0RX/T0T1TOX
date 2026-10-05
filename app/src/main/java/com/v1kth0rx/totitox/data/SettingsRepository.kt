package com.v1kth0rx.totitox.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.v1kth0rx.totitox.domain.Difficulty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.catch
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

interface SettingsRepository {
    val appSettings: Flow<AppSettings>
    suspend fun updatePalette(palette: AppPalette)
    suspend fun updateThemeMode(themeMode: ThemeMode)
    suspend fun updateDifficulty(difficulty: Difficulty)
    suspend fun updateIconStyle(iconStyle: IconStyle)
}

class DataStoreSettingsRepository(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object PreferencesKeys {
        val PALETTE = stringPreferencesKey("palette")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DIFFICULTY = stringPreferencesKey("difficulty")
        val ICON_STYLE = stringPreferencesKey("icon_style")
    }

    override val appSettings: Flow<AppSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
        val paletteStr = preferences[PreferencesKeys.PALETTE]
        val themeModeStr = preferences[PreferencesKeys.THEME_MODE]
        val difficultyStr = preferences[PreferencesKeys.DIFFICULTY]
        val iconStyleStr = preferences[PreferencesKeys.ICON_STYLE]

        AppSettings(
            palette = safeValueOf(paletteStr, AppPalette.DINAMICO),
            themeMode = safeValueOf(themeModeStr, ThemeMode.SISTEMA),
            difficulty = safeValueOf(difficultyStr, Difficulty.MEDIUM),
            iconStyle = safeValueOf(iconStyleStr, IconStyle.CLASSIC)
        )
    }

    override suspend fun updatePalette(palette: AppPalette) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PALETTE] = palette.name
        }
    }

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }

    override suspend fun updateDifficulty(difficulty: Difficulty) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DIFFICULTY] = difficulty.name
        }
    }

    override suspend fun updateIconStyle(iconStyle: IconStyle) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ICON_STYLE] = iconStyle.name
        }
    }

    private inline fun <reified T : Enum<T>> safeValueOf(value: String?, default: T): T {
        if (value == null) return default
        return try {
            enumValueOf<T>(value)
        } catch (e: IllegalArgumentException) {
            default
        }
    }
}
