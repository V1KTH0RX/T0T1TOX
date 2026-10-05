package com.v1kth0rx.totitox.domain

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MediumAiTest {

    /** O puede ganar en 5 (3,4,5); X amenaza ganar en 2 (0,1,2). */
    private val board = Board()
        .place(0, Player.X).place(1, Player.X)
        .place(3, Player.O).place(4, Player.O)
        .place(6, Player.X)

    @Test
    fun sameSeedGivesSameMoves() {
        val a = MediumAi(Random(7))
        val b = MediumAi(Random(7))
        repeat(50) {
            assertEquals(a.nextMove(board, Player.O), b.nextMove(board, Player.O))
        }
    }

    @Test
    fun alwaysReturnsFreeCell() {
        val ai = MediumAi(Random(1))
        repeat(200) { assertTrue(ai.nextMove(board, Player.O) in board.emptyCells()) }
    }

    @Test
    fun playsOptimallyWhenRandomIsBelowThreshold() {
        // Random fijo que siempre devuelve 0.0 => rama óptima
        val optimal = MediumAi(FixedRandom(0.0))
        assertEquals(5, optimal.nextMove(board, Player.O)) // gana
        val noWin = Board().place(0, Player.X).place(1, Player.X).place(4, Player.O)
        assertEquals(2, optimal.nextMove(noWin, Player.O)) // bloquea
    }

    @Test
    fun playsRandomlyWhenRandomIsAboveThreshold() {
        // nextDouble() = 0.99 => rama aleatoria; nextInt() = 0 => primera celda libre
        val random = MediumAi(FixedRandom(0.99))
        assertEquals(board.emptyCells().first(), random.nextMove(board, Player.O))
    }

    private class FixedRandom(private val double: Double) : Random() {
        override fun nextBits(bitCount: Int): Int = 0
        override fun nextDouble(): Double = double
        override fun nextInt(until: Int): Int = 0
    }
}
