package eu.rechenwerk

import eu.rechenwerk.ccc.external.Level
import eu.rechenwerk.ccc.external.Many

@Level(2) fun level2(
    n: Int,
    @Many("n", String::class) paths: List<String>
): String {
    return paths.map{ path ->
        val size = path.getPathSize()
        "${size.first} ${size.second}"
    }.joinToString("\n")
}

fun String.getPathSize(): Pair<Int, Int> {
    var xMax = 0
    var xMin = 0
    var yMax = 0
    var yMin = 0

    var x = 0
    var y = 0

    for (dir in this) {
        when(dir) {
            'W' -> y++
            'S' -> y--
            'A' -> x--
            'D' -> x++
        }
        if(x < xMin) {
            xMin = x
        }
        if(x > xMax) {
            xMax = x
        }
        if(y < yMin) {
            yMin = y
        }
        if(y > yMax) {
            yMax = y
        }
    }
    return Pair(xMax - xMin + 1, yMax - yMin + 1)
}
