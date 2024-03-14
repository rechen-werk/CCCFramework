package eu.rechenwerk.ccc

import eu.rechenwerk.ccc.internals.annotations.Level
import kotlin.math.absoluteValue
import kotlin.math.sign

enum class RouteType {
    VALID, INVALID
}

@Level(3) fun run(input: Input3): Output3 {
    val routes = input.coordinates.map { it.split(" ").map { Coordinate(it) } }

    return Output3(routes.map { isCrossing(it) })
}

fun isCrossing(route: List<Coordinate>):RouteType {
    return if(crossing(route)) RouteType.INVALID else RouteType.VALID
}

fun crossing(route: List<Coordinate>): Boolean {
    val visited = mutableListOf<HalfCoordinate>()
    var last = route.first()
    route.drop(1)
    visited.add(HalfCoordinate(last.x.toFloat(), last.y.toFloat()))
    for(next in route) {
        val dirX = next.x - last.x
        val dirY = next.y - last.y

        val stepSizeX = 0.5f * sign(dirX.toDouble()).toFloat()
        val stepSizeY = 0.5f * sign(dirY.toDouble()).toFloat()
        val repeats = (dirX.absoluteValue).coerceAtLeast(dirY.absoluteValue) * 2
        println("last: $last, next: $next, dirX: $dirX, dirY: $dirY, stepSizeX: $stepSizeX, stepSizeY: $stepSizeY, repeats: $repeats")

        for (i in 1 .. repeats) {
            visited.add(HalfCoordinate(last.x+i*stepSizeX, last.y+i*stepSizeY))
        }
        last = next
    }
    val distinctVisited = visited.distinct()
    println(visited)
    return distinctVisited.size != visited.size
}

data class HalfCoordinate(val x: Float, val y: Float)
