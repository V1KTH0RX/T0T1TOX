package com.v1kth0rx.totitox.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpertAiTest {

    private val ai = ExpertAi()

    @Test
    fun neverLosesWhenAiStarts() {
        val (games, losses) = simulate(Board(), aiPlayer = Player.X, turn = Player.X)
        assertTrue(games > 0)
        assertEquals(0, losses)
    }

    @Test
    fun neverLosesWhenHumanStarts() {
        val (games, losses) = simulate(Board(), aiPlayer = Player.O, turn = Player.X)
        assertTrue(games > 0)
        assertEquals(0, losses)
    }

    @Test
    fun factoryReturnsExpectedImplementations() {
        assertTrue(AiFactory.create(Difficulty.BEGINNER) is BeginnerAi)
        assertTrue(AiFactory.create(Difficulty.MEDIUM) is MediumAi)
        assertTrue(AiFactory.create(Difficulty.EXPERT) is ExpertAi)
    }

    /** Devuelve (partidas terminadas, partidas perdidas por la IA) explorando todos los movimientos humanos. */
    private fun simulate(board: Board, aiPlayer: Player, turn: Player): Pair<Int, Int> {
        when (val result = evaluate(board)) {
            is GameResult.Win -> return 1 to (if (result.player == aiPlayer) 0 else 1)
            GameResult.Draw -> return 1 to 0
            GameResult.InProgress -> Unit
        }
        if (turn == aiPlayer) {
            val move = ai.nextMove(board, aiPlayer)
            assertTrue(move in board.emptyCells())
            return simulate(board.place(move, aiPlayer), aiPlayer, turn.opponent())
        }
        var games = 0
        var losses = 0
        for (cell in board.emptyCells()) {
            val (g, l) = simulate(board.place(cell, turn), aiPlayer, turn.opponent())
            games += g
            losses += l
        }
        return games to losses
    }
}
