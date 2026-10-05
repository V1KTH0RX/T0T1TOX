package com.v1kth0rx.totitox.ui.game

import com.v1kth0rx.totitox.domain.Board
import com.v1kth0rx.totitox.domain.Difficulty
import com.v1kth0rx.totitox.domain.GameResult
import com.v1kth0rx.totitox.domain.Player

data class GameUiState(
    val board: Board = Board(),
    val humanPlayer: Player = Player.X,
    val currentPlayer: Player = Player.X,
    val result: GameResult = GameResult.InProgress,
    val difficulty: Difficulty = Difficulty.BEGINNER,
    val isAiThinking: Boolean = false,
    val scoreWins: Int = 0,
    val scoreLosses: Int = 0,
    val scoreDraws: Int = 0,
    val isGameOverDialogVisible: Boolean = false
)
