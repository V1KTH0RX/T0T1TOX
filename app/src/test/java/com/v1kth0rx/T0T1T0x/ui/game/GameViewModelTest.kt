package com.v1kth0rx.T0T1T0x.ui.game

import app.cash.turbine.test
import com.v1kth0rx.T0T1T0x.domain.AiFactory
import com.v1kth0rx.T0T1T0x.domain.Board
import com.v1kth0rx.T0T1T0x.domain.Difficulty
import com.v1kth0rx.T0T1T0x.domain.GameResult
import com.v1kth0rx.T0T1T0x.domain.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        val viewModel = GameViewModel(testDispatcher, firstChoiceRandom, 500L)
        
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
        val viewModel = GameViewModel(testDispatcher, firstChoiceRandom, 500L)
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
        val viewModel = GameViewModel(testDispatcher, firstChoiceRandom, 500L)
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
        val viewModel = GameViewModel(testDispatcher, firstChoiceRandom, 500L)
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
        val viewModel = GameViewModel(testDispatcher, firstChoiceRandom, 500L)
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
}
