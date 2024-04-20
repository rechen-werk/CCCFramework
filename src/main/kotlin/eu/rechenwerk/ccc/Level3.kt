package eu.rechenwerk.ccc

import eu.rechenwerk.ccc.internals.annotations.Level
import eu.rechenwerk.ccc.internals.annotations.Many

@Level(3) fun level3(count: Int, @Many("count", LawnMower::class) lawns: List<LawnMower>): String {
   return lawns.joinToString("\n") { if (it.hasValidPath()) "VALID" else "INVALID" }
}

data class LawnMower(
    val width: Int,
    val height: Int,
    @Many("height", String::class) val lawn: List<String>,
    val path: String) {

    fun hasValidPath(): Boolean {
        val (widthP, heightP) = path.pathDimensions()
        var (x, y) = path.startCoordinates()
        val copy = lawn.map { row -> row.toCharArray() }

        if(width != widthP || height != heightP) return false

        if(copy[y][x] == 'X') return false
        else copy[y][x] = 'X'

        path.forEach { tile ->
            when(tile) {
                'W' -> y--
                'A' -> x--
                'S' -> y++
                'D' -> x++
                else -> {throw IllegalArgumentException()}
            }
            if(copy[y][x] == 'X') return false
            else copy[y][x] = 'X'
        }

        return copy.all{ row -> row.all { tile -> tile == 'X' }}
    }
}

fun String.startCoordinates(): Pair<Int, Int> {
    var x = 0
    var y = 0
    var minX = 0
    var minY = 0
    forEach { when(it) {
        'W' -> {
            y--
            if(y < minY) minY = y
        }
        'A' -> {
            x--
            if(x < minX) minX = x
        }'S' -> {
            y++
            if(y < minY) minY = y
        }'D' -> {
            x++
            if(x < minX) minX = x
        }
        else -> {throw IllegalArgumentException()}
    } }
    return Pair(-minX, -minY)
}