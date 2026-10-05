package com.v1kth0rx.totitox.domain

sealed interface GameResult {
    data class Win(val player: Player, val line: List<Int>) : GameResult
    data object Draw : GameResult
    data object InProgress : GameResult
}

val WINNING_LINES: List<List<Int>> = listOf(
    listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
    listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
    listOf(0, 4, 8), listOf(2, 4, 6),
)

fun evaluate(board: Board): GameResult {
    for (line in WINNING_LINES) {
        val first = board.cells[line[0]] ?: continue
        if (board.cells[line[1]] == first && board.cells[line[2]] == first) {
            return GameResult.Win(first, line)
        }
    }
    return if (board.isFull()) GameResult.Draw else GameResult.InProgress
}
