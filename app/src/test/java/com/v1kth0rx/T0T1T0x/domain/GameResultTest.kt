package com.v1kth0rx.T0T1T0x.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameResultTest {

    @Test
    fun detectsWinOnAllEightLines() {
        assertEquals(8, WINNING_LINES.size)
        for (line in WINNING_LINES) {
            for (player in Player.entries) {
                val board = line.fold(Board()) { b, i -> b.place(i, player) }
                assertEquals(GameResult.Win(player, line), evaluate(board))
            }
        }
    }

    @Test
    fun detectsDraw() {
        // X O X
        // X O O
        // O X X
        val moves = listOf(
            0 to Player.X, 1 to Player.O, 2 to Player.X,
            3 to Player.X, 4 to Player.O, 5 to Player.O,
            6 to Player.O, 7 to Player.X, 8 to Player.X,
        )
        val board = moves.fold(Board()) { b, (i, p) -> b.place(i, p) }
        assertEquals(GameResult.Draw, evaluate(board))
    }

    @Test
    fun detectsInProgress() {
        assertEquals(GameResult.InProgress, evaluate(Board()))
        val board = Board().place(0, Player.X).place(4, Player.O)
        assertEquals(GameResult.InProgress, evaluate(board))
    }

    @Test
    fun boardIsImmutableAndTracksCells() {
        val empty = Board()
        val next = empty.place(3, Player.X)
        assertEquals(9, empty.emptyCells().size)
        assertTrue(3 !in next.emptyCells())
        assertEquals(Player.O, Player.X.opponent())
    }
}
