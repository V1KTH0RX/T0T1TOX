package com.v1kth0rx.totitox.domain

import kotlin.random.Random

/**
 * Con 50% de probabilidad juega "óptimo" (gana si puede, bloquea si debe);
 * en caso contrario, o si no hay jugada forzada, elige al azar.
 */
class MediumAi(private val random: Random = Random.Default) : TotitoAi {

    override fun nextMove(board: Board, aiPlayer: Player): Int {
        val empty = board.emptyCells()
        require(empty.isNotEmpty()) { "No hay celdas libres" }
        if (random.nextDouble() < OPTIMAL_PROBABILITY) {
            findWinningMove(board, aiPlayer)?.let { return it }
            findWinningMove(board, aiPlayer.opponent())?.let { return it }
        }
        return empty[random.nextInt(empty.size)]
    }

    private fun findWinningMove(board: Board, player: Player): Int? =
        board.emptyCells().firstOrNull { cell ->
            val result = evaluate(board.place(cell, player))
            result is GameResult.Win && result.player == player
        }

    private companion object {
        const val OPTIMAL_PROBABILITY = 0.5
    }
}
