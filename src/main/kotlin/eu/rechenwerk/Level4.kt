package eu.rechenwerk

import eu.rechenwerk.ccc.external.*

//@Example(2)
@Validated
@Level(4) fun level4(
    n: Int,
    @Many("n", Room4::class) rooms: List<Room4>
): String {
    return rooms.map { it.placeDesks().first }.joinToString("\n\n")
}
@Validator(4) fun validator4 (
    n: Int,
    @Many("n", Room4::class) rooms: List<Room4>
): Boolean {
    return rooms.all { it.placeDesks().second == it.nTables }
}

class Room4(val cols: Int, val rows: Int, val nTables: Int) {
    fun placeDesks(): Pair<String, Int> {
        val table = Array(rows) { BooleanArray(cols) { false } }
        var placedTables = 0

        for(r in 0 until rows step 2) {
            for(c in 0 until cols - 2 step 4) {
                table[r][c] = true
                table[r][c + 1] = true
                table[r][c + 2] = true
                placedTables++
            }
        }
        while(placedTables < nTables) {
            for(r in 0 until rows - 2 step 4) {
                for(c in (cols / 4) * 4 until cols step 2) {
                    table[r][c] = true
                    table[r + 1][c] = true
                    table[r + 2][c] = true
                    placedTables++
                }
            }
        }

        return table.joinToString("\n") { c -> c.joinToString("") { r -> if (r) "X" else "." } } to placedTables
    }
}