package com.v1kth0rx.totitox.ui.game

import app.cash.turbine.test
import com.v1kth0rx.totitox.data.AppPalette
import com.v1kth0rx.totitox.data.AppSettings
import com.v1kth0rx.totitox.data.IconStyle
import com.v1kth0rx.totitox.data.SettingsRepository
import com.v1kth0rx.totitox.data.ThemeMode
import com.v1kth0rx.totitox.domain.Difficulty
import com.v1kth0rx.totitox.domain.GameResult
import com.v1kth0rx.totitox.domain.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class FakeSettingsRepository : SettingsRepository {
    private val _appSettings = MutableStateFlow(AppSettings())
    override val appSettings: Flow<AppSettings> = _appSettings

    override suspend fun updatePalette(palette: AppPalette) {
        _appSettings.update { it.copy(palette = palette) }
    }

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        _appSettings.update { it.copy(themeMode = themeMode) }
    }

    override suspend fun updateDifficulty(difficulty: Difficulty) {
        _appSettings.update { it.copy(difficulty = difficulty) }
    }

    override suspend fun updateIconStyle(iconStyle: IconStyle) {
        _appSettings.update { it.copy(iconStyle = iconStyle) }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val firstChoiceRandom = object : Random() {
        override fun nextBits(bitCount: Int): Int = 0
        override fun nextInt(until: Int): Int = 0
        override fun nextInt(): Int = 0
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun humanWins_updatesScoreOnce() = runTest(testDispatcher) {
        val fakeRepository = FakeSettingsRepository()
        val viewModel = GameViewModel(fakeRepository, testDispatcher, firstChoiceRandom, 500L)
        
        viewModel.onDifficultyChange(Difficulty.BEGINNER)
        advanceUntilIdle()

        viewModel.uiState.test {
            val initialState = expectMostRecentItem()
            assertEquals(0, initialState.scoreWins)
            
            viewModel.onCellClick(4)
            advanceTimeBy(600)
            
            viewModel.onCellClick(1)
            advanceTimeBy(600)
            
            viewModel.onCellClick(7) // Gana
            advanceUntilIdle()
            
            val finalState = expectMostRecentItem()
            assertTrue(finalState.result is GameResult.Win)
            assertEquals(1, finalState.scoreWins)
            assertEquals(0, finalState.scoreLosses)
        }
    }

    @Test
    fun aiMovesAutomaticallyWhenStarting() = runTest(testDispatcher) {
        val fakeRepository = FakeSettingsRepository()
        val viewModel = GameViewModel(fakeRepository, testDispatcher, firstChoiceRandom, 500L)
        advanceUntilIdle() // humano empieza primero por defecto, X en 0

        viewModel.onCellClick(0) 
        advanceTimeBy(600) // IA responde

        // Al crear nueva partida, le toca a la IA primero (porque se alterna)
        viewModel.onNewGame()
        
        viewModel.uiState.test {
            val state = expectMostRecentItem()
            // Turno de la IA (Player.X)
            assertEquals(Player.X, state.currentPlayer)
            assertEquals(Player.O, state.humanPlayer)
            assertTrue(state.isAiThinking)
            
            advanceTimeBy(600)
            
            val finalState = expectMostRecentItem()
            assertFalse(finalState.isAiThinking)
            assertNotNull(finalState.board.cells[0]) // IA movió en 0 (firstChoiceRandom)
        }
    }

    @Test
    fun ignoreTouchesDuringAiTurn() = runTest(testDispatcher) {
        val fakeRepository = FakeSettingsRepository()
        val viewModel = GameViewModel(fakeRepository, testDispatcher, firstChoiceRandom, 500L)
        advanceUntilIdle()

        viewModel.onCellClick(4)
        
        viewModel.uiState.test {
            val thinkingState = expectMostRecentItem()
            assertTrue(thinkingState.isAiThinking)
            
            // Intenta tocar durante el turno de la IA
            viewModel.onCellClick(1)
            
            advanceTimeBy(600)
            val finalState = expectMostRecentItem()
            
            // La celda 1 no debe estar ocupada por el humano, sino que la IA debió ocupar la 0
            assertNull(finalState.board.cells[1])
            assertEquals(Player.O, finalState.board.cells[0])
        }
    }

    @Test
    fun newGameCancelsPendingAi() = runTest(testDispatcher) {
        val fakeRepository = FakeSettingsRepository()
        val viewModel = GameViewModel(fakeRepository, testDispatcher, firstChoiceRandom, 500L)
        advanceUntilIdle()

        viewModel.onCellClick(4)
        
        // Antes de que pasen los 500ms, damos a nueva partida
        advanceTimeBy(100)
        viewModel.onNewGame()
        
        advanceUntilIdle() // Dejamos terminar todo (esto asegura que cualquier corrutina termine)
        
        val finalState = viewModel.uiState.value
        assertEquals(Player.O, finalState.humanPlayer)
        assertFalse(finalState.isAiThinking)
        assertEquals(1, finalState.board.cells.count { it != null })
    }

    @Test
    fun humanLosesAgainstExpertAi() = runTest(testDispatcher) {
        val fakeRepository = FakeSettingsRepository()
        val viewModel = GameViewModel(fakeRepository, testDispatcher, firstChoiceRandom, 500L)
        viewModel.onDifficultyChange(Difficulty.EXPERT)
        advanceUntilIdle()

        // El experto es invencible. Para que el humano pierda rápido:
        // Humano juega 0
        viewModel.onCellClick(0)
        advanceUntilIdle()
        // Expert juega 4 (centro)
        
        // Humano juega 1
        viewModel.onCellClick(1)
        advanceUntilIdle()
        // Expert juega 2 (bloquea)
        
        // Humano juega 8
        viewModel.onCellClick(8)
        advanceUntilIdle()
        // Expert juega 6 (doble amenaza, o gana directo si no nos dimos cuenta)
        
        // Humano juega 3
        viewModel.onCellClick(3)
        advanceUntilIdle()
        
        // Seguimos jugando hasta que termine
        for (i in 0..8) {
            if (viewModel.uiState.value.result !is GameResult.InProgress) break
            viewModel.onCellClick(i)
            advanceUntilIdle()
        }
        
        val finalState = viewModel.uiState.value
        assertTrue(finalState.result is GameResult.Win)
        assertEquals(1, finalState.scoreLosses)
    }

    @Test
    fun gameEndsInDraw_updatesScoreOnce() = runTest(testDispatcher) {
        val fakeRepository = FakeSettingsRepository()
        val viewModel = GameViewModel(fakeRepository, testDispatcher, firstChoiceRandom, 500L)
        advanceUntilIdle()

        // Set up a draw situation
        // We can just manually place moves, but AI responds.
        // It's easier to simulate clicks and let the AI respond with firstChoiceRandom,
        // but we have to be careful to actually reach a draw.
        // Since we want to test that a Draw updates scoreDraws exactly once,
        // let's do this: X in 0, O in 1, X in 2, O in 3, X in 5, O in 4, X in 6, O in 8, X in 7.
        // To control AI moves entirely, we can use a sequence random.
        val sequenceRandom = object : Random() {
            val seq = mutableListOf(0, 0, 0, 1)
            override fun nextBits(bitCount: Int): Int = 0
            override fun nextInt(until: Int): Int = seq.removeFirst()
            override fun nextInt(): Int = 0
        }
        val drawViewModel = GameViewModel(fakeRepository, testDispatcher, sequenceRandom, 500L)
        advanceUntilIdle()

        drawViewModel.onCellClick(0) // X
        advanceTimeBy(600) // O -> 1
        drawViewModel.onCellClick(2) // X
        advanceTimeBy(600) // O -> 3
        drawViewModel.onCellClick(5) // X
        advanceTimeBy(600) // O -> 4
        drawViewModel.onCellClick(6) // X
        advanceTimeBy(600) // O -> 8
        drawViewModel.onCellClick(7) // X -> Board full, no winner = Draw
        advanceUntilIdle()

        val state = drawViewModel.uiState.value
        assertTrue(state.result is GameResult.Draw)
        assertEquals(1, state.scoreDraws)
        assertEquals(0, state.scoreWins)
        assertEquals(0, state.scoreLosses)
    }

    @Test
    fun changeDifficultyDuringGame_resetsBoardAndCancelsAi() = runTest(testDispatcher) {
        val fakeRepository = FakeSettingsRepository()
        val viewModel = GameViewModel(fakeRepository, testDispatcher, firstChoiceRandom, 500L)
        advanceUntilIdle()

        viewModel.onCellClick(4)
        
        // Before AI finishes thinking (500ms), we change difficulty
        advanceTimeBy(100)
        viewModel.onDifficultyChange(Difficulty.EXPERT)
        
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(Difficulty.EXPERT, state.difficulty)
        // Board is reset
        assertEquals(0, state.board.cells.count { it != null })
        assertFalse(state.isAiThinking)
    }
}
