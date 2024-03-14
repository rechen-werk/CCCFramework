package eu.rechenwerk.ccc

import eu.rechenwerk.ccc.Input1
import eu.rechenwerk.ccc.Output1
import eu.rechenwerk.ccc.internals.annotations.Level

@Level(1) fun run(input: Input1): Output1 {
    val coordinates = input.coordinates.map { it.split(",").map { it.toInt() } }
    val letters = coordinates.map { input.map[it[1]][it[0]].toString() }
    return Output1(letters)
}