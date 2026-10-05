package com.v1kth0rx.totitox.domain

import kotlin.random.Random
import org.junit.Assert.assertTrue
import org.junit.Test

class BeginnerAiTest {

    @Test
    fun alwaysReturnsFreeCell() {
        val ai = BeginnerAi(Random(42))
        repeat(500) {
            var board = Board()
            val rnd = Random(it)
            repeat(rnd.nextInt(0, 9)) {
                board = board.place(board.emptyCells().random(rnd), Player.X)
            }
            val move = ai.nextMove(board, Player.O)
            assertTrue(move in board.emptyCells())
        }
    }
}
