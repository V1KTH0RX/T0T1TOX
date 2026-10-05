package com.v1kth0rx.totitox.domain

enum class Player {
    X,
    O;

    fun opponent(): Player = if (this == X) O else X
}
