package com.v1kth0rx.totitox.domain

import kotlin.random.Random

enum class Difficulty { BEGINNER, MEDIUM, EXPERT }

object AiFactory {
    fun create(difficulty: Difficulty, random: Random = Random.Default): TotitoAi =
        when (difficulty) {
            Difficulty.BEGINNER -> BeginnerAi(random)
            Difficulty.MEDIUM -> MediumAi(random)
            Difficulty.EXPERT -> ExpertAi()
        }
}
