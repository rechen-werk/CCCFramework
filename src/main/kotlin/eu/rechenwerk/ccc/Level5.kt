package eu.rechenwerk.ccc

import kotlin.math.abs

//@Example(0)
@Level(5) 
fun level5(
    N: Int,
    @Many(sizeParamName = "N", AsteroidMission::class) mission: List<AsteroidMission>
): String {
    return mission.joinToString("\n\n") {
        val (xpath, ypath) = it.build()
        val x = xpath.joinToString(" ")
        val y = ypath.joinToString(" ")
        "$x\n$y"
    }
}

//@Validator(5)
fun validator5(
    
): Boolean {
    return true
}

class AsteroidMission(line1: Line, line2: Line) {
    val goal: Point
    val timeLimit: Int
    val asteroid: Point

    var curPaceX = 0
    var curPaceY = 0

    init {
        val stationValues = line1.split(" ")
        val goalCords = stationValues[0].split(",")
        val asteroidCoords = line2.split(",")
        timeLimit = stationValues[1].toInt()
        goal = Point(goalCords[0].toInt(), goalCords[1].toInt())
        asteroid = Point(asteroidCoords[0].toInt(), asteroidCoords[1].toInt())
    }

    fun build(): Pair<List<Int>, List<Int>> {
        val (simplifiegGoal, simplifiedAsteroid) = rotate()



        val coolLine = if (simplifiegGoal.x == 0 && simplifiedAsteroid.x == 0) {
            val target1 = Point(simplifiedAsteroid.x - 3, simplifiedAsteroid.y + 3)
            val target2 = Point(simplifiedAsteroid.x + 3, simplifiedAsteroid.y + 3)

            val straightLine1 = Mission2D(Line("${target1.x},${target1.y} $timeLimit")).build()
            val straightLine2 = Mission2D(Line("${target2.x - target1.x},${target2.y - target1.y} $timeLimit")).build()
            val straightLine3 = Mission2D(Line("${simplifiegGoal.x - target2.x},${simplifiegGoal.y - target2.y} $timeLimit")).build()

            Pair(straightLine1.first + straightLine2.first + straightLine3.first, straightLine1.second + straightLine2.second + straightLine3.second)
        } else if (simplifiegGoal.y == 0 && simplifiedAsteroid.y == 0) {
            val target1 = Point(simplifiedAsteroid.x + 3, simplifiedAsteroid.y - 3)
            val target2 = Point(simplifiedAsteroid.x + 3, simplifiedAsteroid.y + 3)

            val straightLine1 = Mission2D(Line("${target1.x},${target1.y} $timeLimit")).build()
            val straightLine2 = Mission2D(Line("${target2.x - target1.x},${target2.y - target1.y} $timeLimit")).build()
            val straightLine3 = Mission2D(Line("${simplifiegGoal.x - target2.x},${simplifiegGoal.y - target2.y} $timeLimit")).build()

            Pair(straightLine1.first + straightLine2.first + straightLine3.first, straightLine1.second + straightLine2.second + straightLine3.second)

        } else {
            val belowSlope = simplifiedAsteroid.y < (simplifiegGoal.y / simplifiegGoal.x) * simplifiedAsteroid.x
            val target = if (belowSlope) {
                Point(simplifiedAsteroid.x - 3, simplifiedAsteroid.y + 3)
            } else {
                Point(simplifiedAsteroid.x + 3, simplifiedAsteroid.y - 3)
            }

            val straightLine1 = Mission2D(Line("${target.x},${target.y} $timeLimit")).build()
            val straightLine2 = Mission2D(Line("${simplifiegGoal.x - target.x},${simplifiegGoal.y - target.y} $timeLimit")).build()

             Pair(straightLine1.first + straightLine2.first, straightLine1.second + straightLine2.second)
        }

        return rotate(coolLine)
    }

    fun rotate(): Pair<Point, Point> {
        if (goal.x > 0 && goal.y > 0) {
            return Pair(Point(goal.x, goal.y), Point(asteroid.x, asteroid.y))
        }
        if (goal.x <= 0 && goal.y > 0) {
            return Pair(Point(goal.y, -goal.x), Point(asteroid.y, -asteroid.x))
        }
        if (goal.x <= 0 && goal.y <= 0) {
            return Pair(Point(-goal.x, -goal.y), Point(-asteroid.x, -asteroid.y))
        }
        return Pair(Point(-goal.y, goal.x), Point(-asteroid.y, asteroid.x))
    }

    fun rotate(coolLine: Pair<List<Int>, List<Int>>): Pair<List<Int>, List<Int>> {
        if (goal.x > 0 && goal.y > 0) {
            return coolLine
        }
        if (goal.x <= 0 && goal.y > 0) {
            return Pair(coolLine.second.map { it }.toList(), coolLine.first.map { -it }.toList())
        }
        if (goal.x <= 0 && goal.y <= 0) {
            return Pair(coolLine.first.map { -it }.toList(), coolLine.second.map { -it }.toList())
        }
        return Pair(coolLine.second.map { -it }.toList(), coolLine.first.map { it }.toList())
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

    fun decelX(dirX: Int) {
        if (curPaceX == 5 || curPaceX == -5) {
            curPaceX = 0
        } else if (curPaceX == 0) {
            // do nothing
        } else if (dirX == 1) {
            curPaceX++
        }  else {
            curPaceX--
        }
    }

    fun decelY(dirY: Int) {
        if (curPaceY == 5 || curPaceY == -5) {
            curPaceY = 0
        } else if (curPaceY == 0) {
            // do nothing
        } else if (dirY == 1) {
            curPaceY++
        }  else {
            curPaceY--
        }
    }

    fun isColliding(shipX: Int, shipY: Int, asteroidX: Int, asteroidY: Int): Boolean {
        return abs(shipX - asteroidX) <= 2 || abs(shipY - asteroidY) <= 2
    }
}

data class Point(val x: Int, val y: Int)