package com.v1kth0rx.totitox.domain

import kotlin.random.Random

class BeginnerAi(private val random: Random = Random.Default) : TotitoAi {
    override fun nextMove(board: Board, aiPlayer: Player): Int {
        val empty = board.emptyCells()
        require(empty.isNotEmpty()) { "No hay celdas libres" }
        return empty[random.nextInt(empty.size)]
    }
}
