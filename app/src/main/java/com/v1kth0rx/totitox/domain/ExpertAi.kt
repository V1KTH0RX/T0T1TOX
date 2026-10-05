package com.v1kth0rx.totitox.domain

/** IA invencible: Minimax con poda alfa-beta. Prefiere ganar rápido y perder tarde. */
class ExpertAi : TotitoAi {

    override fun nextMove(board: Board, aiPlayer: Player): Int {
        val empty = board.emptyCells()
        require(empty.isNotEmpty()) { "No hay celdas libres" }
        var bestMove = empty.first()
        var bestScore = Int.MIN_VALUE
        for (cell in empty) {
            val score = minimax(
                board = board.place(cell, aiPlayer),
                current = aiPlayer.opponent(),
                ai = aiPlayer,
                depth = 1,
                alpha = bestScore,
                beta = Int.MAX_VALUE,
            )
            if (score > bestScore) {
                bestScore = score
                bestMove = cell
            }
        }
        return bestMove
    }

    private fun minimax(
        board: Board,
        current: Player,
        ai: Player,
        depth: Int,
        alpha: Int,
        beta: Int,
    ): Int {
        when (val result = evaluate(board)) {
            is GameResult.Win -> return if (result.player == ai) 10 - depth else depth - 10
            GameResult.Draw -> return 0
            GameResult.InProgress -> Unit
        }
        var a = alpha
        var b = beta
        return if (current == ai) {
            var best = Int.MIN_VALUE
            for (cell in board.emptyCells()) {
                best = maxOf(best, minimax(board.place(cell, current), current.opponent(), ai, depth + 1, a, b))
                a = maxOf(a, best)
                if (a >= b) break
            }
            best
        } else {
            var best = Int.MAX_VALUE
            for (cell in board.emptyCells()) {
                best = minOf(best, minimax(board.place(cell, current), current.opponent(), ai, depth + 1, a, b))
                b = minOf(b, best)
                if (a >= b) break
            }
            best
        }
    }
}
