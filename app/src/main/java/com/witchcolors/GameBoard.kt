package com.witchcolors

class GameBoard(private val rows: Int, private val cols: Int, private val emptyCellCount: Int) {

    private val colors = listOf(
        "Rosso", "Blu", "Verde", "Giallo", "Rosa", "Nero", "Celeste", "Arancione", "Viola", "Bianco"
    )

    // Griglia di colori casuali
    private val grid: Array<Array<String>> = Array(rows) { Array(cols) { "" } }

    fun getColorAt(row: Int, col: Int): String {
        return grid[row][col]
    }

    fun resetBoard() {
        // Genera posizioni casuali per celle vuote
        val totalCells = rows * cols
        val emptyCells = mutableSetOf<Pair<Int, Int>>()
        while (emptyCells.size < emptyCellCount) {
            val randomCell = Pair((0 until rows).random(), (0 until cols).random())
            emptyCells.add(randomCell)
        }

        // Prende colori unici per celle non vuote
        val nonEmptyCellCount = totalCells - emptyCellCount
        val selectedColors = colors.shuffled().take(nonEmptyCellCount)

        var colorIndex = 0
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (Pair(i, j) !in emptyCells) {
                    grid[i][j] = selectedColors[colorIndex]
                    colorIndex++
                } else {
                    grid[i][j] = "" // lascia cella vuota
                }
            }
        }
    }

    // Restituisce una lista di coordinate di celle piene
    fun getFullCells(): List<Pair<Int, Int>> {
        val nonEmptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (grid[i][j] != "") {
                    nonEmptyCells.add(Pair(i, j))
                }
            }
        }
        return nonEmptyCells
    }
}
