package eu.rechenwerk

import eu.rechenwerk.ccc.external.*

//@Example(5)
//@Validated
@Level(5)
fun level5(
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
    private val tables = Array(rows) { BooleanArray(cols) { false } }
    private var placedTables = 0
    fun placeDesks(): Pair<String, Int> {
        val heightOdd = rows % 2 == 1
        val widthOdd = cols % 2 == 1

        val heightTrodd = rows % 3 == 2
        val widthTrodd = cols % 3 == 2

        if(heightOdd && widthTrodd) {
            fillTables(0, 0, rows, cols, Orient.HORIZONTAL)
        } else if (heightTrodd && widthOdd) {
            fillTables(0, 0, rows, cols, Orient.VERTICAL)
        } else {
            if(heightTrodd) {
                var idx = 0
                while((cols - idx) % 3 != 2) {
                    fillTables(0, idx, rows, idx + 1, Orient.VERTICAL)
                    idx += 2
                }
                fillTables(0, idx, rows, cols, Orient.HORIZONTAL)
            } else if(widthTrodd) {
                var idx = 0
                while((rows - idx) % 3 != 2) {
                    fillTables(idx, 0, idx + 1, cols, Orient.HORIZONTAL)
                    idx += 2
                }
                fillTables(idx, 0, rows, cols, Orient.VERTICAL)
            } else if(widthOdd) {
                var idx = 0
                while((rows - idx) % 2 != 1) {
                    fillTables(idx, 0, idx + 2, cols, Orient.VERTICAL)
                    idx += 3
                }
                fillTables(idx, 0, rows, cols, Orient.HORIZONTAL)
            } else if(heightOdd) {
                var idx = 0
                while((cols - idx) % 2 != 1) {
                    fillTables(0, idx, rows, idx + 2, Orient.HORIZONTAL)
                    idx += 3
                }
                fillTables(0, idx, rows, cols, Orient.VERTICAL)
            } else {
                if(rows > 10 && cols > 10) {
                    var layer = 0

                    while(cols > 2 * layer + 4 && rows > 2 * layer + 4) {
                        fillTables(layer, layer, rows - (2 + layer), (2 + layer), Orient.HORIZONTAL)
                        fillTables(rows - (2 + layer), layer, rows - layer, cols - (2 + layer), Orient.VERTICAL)
                        fillTables( 1 + (2 + layer), cols - (2 + layer), rows - layer, cols - layer, Orient.HORIZONTAL)
                        fillTables(layer, 1 + (2 + layer), (2 + layer), cols - layer, Orient.VERTICAL)
                        layer += 3
                    }
                    if(placedTables < nTables) {
                        println("rows: $rows, cols: $cols tables: $placedTables wanted: $nTables")
                        println(tables.joinToString("\n") { c -> c.joinToString("") { r -> if (r) "X" else "." } })
                    }
                } else {
                    if(rows <= 10) {
                        fillTables(0,0, rows, 3, Orient.HORIZONTAL)
                        fillTables(0,3, rows, 5, Orient.VERTICAL)
                    } else {

                    }
                }


            }
        }

        return tables.joinToString("\n") { c -> c.joinToString("") { r -> if (r) "X" else "." } } to placedTables
    }

    private fun placeDesk(row: Int, col: Int, orientation: Orient) {
        when(orientation) {
            Orient.HORIZONTAL -> {
                tables[row][col] = true
                tables[row][col + 1] = true
            }
            Orient.VERTICAL -> {
                tables[row][col] = true
                tables[row + 1][col] = true
            }
        }
        placedTables++
    }

    private fun fillTables(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int, orientation: Orient) {
        val (stepRow, stepCol) = when(orientation){
            Orient.HORIZONTAL -> Pair(2, 3)
            Orient.VERTICAL -> Pair(3, 2)
        }
        val (rowSecurityOffset, colSecurityOffset) = when(orientation){
            Orient.HORIZONTAL -> Pair(0, 1)
            Orient.VERTICAL -> Pair(1, 0)
        }
        for(r in fromRow until toRow - rowSecurityOffset step stepRow) {
            for(c in fromCol until toCol - colSecurityOffset step stepCol) {
                placeDesk(r, c, orientation)
            }
        }
    }

    enum class Orient {
        HORIZONTAL,
        VERTICAL
    }
}