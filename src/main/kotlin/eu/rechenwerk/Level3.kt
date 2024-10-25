package eu.rechenwerk.eu.rechenwerk

import eu.rechenwerk.ccc.external.*

@Validated
@Level(3) fun level3 (
    n: Int,
    @Many("n", Room3::class) rooms: List<Room3>
): String {
    return rooms.map { it.placeDesks() }.joinToString("\n\n")
}

@Validator(3) fun validator3 (
    n: Int,
    @Many("n", Room3::class) rooms: List<Room3>
): Boolean {
    return true
}

class Room3(val cols: Int, val rows: Int, val nTables: Int) {
    fun placeDesks(): String {
        val table = Array(rows) { IntArray(cols) { 0 } }
        val tablesPerRow = cols / 3
        val rest = cols % 3
        var tableID = 1

        for(r in 0 until rows) {
            for(c in 0 until tablesPerRow) {
                table[r][c * 3] = tableID
                table[r][c * 3 + 1] = tableID
                table[r][c * 3 + 2] = tableID
                tableID++
            }
        }
        val start = 3 * tablesPerRow
        println(start)
        while (tableID <= nTables) {
            for(r in 0 until rows - 2 step 3) {
                for(c in start until cols) {
                    table[r][c] = tableID
                    table[r + 1][c] = tableID
                    table[r + 2][c] = tableID
                    tableID++
                }
            }
        }

        return table.map{ it.joinToString(" ") }.joinToString("\n")
    }
    enum class Orient {
        HORIZONTAL,
        VERTICAL
    }

    fun placeDesk(table: Array<IntArray>, id: Int, row: Int, col: Int, orientation: Orient) {
        if (orientation == Orient.HORIZONTAL) {
            table[col][row] = 1
            table[col + 1][row] = 1
            table[col + 2][row] = 1
        } else {
            table[col][row] = 1
            table[col][row + 1] = 1
            table[col][row + 2] = 1
        }
    }
}