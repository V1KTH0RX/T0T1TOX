package com.v1kth0rx.T0T1T0x.ui.settings

import app.cash.turbine.test
import com.v1kth0rx.T0T1T0x.data.AppPalette
import com.v1kth0rx.T0T1T0x.data.IconStyle
import com.v1kth0rx.T0T1T0x.data.ThemeMode
import com.v1kth0rx.T0T1T0x.domain.Difficulty
import com.v1kth0rx.T0T1T0x.ui.game.FakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun updatePalette_updatesStateFlow() = runTest(testDispatcher) {
        val fakeRepo = FakeSettingsRepository()
        val viewModel = SettingsViewModel(fakeRepo)

        viewModel.appSettings.test {
            awaitItem() // initial
            viewModel.onPaletteSelected(AppPalette.LAVANDA)
            val updated = awaitItem()
            assertEquals(AppPalette.LAVANDA, updated.palette)
        }
    }

    @Test
    fun updateThemeMode_updatesStateFlow() = runTest(testDispatcher) {
        val fakeRepo = FakeSettingsRepository()
        val viewModel = SettingsViewModel(fakeRepo)

        viewModel.appSettings.test {
            awaitItem() // initial
            viewModel.onThemeModeSelected(ThemeMode.CLARO)
            val updated = awaitItem()
            assertEquals(ThemeMode.CLARO, updated.themeMode)
        }
    }

    @Test
    fun updateDifficulty_updatesStateFlow() = runTest(testDispatcher) {
        val fakeRepo = FakeSettingsRepository()
        val viewModel = SettingsViewModel(fakeRepo)

        viewModel.appSettings.test {
            awaitItem() // initial
            viewModel.onDifficultySelected(Difficulty.EXPERT)
            val updated = awaitItem()
            assertEquals(Difficulty.EXPERT, updated.difficulty)
        }
    }

    @Test
    fun updateIconStyle_updatesStateFlow() = runTest(testDispatcher) {
        val fakeRepo = FakeSettingsRepository()
        val viewModel = SettingsViewModel(fakeRepo)

        viewModel.appSettings.test {
            awaitItem() // initial
            viewModel.onIconStyleSelected(IconStyle.SIGNS)
            val updated = awaitItem()
            assertEquals(IconStyle.SIGNS, updated.iconStyle)
        }
    }
}
