package eu.rechenwerk

import eu.rechenwerk.ccc.external.*

//@Example
//@Validated
@Level(5) fun level5(
    n: Int,
    @Many("n", Room5::class) rooms: List<Room5>
): String {
    return rooms.map { it.placeDesks().first }.joinToString("\n\n")
}
@Validator(5) fun validator5 (
    n: Int,
    @Many("n", Room5::class) rooms: List<Room5>
): Boolean {
    return rooms.all { it.placeDesks().second == it.nTables }
}

class Room5(val cols: Int, val rows: Int, val nTables: Int) {
    fun placeDesks(): Pair<String, Int> {
        val table = Array(rows) { BooleanArray(cols) { false } }
        var placedTables = 0

        println(cols % 2 == 1 || rows % 2 == 1)

        //for(r in 0 until rows step 2) {
        //    for(c in 0 until cols - 1 step 3) {
        //        table[r][c] = true
        //        table[r][c + 1] = true
        //        placedTables++
        //    }
        //}
        //while(placedTables < nTables) {
        //    for(r in 0 until rows - 1 step 3) {
        //        for(c in (cols / 3) * 3 until cols step 2) {
        //            table[r][c] = true
        //            table[r + 1][c] = true
        //            placedTables++
        //        }
        //    }
        //}

        return table.joinToString("\n") { c -> c.joinToString("") { r -> if (r) "X" else "." } } to placedTables
    }
}