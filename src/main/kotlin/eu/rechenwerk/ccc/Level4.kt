package eu.rechenwerk.ccc

import eu.rechenwerk.ccc.internals.annotations.Level
import eu.rechenwerk.ccc.internals.annotations.Many

@Level(4) fun level4(count: Int, @Many("count", Lawn::class) lawns: List<Lawn>): String {
    return lawns.joinToString("\n") { lawn -> lawn.mow(State()).path }
}

class State(var path: String = "") {
    private operator fun String.times(other: Int) = this.repeat(other)
    fun up(length: Int) {
        path += "W" * length
    }

    fun down(length: Int) {
        path += "S" * length
    }

    fun left(length: Int) {
        path += "A" * length
    }

    fun right(length: Int) {
        path += "D" * length
    }
}

data class Lawn(val width: Int,
                     val height: Int,
                     @Many("height", String::class) val lawn: List<String>,

) {
    fun mow(state: State): LawnMower {
        var treeX : Int = -1
        var treeY : Int = -1

        //get tree
        for((rowIndex, row) in lawn.withIndex()) {
            for((colIndex, tile) in row.withIndex()) {
                if(tile == 'X') {
                    treeX = colIndex
                    treeY = rowIndex
                }
            }
        }
        if(treeX == -1) throw IllegalStateException("Tree not found")
        //start above tree
        //go to top wall
        state.up(treeY -1)
        //go to left wall
        state.left(treeX) // pos is at 0,0
        //go to bottom wall
        state.down(height-1)
        //to to right wall
        state.right(width-1)
        //go to top wall
        state.up(height-1)
        //go until tree
        state.left(width-treeX-2)
        //go down in snake lines until bottom
        var h = 0
        while (h <= height-3) {
            state.down(1)
            if(width - treeX - 3 > 0) {
                if (h % 2 == 0) {
                    state.right(width - treeX - 3)
                } else {
                    state.left(width - treeX - 3)
                }
            }
            h++
        }
        //go left as far as possible
        //go snake lines up
            //if uneven height vertical snakes

        return LawnMower(width, height, lawn, state.path)
    }

    private fun startPoint(treeX: Int, treeY: Int): Pair<Int, Int> {
        return Pair(treeX, treeY-1)
    }

    private fun leftCloser(treeX: Int): Boolean {
        return treeX - 0 < width - treeX -1
    }

    private fun topCloser(treeY: Int): Boolean {
        return treeY - 0 < height - treeY -1
    }

    private fun String.rotatePath(times: Int): String {
        if(times == 0) return this
        val new = this
            .replace("W", "U")
            .replace("A", "L")
            .replace("S", "B")
            .replace("D", "R")
            .replace("U", "D")
            .replace("L", "W")
            .replace("B", "A")
            .replace("R", "S")
        return new.rotatePath(times-1)
    }
}