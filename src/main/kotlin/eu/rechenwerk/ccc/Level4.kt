package eu.rechenwerk.ccc

import eu.rechenwerk.ccc.internals.annotations.Level
import kotlin.math.abs

@Level(4) fun run(input: Input4): Output4 {
    val coordinates = (input.coordinatePairs + input.coordinatePairs2).map { Coordinate(it) }
    val coordinatePairs = coordinates.windowed(2, 2)

    val visited = mutableSetOf<Coordinate>()
    val path = coordinatePairs.map { findRoute(it[0], it[1], input.map, visited, listOf(it[0])) }

    return Output4(path)
}

private fun findRoute(start: Coordinate, goal: Coordinate, map: List<String>, visited: MutableSet<Coordinate>, path: List<Coordinate>): List<Coordinate> {
    if(start == goal) return path + goal
    val surroundingStart = mutableListOf(
        Coordinate(start.x+1, start.y+1),
        Coordinate(start.x+1, start.y-1),
        Coordinate(start.x+1, start.y),
        Coordinate(start.x-1, start.y+1),
        Coordinate(start.x-1, start.y-1),
        Coordinate(start.x-1, start.y),
        Coordinate(start.x, start.y+1),
        Coordinate(start.x, start.y-1),
    ).filter {
        try {
            map[it.y][it.x] == 'W' && !visited.contains(it)
        } catch (e: IndexOutOfBoundsException) {
            false
        }
    }.toMutableList()

    val maybeResult = surroundingStart.firstOrNull { it == goal }
    if(maybeResult != null) return path + maybeResult
    surroundingStart.forEach{ visited.add(it) }

    while(surroundingStart.isNotEmpty()) {
        val closestCandidate = closest(goal, surroundingStart)
        surroundingStart.remove(closestCandidate)
        val result = findRoute(closestCandidate, goal, map, visited, path + closestCandidate)
        if (result.isNotEmpty()) return result
    }
    return emptyList()
}

private fun closest(ref: Coordinate, rest: List<Coordinate>): Coordinate {
    return rest.minBy { Math.max(abs(ref.x - it.x), abs(ref.y - it.y)) }
}
