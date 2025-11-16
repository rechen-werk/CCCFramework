package eu.rechenwerk.ccc

//@Example(0)
@Level(2) 
fun level2(
    N: Int,
    @Many(sizeParamName = "N", Sequence::class) sequence: List<Sequence>
): String {
    return sequence.map { "${it.position()} ${it.time()}" }.joinToString("\n")
}

//@Validator(2) 
fun validator2(
    N: Int,
    @Many(sizeParamName = "N", Line::class) sequence: List<Line>
): Boolean {
    return true
}

class Sequence(line: Line) {
    val sequence: List<Int>
    init {
        sequence = line.split(" ").map { it2 -> it2.toInt() }
    }

    fun position(): Int {
        return sequence.filter { it != 0 }.map { if (it > 0) 1 else -1 }.sum()
    }

    fun time(): Int {
        return sequence.map { if(it == 0) 1 else it }.map { if (it > 0) it else -it }.sum()
    }
}