package eu.rechenwerk

import eu.rechenwerk.ccc.external.Many
import eu.rechenwerk.ccc.internal.times

class LawnMowing(
    val width: Int,
    val height: Int,
    @Many("height", String::class)val lawn: List<String>
) {
    class Point(val row: Int, val col: Int) {
        operator fun component1() = row
        operator fun component2() = col
    }
    private val validate = true

    val tree = getTreePosition()

    val above = tree.row
    val below = height - (tree.row + 1)
    val left  = tree.col
    val right = width - (tree.col + 1)

    private var traceBackAtEnd = false

    fun mow(): Lawn {
        var path = ""

        val a = above % 2 == 1
        val b = below % 2 == 1
        val l = left % 2 == 1
        val r = right % 2 == 1
        val unevenPosition = a && (r || l) || b && (r || l)

        val quadrant = if(unevenPosition) {
            if(a && r) 2 // a && r
            else if(a) 1 // a && l
            else if(l) 4 // b && l
            else       3 // b && r
        } else {
            if(!a && !r) 2      // a && r
            else if(!a && !l) 1 // a && l
            else if(!b && !l) 4 // b && l
            else 3              // b && r
        }

        path += fillQuadrant(unevenPosition, quadrant)
        path += goToWall(unevenPosition, quadrant)
        path += fillRest(unevenPosition, quadrant)

        val solution = Lawn(this.width, this.height, this.lawn, path)

        return solution
    }

    fun mowAdvanced(): Lawn {
        var path = ""

        val solution = Lawn(this.width, this.height, this.lawn, path)

        return solution
    }

    private fun fillQuadrant(uneven: Boolean, quadrant: Int): String {
        var path = ""
        val (walkDirection, backDirection, trajectory) = when(quadrant) {
            1 -> Triple('D', 'A', 'W')
            2 -> Triple('A', 'D', 'W')
            3 -> Triple('A', 'D', 'S')
            4 -> Triple('D', 'A', 'S')
            else -> throw IllegalStateException("Quadrant invalid.")
        }
        val (walkLength, trajectoryLength, backTrajectory) = when(quadrant) {
            1 -> Triple(right, above, 'S')
            2 -> Triple(left, above, 'S')
            3 -> Triple(left, below, 'W')
            4 -> Triple(right, below, 'W')
            else -> throw IllegalStateException("Quadrant invalid.")
        }
        if(uneven) {
            var i = trajectoryLength
            while(i > 0) {
                path += walkLength * walkDirection
                path += trajectory
                path += walkLength * backDirection
                path += trajectory
                i -= 2
            }
            path = path.dropLast(1)
        } else {
            path += trajectory
            path += backDirection
            path += 2 * backTrajectory
            path += (walkLength + 1) * walkDirection
            path += trajectory
            path += (walkLength - 1) * backDirection
            path += trajectory
            path += (walkLength - 1) * walkDirection
            path += trajectory
            path += (walkLength + 1) * backDirection

            var i = trajectoryLength - 2
            while(i > 0) {
                path += trajectory
                path += (walkLength + 1) * walkDirection
                path += trajectory
                path += (walkLength + 1) * backDirection
                i -= 2
            }
        }
        path += backDirection
        return path.drop(1)
    }

    fun goToWall(uneven: Boolean, quadrant: Int): String {
        var path = ""
        val (walkDirection, backDirection, trajectory) = when(quadrant) {
            1 -> Triple('S', 'W', 'A')
            2 -> Triple('S', 'W', 'D')
            3 -> Triple('W', 'S', 'D')
            4 -> Triple('W', 'S', 'A')
            else -> throw IllegalStateException("Quadrant invalid.")
        }
        val (wdOffset, tOffset) = if (uneven) {
            Pair(0, 0)
        } else {
            Pair(1, -1)
        }
        val (walkLength, trajectoryLength) = when(quadrant) {
            1 -> Pair(above + wdOffset, left + tOffset)
            2 -> Pair(above + wdOffset, right + tOffset)
            3 -> Pair(below + wdOffset, right + tOffset)
            4 -> Pair(below + wdOffset, left + tOffset)
            else -> throw IllegalStateException("Quadrant invalid.")
        }

        var i = trajectoryLength - 2
        while(i > 0) {
            path += walkLength * walkDirection
            path += trajectory
            path += walkLength * backDirection
            path += trajectory
            i -= 2
        }
        if(i == 0) traceBackAtEnd = true
        path += walkLength * walkDirection
        path += trajectory
        path = path.dropLast(1)
        if(walkLength != height - 1) {
            path += walkDirection
        }
        return path
    }

    fun fillRest(uneven: Boolean, quadrant: Int): String {
        //println("$quadrant ${if(uneven) " ODD " else " EVEN "} above: $above below: $below left: $left right: $right" )
        var path = ""
        val (walkDirection, backDirection, trajectory) = when(quadrant) {
            1 -> Triple('D', 'A', 'S')
            2 -> Triple('A', 'D', 'S')
            3 -> Triple('A', 'D', 'W')
            4 -> Triple('D', 'A', 'W')
            else -> throw IllegalStateException("Quadrant invalid.")
        }
        val tOffset = if (uneven) 0 else -1
        val wdOffset = if(traceBackAtEnd) -2 else -1

        val (walkLength, trajectoryLength, backTrajectory) = when(quadrant) {
            1, 2 -> Triple(width + wdOffset, below + tOffset, 'W')
            3, 4 -> Triple(width + wdOffset, above + tOffset, 'S')
            else -> throw IllegalStateException("Quadrant invalid.")
        }

        var i = trajectoryLength
        if(i == 1) {
            path += walkLength * walkDirection
            path += trajectory
            i = 0
        }
        while(i >= 2) {
            path += walkLength * walkDirection
            path += trajectory
            path += walkLength * backDirection
            path += trajectory
            i -= 2
        }
        path = path.dropLast(1)
        if(i % 2 == 1) {
            path += trajectory
            path += walkLength * walkDirection
            if(traceBackAtEnd) {
                path += walkDirection
                path += backTrajectory * (height - 1)
            }
        } else {
            if(traceBackAtEnd) {
                path += backDirection
                path += backTrajectory * (height - 1)
            }
        }

        return path
    }

    private fun getTreePosition(): Point {
        for((row, line) in lawn.withIndex()) {
            if(line.contains('X')) {
                return Point(row, line.indexOfFirst { it == 'X' })
            }
        }
        throw IllegalArgumentException("No tree found")
    }
}
