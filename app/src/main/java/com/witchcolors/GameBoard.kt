package com.witchcolors

class GameBoard(private val rows: Int, private val cols: Int, private var emptyCellCount: Int) {

    private val objects = listOf(/*"sfera","mela", "spada", "pozione",*/"cappello"/*,"fungo"*/)
    private val colors = listOf("Rosso", "Giallo", "Verde","Celeste", "Blu", "Viola", "Arancione", "Rosa", "Nero", "Bianco")
    private val powerups = listOf("Veleno", "Gelo", "Double_Score","Magic_Dust", "ExtraLife")

    // Griglia di celle vuote
    private var grid: Array<Array<String>> = Array(rows) { Array(cols) { "" } }

    fun getColorAt(row: Int, col: Int): String {
        return grid[row][col]
    }

    fun getRandomObjects(): String {
        return objects.random()
    }

    fun getRandomPower(): String {
        return powerups.random()
    }

    // Restituisce una lista di coordinate di celle piene
    fun getFullCells(): List<Pair<Int, Int>> {
        val fullCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (grid[i][j] != "") {
                    fullCells.add(Pair(i, j))
                }
            }
        }
        return fullCells
    }

    fun updateEmptyCells(cells:Int){
        emptyCellCount-=cells
    }

    fun resetBoard() {
        // Genera posizioni casuali per celle vuote
        val totalCells = rows * cols
        val emptyCells = mutableSetOf<Pair<Int, Int>>()
        while (emptyCells.size < emptyCellCount) {
            val randomCell = Pair((0 until rows).random(), (0 until cols).random())
            emptyCells.add(randomCell)
        }

        // Aggiungi colori unici per celle non vuote
        val nonEmptyCellCount = totalCells - emptyCellCount
        val selectedColors = colors.shuffled().take(nonEmptyCellCount)

        grid = Array(rows) { Array(cols) { "" } }
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


}
