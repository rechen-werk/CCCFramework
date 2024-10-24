package eu.rechenwerk

import eu.rechenwerk.LawnMowing.TreeSide.*
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
        return if(isAtWall()) {
            mowFromWall()
        } else {
            mow()
        }
    }

    private fun isAtWall() =
        tree.row == 0 || tree.col == 0 || tree.row == height - 1 || tree.col == width - 1

    private enum class TreeSide {
        NORTH, EAST, SOUTH, WEST, NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST
    }

    private fun mowFromWall(): Lawn {
        var path = ""
        val treeSide =
            if(tree.row == 0) {
                when (tree.col) {
                    0 -> NORTH_WEST
                    width - 1 -> NORTH_EAST
                    else -> NORTH
                }
            } else if(tree.row == height - 1) {
                when (tree.col) {
                    0 -> SOUTH_WEST
                    width - 1 -> SOUTH_EAST
                    else -> SOUTH
                }
            } else if(tree.col == 0) {
                WEST
            } else {
                EAST
            }

        val (treeLeft, treeRight) = when(treeSide) {
            NORTH -> Pair(left, right)
            EAST -> Pair(above, below)
            SOUTH -> Pair(right, left)
            WEST -> Pair(below, above)
            NORTH_WEST -> Pair(0, right)
            NORTH_EAST -> Pair(0, below)
            SOUTH_WEST -> Pair(0, above)
            SOUTH_EAST -> Pair(0, left)
        }
        val (treeWidth, treeHeight) = when(treeSide) {
            NORTH -> Pair(width, height)
            EAST -> Pair(height, width)
            SOUTH -> Pair(width, height)
            WEST -> Pair(height, width)
            NORTH_WEST -> Pair(width, height)
            NORTH_EAST -> Pair(height, width)
            SOUTH_WEST -> Pair(height, width)
            SOUTH_EAST -> Pair(width, height)
        }
        val (leftChar, rightChar) = when(treeSide) {
            NORTH -> Pair('A', 'D')
            EAST -> Pair('W', 'S')
            SOUTH -> Pair('D', 'A')
            WEST -> Pair('S', 'W')
            NORTH_WEST -> Pair('A', 'D')
            NORTH_EAST -> Pair('W', 'S')
            SOUTH_WEST -> Pair('S', 'W')
            SOUTH_EAST -> Pair('D', 'A')
        }
        val (upChar, downChar) = when(treeSide) {
            NORTH -> Pair('W', 'S')
            EAST -> Pair('D', 'A')
            SOUTH -> Pair('S', 'W')
            WEST -> Pair('A', 'D')
            NORTH_WEST -> Pair('W', 'S')
            NORTH_EAST -> Pair('D', 'A')
            SOUTH_WEST -> Pair('A', 'D')
            SOUTH_EAST -> Pair('S', 'W')
        }
        if (treeLeft == 0) {
            path += (treeWidth - 1) * rightChar
            path += fill(downChar, leftChar, rightChar, treeHeight, treeWidth)
        } else if(treeLeft % 2 == 0) {
            path += treeRight * rightChar
            path += downChar
            path += treeRight * leftChar
            path += fill(leftChar, upChar, downChar, treeLeft + 1, 2)
            path += fill(downChar, rightChar, leftChar, treeHeight - 1, treeWidth)
        } else if(treeRight % 2 == 0) {
            path += treeLeft * leftChar
            path += downChar
            path += treeLeft * rightChar
            path += fill(rightChar, upChar, downChar, treeRight + 1, 2)
            path += fill(downChar, leftChar, rightChar, treeHeight - 1, treeWidth)
        } else {
            path += treeRight * rightChar
            path += downChar
            path += (treeWidth - 2) * leftChar
            path += fill(downChar, rightChar, leftChar, treeHeight - 1, treeWidth -1)
            path += fill(leftChar, upChar, downChar, 2, treeHeight -1)
            path += fill(upChar, rightChar, leftChar, 2, treeLeft)
        }

        //rotate path back
        path = path.drop(1)

        val solution = Lawn(this.width, this.height, this.lawn, path)

        // debug
        println("Lawn:")
        lawn.forEach { println(it) }
        println("Path:")
        println(path)

        return solution
    }

    private fun fill(
        trajectoryChar: Char,
        directionChar: Char,
        backDirectionChar: Char,
        trajectoryLength: Int,
        direcitonWidth: Int
    ): String {
        var path = ""
        for (i in 1 until trajectoryLength) {
            path += trajectoryChar
            path += if(i % 2 == 1) {
                (direcitonWidth - 1) * directionChar
            } else {
                (direcitonWidth - 1) * backDirectionChar
            }
        }
        return path
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

    private fun goToWall(uneven: Boolean, quadrant: Int): String {
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

    private fun fillRest(uneven: Boolean, quadrant: Int): String {
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
