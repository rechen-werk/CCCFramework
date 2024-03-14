package eu.rechenwerk.ccc

import eu.rechenwerk.ccc.internals.annotations.Output

@Output(1)
class Output1(
    val list: List<String>
) {
    override fun toString(): String {
        return list.joinToString("\n")
    }
}

@Output(2)
class Output2(
    val list: List<IslandType>
) {
    override fun toString(): String {
        return list.joinToString("\n")
    }
}

@Output(3)
class Output3(
    val list: List<RouteType>
) {
    override fun toString(): String {
        return list.joinToString("\n")
    }
}

@Output(4)
class Output4(
    val list: List<List<Coordinate>>
) {
    override fun toString(): String {
        return list.map { it.joinToString(" ") }.joinToString("\n")
    }
}