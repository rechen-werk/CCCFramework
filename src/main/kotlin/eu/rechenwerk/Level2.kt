package eu.rechenwerk

import eu.rechenwerk.ccc.external.Level
import eu.rechenwerk.ccc.external.Many
import eu.rechenwerk.ccc.external.Validated
import eu.rechenwerk.ccc.external.Validator

@Validated
@Level(2) fun level2 (
    n: Int,
    @Many("n", Room2::class) rooms: List<Room2>
): String {
    return rooms.map { it.placeDesks() }.joinToString("\n")
}

@Validator(2) fun validator2 (
    n: Int,
    @Many("n", Room2::class) rooms: List<Room2>
): Boolean {
    return true
}

class Room2(val x: Int, val y: Int, nTables: Int) {
    fun placeDesks(): String {
        var tables = ""
        var tableID = 1

        for (j in 0 until y) {
            for(i in 0 until x / 3) {
            tables += "$tableID $tableID $tableID "
            tableID++
        }
            tables = tables.trim() + "\n"
        }
        return tables
    }
}