package com.v1kth0rx.T0T1T0x.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.v1kth0rx.T0T1T0x.domain.Difficulty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreSettingsRepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: DataStoreSettingsRepository
    private lateinit var testFile: File

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        testFile = File.createTempFile("settings-${UUID.randomUUID()}", ".preferences_pb")
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { testFile }
        )
        repository = DataStoreSettingsRepository(dataStore)
    }

    @After
    fun teardown() {
        testFile.delete()
        Dispatchers.resetMain()
    }

    @Test
    fun defaultValuesAreCorrect() = testScope.runTest {
        repository.appSettings.test {
            val settings = awaitItem()
            assertEquals(AppPalette.DINAMICO, settings.palette)
            assertEquals(ThemeMode.SISTEMA, settings.themeMode)
            assertEquals(Difficulty.MEDIUM, settings.difficulty)
            assertEquals(IconStyle.CLASSIC, settings.iconStyle)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updatePalette_emitsNewPalette() = testScope.runTest {
        repository.appSettings.test {
            awaitItem() // initial
            repository.updatePalette(AppPalette.ESMERALDA)
            val settings = awaitItem()
            assertEquals(AppPalette.ESMERALDA, settings.palette)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateThemeMode_emitsNewThemeMode() = testScope.runTest {
        repository.appSettings.test {
            awaitItem() // initial
            repository.updateThemeMode(ThemeMode.OSCURO)
            val settings = awaitItem()
            assertEquals(ThemeMode.OSCURO, settings.themeMode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateDifficulty_emitsNewDifficulty() = testScope.runTest {
        repository.appSettings.test {
            awaitItem() // initial
            repository.updateDifficulty(Difficulty.EXPERT)
            val settings = awaitItem()
            assertEquals(Difficulty.EXPERT, settings.difficulty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateIconStyle_emitsNewIconStyle() = testScope.runTest {
        repository.appSettings.test {
            awaitItem() // initial
            repository.updateIconStyle(IconStyle.SHAPES)
            val settings = awaitItem()
            assertEquals(IconStyle.SHAPES, settings.iconStyle)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun ioException_emitsDefaultValues() = testScope.runTest {
        val failingDataStore = object : DataStore<Preferences> {
            override val data: kotlinx.coroutines.flow.Flow<Preferences> = kotlinx.coroutines.flow.flow {
                throw java.io.IOException("Test IO Exception")
            }
            override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
                throw java.io.IOException("Test IO Exception")
            }
        }
        val failingRepo = DataStoreSettingsRepository(failingDataStore)
        
        failingRepo.appSettings.test {
            val settings = awaitItem()
            assertEquals(AppPalette.DINAMICO, settings.palette)
            assertEquals(ThemeMode.SISTEMA, settings.themeMode)
            assertEquals(Difficulty.MEDIUM, settings.difficulty)
            assertEquals(IconStyle.CLASSIC, settings.iconStyle)
            awaitComplete()
        }
    }
}
