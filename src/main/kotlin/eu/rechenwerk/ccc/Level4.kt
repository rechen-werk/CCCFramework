package eu.rechenwerk.ccc

import kotlin.collections.joinToString
import kotlin.math.abs

//@Example(1)
@Level(4)
fun level4(
    N: Int,
    @Many(sizeParamName = "N", Mission2D::class) mission: List<Mission2D>
): String {
    return mission.joinToString("\n\n") {
        val (xpath, ypath) = it.build()
        val x = xpath.joinToString(" ")
        val y = ypath.joinToString(" ")
        "$x\n$y"
    }
}

//@Validator(4)
fun validator4(
    N: Int,
    @Many(sizeParamName = "N", Mission2D::class) mission: List<Mission2D>
): Boolean {
    return false
}


class Mission2D(line: Line) {
    val x: Int
    val y: Int
    val timeLimit: Int


    var curPaceX = 0
    var curPaceY = 0

    init {
        val ints = line.split(" ")
        val coords = ints[0].split(",")
        x = coords[0].toInt()
        y = coords[1].toInt()
        timeLimit = ints[1].toInt()
    }

    fun build(): Pair<List<Int>, List<Int>> {
        val missionX = Mission(Line("${x} ${timeLimit}"))
        val missionY = Mission(Line("${y} ${timeLimit}"))

        val xpaths = missionX.build()
        val ypaths = missionY.build()

        return Pair(xpaths, ypaths)
    }


    fun accelX(dirX: Int) {
        if (curPaceX == 0 && dirX == 1) {
            curPaceX = 5
        } else if (curPaceX == 0 && dirX == -1) {
            curPaceX = -5
        } else if (curPaceX == 1 || curPaceX == -1) {
            // do nothing
        } else if (curPaceX != 0 && dirX == 1) {
            curPaceX--
        } else {
            curPaceX++
        }
    }

    fun accelY(dirY: Int) {
        if (curPaceY == 0 && dirY == 1) {
            curPaceY = 5
        } else if (curPaceY == 0 && dirY == -1) {
            curPaceY = -5
        } else if (curPaceY == 1 || curPaceY == -1) {
            // do nothing
        } else if (curPaceY != 0 && dirY == 1) {
            curPaceY--
        } else {
            curPaceY++
        }
    }
}