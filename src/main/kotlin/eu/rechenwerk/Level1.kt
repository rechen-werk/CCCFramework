package eu.rechenwerk

import eu.rechenwerk.ccc.*

//@Example(0)
//@Example(1)
//@Example(2)
//@Example(3)
@Level(1)
fun level1(
    N: Int,
    @Many(sizeParamName = "N", type = String::class) fightingStyles: List<String>
): String {
    return fightingStyles.map { fight(it[0], it[1]) }.joinToString("\n")
}

private fun fight(left: Char, right: Char): String {
    when (left) {
        'R' -> return if (right == 'P') "P" else "R"
        'P' -> return if (right == 'S') "S" else "P"
        'S' -> return if (right == 'R') "R" else "S"
        else -> throw IllegalArgumentException("No such fighting styles")
    }
}