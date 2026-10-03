package com.v1kth0rx.T0T1T0x.domain

enum class Player {
    X,
    O;

    fun opponent(): Player = if (this == X) O else X
}
