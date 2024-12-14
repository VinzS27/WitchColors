package com.witchcolors

class GameGrid(private val rows: Int, private val cols: Int, private var emptyCellCount: Int,
               private var level: Int, private var worldIndex: Int) {

    private val objects = listOf("sfera","mela", "spada", "pozione","cappello","fungo")
    private val colors = listOf("Rosso", "Giallo", "Verde","Celeste", "Blu", "Viola", "Arancione", "Rosa", "Nero", "Bianco")
    private val powers = listOf("Veleno", "Gelo", "Double_Score","Magic_Dust", "ExtraLife")
    private val witchSpells = listOf("buio","nebbia"/*,"mescola","cassa"*/)

    // Griglia di celle vuote
    private var grid: Array<Array<String>> = Array(rows) { Array(cols) { "" } }

    fun getColorAt(row: Int, col: Int): String {
        return grid[row][col]
    }

    fun getRandomObjects(): String {
        return objects.random()
    }

    fun getRandomPower(): String {
        return powers.random()
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

    fun getBombProbability(): Int {
        return when (level) {
            0, 1, 2 -> 0       // Nessuna bomba nei primi due livelli
            3 -> 2             // 5% di probabilità al livello 3
            4, 5 -> 5         // 10% di probabilità ai livelli 4 e 5
            else -> 0
        }
    }

    fun getSpellProbability(): Int {
        if(worldIndex>=1) {
            return when (level) {
                0, 1 -> 5
                2 -> 20
                3 -> 35
                4, 5 -> 50
                else -> 0
            }
        }
        return 0
    }

    fun getWitchSpell(): String{
        return if (worldIndex == 1) witchSpells.random() else ""
    }

    fun getInitialTimer(): Long {
        return 10000L + (level * 10000L)
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
    // Riduce il tempo totale rimanente
    fun reduceTime(millis: Long, timeLeft:Long): Long {
        val reducedTime = (timeLeft - millis).coerceAtLeast(0) // Impedisce tempo negativo
        return reducedTime
    }
}
