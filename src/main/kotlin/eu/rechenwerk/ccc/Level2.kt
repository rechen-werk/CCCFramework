package eu.rechenwerk.ccc

import eu.rechenwerk.ccc.internals.annotations.Level
import eu.rechenwerk.ccc.internals.annotations.Many

@Level(2) fun level2(count: Int, @Many("count", String::class) paths: List<String>): String {
    return paths
        .map { line -> line.pathDimensions() }
        .joinToString("\n") { pair -> "${pair.first} ${pair.second}" }
}

fun String.pathDimensions(): Pair<Int, Int> {
    var x = 0
    var y = 0
    var maxX = 0
    var minX = 0
    var maxY = 0
    var minY = 0
    forEach { when(it) {
        'W' -> {
            y--
            if(y > maxY) maxY = y
            if(y < minY) minY = y
        }
        'A' -> {
            x--
            if(x > maxX) maxX = x
            if(x < minX) minX = x

        }'S' -> {
            y++
            if(y > maxY) maxY = y
            if(y < minY) minY = y
        }'D' -> {
            x++
            if(x > maxX) maxX = x
            if(x < minX) minX = x
        }
        else -> {throw IllegalArgumentException()}
    }}
    return Pair(1 + maxX - minX,1 + maxY - minY)
}