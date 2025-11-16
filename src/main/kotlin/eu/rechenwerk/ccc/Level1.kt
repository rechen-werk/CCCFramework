package eu.rechenwerk.ccc

//@Example(0)
@Level(1) 
fun level1(
    N: Int,
    @Many(sizeParamName = "N", Line::class) sequence: List<Line>
): String {
    return sequence.map { it.split(" ").map { it2 -> it2.toInt() }.sum() }.joinToString("\n")
}

//@Validator(1) 
fun validator1(
    
): Boolean {
    return true
}
