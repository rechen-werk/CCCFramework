package eu.rechenwerk.ccc

import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.math.abs
import kotlin.math.ceil

//@Example(1)
@Level(3) 
fun level3(
    N: Int,
    @Many(sizeParamName = "N", Mission::class) mission: List<Mission>
): String {
    return mission.map { it.build().joinToString(" ") }.joinToString("\n")
}

@Validator(3)
fun validator3(
    N: Int,
    @Many(sizeParamName = "N", Mission::class) mission: List<Mission>
): Boolean {
    return mission.all {
        val mission = it.build()
        val missionString = mission.joinToString(" ")
        val sequence = Sequence(Line(missionString))
        sequence.position() == it.distance &&
                sequence.time() <= it.timeLimit
    }
}


class Mission(line: Line) {
    val distance: Int
    val timeLimit: Int
    var curPace = 0

    init {
        val ints = line.split(" ")
        distance = ints[0].toInt()
        timeLimit = ints[1].toInt()
    }

    fun build(): List<Int> {
        var list = mutableListOf<Int>()
        list.add(0)


        val distanceToTravel = abs(distance)
        val distanceEven = distanceToTravel % 2 == 0
        var accelerationsLeft = ceil(distanceToTravel / 2f).toInt()
        curPace = 5

        while (accelerationsLeft > 0) {
            list.add(curPace)
            accel(1)
            accelerationsLeft--
        }

        val listcopyreversed = list.asReversed()
        if(distanceEven) {
            list.addAll(listcopyreversed)
        } else {
            list.addAll(listcopyreversed.drop(1))
        }

        if (distance < 0) {
            list = list.map {-it}.toList() as MutableList<Int>
        }
        return list
    }


    fun accel(dir: Int) {
        if (curPace == 0 && dir == 1) {
            curPace = 5
        } else if (curPace == 0 && dir == -1) {
            curPace = -5
        } else if (curPace == 1 || curPace == -1) {
            // do nothing
        } else if (curPace != 0 && dir == 1) {
            curPace--
        } else {
            curPace++
        }
    }

    fun decel(dir: Int) {
        if (curPace == 5 || curPace == -5) {
            curPace = 0
        } else if (curPace == 0) {
            // do nothing
        } else if (dir == 1) {
            curPace++
        }  else {
            curPace--
        }
    }
}