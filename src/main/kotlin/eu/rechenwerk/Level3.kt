package eu.rechenwerk

import eu.rechenwerk.ccc.external.Level
import eu.rechenwerk.ccc.external.Many
import eu.rechenwerk.ccc.external.Validator

@Level(3) fun level3(
    n: Int,
    @Many("n", Lawn::class) lawns: List<Lawn>
): String {
    return lawns.map {
        if (it.validate()) "VALID"
        else "INVALID"
    }.joinToString("\n")
}

class Lawn(
    val width: Int,
    val height: Int,
    @Many("height", String::class)val lawn: List<String>,
    val path: String
)

fun Lawn.validate(): Boolean {
    val myLawn = lawn.map{row ->
        row.map{ char -> when(char) {
            '.' -> Cell.UNVISITED
            'X' -> Cell.TREE
            else -> {throw IllegalArgumentException("Unexpected field in lawn.")}
        } }.toMutableList()
    }.toMutableList()

    val (cWidth, cHeight) = path.getPathSize()
    if(cWidth != width || cHeight != height) return false

    var (row, col) = path.startCoordinates()

    if(myLawn[row][col] != Cell.UNVISITED) return false
    myLawn[row][col] = Cell.VISITED

    path.forEach {
        when(it) {
            'W' -> row--
            'A' -> col--
            'S' -> row++
            'D' -> col++
        }
        if(myLawn[row][col] != Cell.UNVISITED) return false
        myLawn[row][col] = Cell.VISITED
    }

    return !myLawn.flatten().any{ it == Cell.UNVISITED }
}

private fun String.startCoordinates(): Pair<Int, Int> {
    var row = 0
    var col = 0

    var rowMin = 0
    var colMin = 0

    this.forEach { action ->
        when(action) {
            'W' -> row--
            'A' -> col--
            'S' -> row++
            'D' -> col++
        }
        if (row < rowMin) {
            rowMin = row
        }
        if(col < colMin) {
            colMin = col
        }
    }
    return Pair(-rowMin, -colMin)
}

enum class Cell {
    TREE, UNVISITED, VISITED
}
