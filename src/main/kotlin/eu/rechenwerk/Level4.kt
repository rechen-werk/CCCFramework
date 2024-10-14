package eu.rechenwerk
//
//import eu.rechenwerk.ccc.external.*
//import eu.rechenwerk.ccc.external.Level
//import eu.rechenwerk.ccc.external.Many
//import eu.rechenwerk.ccc.internal.times
//import java.lang.IllegalStateException
//import kotlin.math.abs
//
//@Example
//@Validated
//@Level(4) fun level4(
//    n: Int,
//    @Many("n", LawnMowing::class) lawns: List<LawnMowing>
//): String {
//    return lawns.map {
//        lawn -> lawn.mow().path
//    }.joinToString("\n")
//}
//
//
//@Validator(4) fun validator4(
//    n: Int,
//    @Many("n", LawnMowing::class) lawns: List<LawnMowing>
//): Boolean {
//    return lawns
//        .map { lawn -> lawn.mow() }
//        .all { lawn -> lawn.validate() }
//}
//class Point(val row: Int, val col: Int) {
//    operator fun component1() = row
//    operator fun component2() = col
//}
//
//class LawnMowing(
//    val width: Int,
//    val height: Int,
//    @Many("height", String::class)val lawn: List<String>
//) {
//    val treeChar = 'X'
//    val validate = true
//
//    val treePos = getTreePosition()
//
//    var row = treePos.row
//    var col = treePos.col
//
//    var above = Wall(row, Direction.UP)
//    var below = Wall(height - (row + 1), Direction.DOWN)
//    var left = Wall(col, Direction.LEFT)
//    var right = Wall(width - (col + 1), Direction.RIGHT)
//
//    var imaginaryWallRow = treePos.row
//    var imaginaryWallCol = treePos.col
//
//    var path = ""
//
//    val myLawn = lawn.map{row ->
//        row.map{ char -> when(char) {
//            '.' -> Cell.UNVISITED
//            'X' -> Cell.VISITED
//            else -> {throw IllegalArgumentException("Unexpected field in lawn.")}
//        } }.toMutableList()
//    }.toMutableList()
//}
//
//fun LawnMowing.mow(): Lawn {
//    goToClosestCorner()
//    walk()
//
//    val solution = Lawn(this.width, this.height, this.lawn, path)
//
//    if(validate && !solution.validate()) {
//
//        throw Exception("Solution is not always correct, debug to get more info for the case.\n" +
//                "Lawn:\n" +
//                "${solution.lawn.joinToString("\n") } }}\n" +
//                "myLawn:\n" +
//                "${myLawn.joinToString("\n") } }}\n" +
//                "Path:\n" +
//                solution.path
//        )
//    }
//
//    return solution
//}
//
//private fun LawnMowing.getTreePosition(): Point {
//    for((row, line) in lawn.withIndex()) {
//        if(line.contains(treeChar)) {
//            return Point(row, line.indexOfFirst { it == treeChar })
//        }
//    }
//    throw IllegalArgumentException("No tree found")
//}
//
//private fun LawnMowing.goToClosestCorner() {
//    val closestWalls = listOf(above, below, left, right).sortedBy { it.distance }
//    val first = closestWalls[0]
//    val second = closestWalls[1]
//
//    move(first.distance, first.direction)
//    move(second.distance, second.direction)
//}
//
//private fun LawnMowing.move(times: Int, direction: Direction) {
//    path += direction.action * times
//    when(direction) {
//        Direction.UP -> {
//            for(i in 1 .. times) {
//                myLawn[row-i][col] = Cell.VISITED
//            }
//            row -= times
//        }
//        Direction.LEFT -> {
//            for(i in 1 .. times) {
//                myLawn[row][col-i] = Cell.VISITED
//            }
//            col -= times
//        }
//        Direction.DOWN -> {
//            for(i in 1 .. times) {
//                myLawn[row+i][col] = Cell.VISITED
//            }
//            row += times
//        }
//        Direction.RIGHT -> {
//            for(i in 1 .. times) {
//                myLawn[row][col+i] = Cell.VISITED
//            }
//            col += times
//        }
//    }
//}
//
//private fun LawnMowing.walk() {
//    var walkDirection = !Direction.from(path.last())
//    var trajectory = !Direction.from(path.first())
//    var snakeLength = 0
//
//
//    val steps = when(walkDirection) {
//        Direction.UP, Direction.DOWN-> abs(imaginaryWallRow - row) - 1
//        Direction.LEFT, Direction.RIGHT-> abs(imaginaryWallCol - col) - 1
//    }
//    while(!atObstacle(trajectory) && !obstacleIs2Away(trajectory)) {
//        move(1, trajectory)
//        move(steps, walkDirection)
//        walkDirection = !walkDirection
//    }
//    // if at obstacle:
//    // if obstacle 2 away:
//    println(trajectory)
//}
//
//fun LawnMowing.atObstacle(trajecory: Direction): Boolean {
//    return when(trajecory) {
//        Direction.UP -> {
//            val check = row - 1
//            check == -1 || myLawn[check][col] == Cell.VISITED
//        }
//        Direction.LEFT -> {
//            val check = col - 1
//            check == -1 || myLawn[row][check] == Cell.VISITED
//        }
//        Direction.DOWN -> {
//            val check = row + 1
//            check == width || myLawn[check][col] == Cell.VISITED
//        }
//        Direction.RIGHT -> {
//            val check = col + 1
//            check == width || myLawn[row][check] == Cell.VISITED
//        }
//    }
//}
//
//fun LawnMowing.obstacleIs2Away(trajectory: Direction): Boolean {
//    return when(trajectory) {
//        Direction.UP -> {
//            val check = row - 2
//            if(check != -1)
//                myLawn[check][col] == Cell.VISITED
//            else false
//        }
//        Direction.LEFT -> {
//            val check = col - 2
//            if(check != -1)
//                myLawn[row][check] == Cell.VISITED
//            else false
//        }
//        Direction.DOWN -> {
//            val check = row + 2
//            if(check != -1)
//                myLawn[check][col] == Cell.VISITED
//            else false
//
//        }
//        Direction.RIGHT -> {
//            val check = col + 2
//            if(check != -1)
//                myLawn[row][check] == Cell.VISITED
//            else false
//        }
//    }
//}
//
//
//enum class Direction(val action: Char) {
//    UP('W'),
//    LEFT('A'),
//    DOWN('S'),
//    RIGHT('D');
//
//    companion object {
//        fun from(action: Char): Direction {
//            return when (action) {
//                'W' -> UP
//                'A' -> LEFT
//                'S' -> DOWN
//                'D' -> RIGHT
//                else -> throw IllegalStateException("We wrote trash in the path.")
//            }
//        }
//    }
//
//    operator fun not(): Direction {
//        return when(this) {
//            UP -> DOWN
//            LEFT -> RIGHT
//            DOWN -> UP
//            RIGHT -> LEFT
//        }
//    }
//}
//
//class Wall(val distance: Int, val direction: Direction)//