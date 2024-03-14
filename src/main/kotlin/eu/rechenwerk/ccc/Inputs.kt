package eu.rechenwerk.ccc

import eu.rechenwerk.ccc.internals.annotations.Input

@Input(1)
class Input1(
    val mapSize: Int,
    val map: List<String>,
    val N: Int,
    val coordinates: List<String>
)

@Input(2)
class Input2(
    val mapSize: Int,
    val map: List<String>,
    val N: Int,
    val coordinatePairs: List<String>,
    val coordinatePairs2: List<String>
)

@Input(3)
class Input3(
    val mapSize: Int,
    val map: List<String>,
    val N: Int,
    val coordinates: List<String>
)

@Input(4)
class Input4(
    val mapSize: Int,
    val map: List<String>,
    val N: Int,
    val coordinatePairs: List<String>,
    val coordinatePairs2: List<String>
)