package com.v1kth0rx.T0T1T0x.domain

interface TotitoAi {
    /** Devuelve el índice (0..8) de una celda libre donde jugar. */
    fun nextMove(board: Board, aiPlayer: Player): Int
}
