package com.v1kth0rx.totitox.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.v1kth0rx.totitox.data.SettingsRepository
import com.v1kth0rx.totitox.domain.AiFactory
import com.v1kth0rx.totitox.domain.Board
import com.v1kth0rx.totitox.domain.Difficulty
import com.v1kth0rx.totitox.domain.GameResult
import com.v1kth0rx.totitox.domain.Player
import com.v1kth0rx.totitox.domain.evaluate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(
    private val settingsRepository: SettingsRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val random: Random = Random.Default,
    private val aiDelayMs: Long = 500L
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var aiJob: Job? = null
    private var isInitialized = false

    init {
        viewModelScope.launch {
            settingsRepository.appSettings
                .map { it.difficulty }
                .distinctUntilChanged()
                .collect { newDifficulty ->
                    if (!isInitialized) {
                        // Just set initial difficulty without restarting game
                        _uiState.update { it.copy(difficulty = newDifficulty) }
                        isInitialized = true
                        checkAiTurn()
                    } else if (_uiState.value.difficulty != newDifficulty) {
                        aiJob?.cancel()
                        _uiState.update { currentState ->
                            currentState.copy(
                                difficulty = newDifficulty,
                                board = Board(),
                                currentPlayer = Player.X,
                                result = GameResult.InProgress,
                                isAiThinking = false,
                                isGameOverDialogVisible = false
                            )
                        }
                        checkAiTurn()
                    }
                }
        }
    }

    fun onCellClick(index: Int) {
        val state = _uiState.value
        if (state.isAiThinking || state.result !is GameResult.InProgress || state.board.cells[index] != null) {
            return
        }

        if (state.currentPlayer == state.humanPlayer) {
            makeMove(index)
        }
    }

    fun onNewGame() {
        aiJob?.cancel()
        _uiState.update { currentState ->
            val newHumanStarts = currentState.humanPlayer != Player.X
            val newHuman = if (newHumanStarts) Player.X else Player.O
            currentState.copy(
                board = Board(),
                humanPlayer = newHuman,
                currentPlayer = Player.X,
                result = GameResult.InProgress,
                isAiThinking = false,
                isGameOverDialogVisible = false
            )
        }
        checkAiTurn()
    }

    fun onDifficultyChange(difficulty: Difficulty) {
        viewModelScope.launch {
            settingsRepository.updateDifficulty(difficulty)
        }
    }

    fun dismissGameOverDialog() {
        _uiState.update { it.copy(isGameOverDialogVisible = false) }
    }

    private fun makeMove(index: Int) {
        _uiState.update { currentState ->
            val newBoard = currentState.board.place(index, currentState.currentPlayer)
            val newResult = evaluate(newBoard)
            
            val updatedScore = if (currentState.result is GameResult.InProgress && newResult !is GameResult.InProgress) {
                when (newResult) {
                    is GameResult.Win -> {
                        if (newResult.player == currentState.humanPlayer) {
                            currentState.copy(scoreWins = currentState.scoreWins + 1)
                        } else {
                            currentState.copy(scoreLosses = currentState.scoreLosses + 1)
                        }
                    }
                    is GameResult.Draw -> currentState.copy(scoreDraws = currentState.scoreDraws + 1)
                    else -> currentState
                }
            } else currentState

            updatedScore.copy(
                board = newBoard,
                currentPlayer = currentState.currentPlayer.opponent(),
                result = newResult,
                isGameOverDialogVisible = newResult !is GameResult.InProgress
            )
        }
        checkAiTurn()
    }

    private fun checkAiTurn() {
        val state = _uiState.value
        if (state.result is GameResult.InProgress && state.currentPlayer != state.humanPlayer) {
            _uiState.update { it.copy(isAiThinking = true) }
            aiJob = viewModelScope.launch(defaultDispatcher) {
                delay(aiDelayMs)
                val ai = AiFactory.create(state.difficulty, random)
                val currentState = _uiState.value
                if (currentState.result is GameResult.InProgress && currentState.currentPlayer != currentState.humanPlayer) {
                    val move = ai.nextMove(currentState.board, currentState.currentPlayer)
                    makeMove(move)
                }
                _uiState.update { it.copy(isAiThinking = false) }
            }
        }
    }

    companion object {
        fun provideFactory(
            settingsRepository: SettingsRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GameViewModel(settingsRepository) as T
            }
        }
    }
}
