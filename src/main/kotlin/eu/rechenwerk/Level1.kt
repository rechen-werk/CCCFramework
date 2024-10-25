package eu.rechenwerk

import eu.rechenwerk.ccc.external.Level
import eu.rechenwerk.ccc.external.Many

@Level(1) fun level1 (
    n: Int,
    @Many("n", Room::class) rooms: List<Room>
): String {
    return rooms.map { it.nDesks() }.joinToString("\n")
}

class Room(val x: Int, val y: Int) {
    fun nDesks(): Int {
        return x / 3 * y;
    }
}