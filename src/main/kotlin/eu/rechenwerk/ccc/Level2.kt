package eu.rechenwerk.ccc

import eu.rechenwerk.ccc.internals.annotations.Level

enum class IslandType {
    SAME, DIFFERENT
}

@Level(2) fun run(input: Input2): Output2 {
    val coordinates = (input.coordinatePairs + input.coordinatePairs2).map { Coordinate(it) }
    val coordinatePairs = coordinates.windowed(2, 2)

    return Output2(coordinatePairs.map { connected(it[0], it[1], input.map) })
}

private fun connected(a: Coordinate, b: Coordinate, map: List<String>): IslandType {
    val visitedList = mutableListOf<Coordinate>()
    return if (sameIsland(a, b, map, visitedList)) IslandType.SAME else IslandType.DIFFERENT
}
private fun sameIsland(a: Coordinate, b: Coordinate, map: List<String>, visitedList: MutableList<Coordinate>): Boolean {
    if(a in visitedList) return false
    visitedList += a
    if(map[a.y][a.x] == 'W') return false
    if(map[b.y][b.x] == 'W') return false
    if(a == b) return true
    if(sameIsland(Coordinate("${a.x+1},${a.y}"), b, map, visitedList)) return true
    if(sameIsland(Coordinate("${a.x-1},${a.y}"), b, map, visitedList)) return true
    if(sameIsland(Coordinate("${a.x},${a.y+1}"), b, map, visitedList)) return true
    if(sameIsland(Coordinate("${a.x},${a.y-1}"), b, map, visitedList)) return true
    return false
}