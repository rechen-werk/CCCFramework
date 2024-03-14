package eu.rechenwerk.ccc

data class Coordinate(val coordinates: String) {
    val x: Int
    val y: Int
    init {
        val coords = coordinates.split(",")
        x = coords[0].toInt()
        y = coords[1].toInt()
    }

    constructor(x: Int, y: Int) : this("$x,$y")

    override fun toString() = "$x,$y"
}