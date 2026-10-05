package com.v1kth0rx.totitox.domain

/** Tablero inmutable de 3x3 con celdas indexadas de 0 a 8 (fila por fila). */
data class Board(val cells: List<Player?> = List(SIZE) { null }) {

    init {
        require(cells.size == SIZE) { "El tablero debe tener $SIZE celdas" }
    }

    fun place(index: Int, player: Player): Board {
        require(index in 0 until SIZE) { "Índice fuera de rango: $index" }
        require(cells[index] == null) { "La celda $index ya está ocupada" }
        return Board(cells.toMutableList().also { it[index] = player })
    }

    fun emptyCells(): List<Int> = cells.indices.filter { cells[it] == null }

    fun isFull(): Boolean = cells.none { it == null }

    companion object {
        const val SIZE = 9
    }
}
