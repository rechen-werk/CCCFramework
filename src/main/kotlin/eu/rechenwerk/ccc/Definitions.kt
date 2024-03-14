package eu.rechenwerk.ccc

import eu.rechenwerk.ccc.internals.Line
import eu.rechenwerk.ccc.internals.annotations.Definition
import eu.rechenwerk.ccc.internals.annotations.Repeat

@Definition(1)
class Definition1(
    val mapSize: Int,
    @Repeat("mapSize") val map: String,
    val N: Int,
    @Repeat("N") val coordinates: String
)

@Definition(2)
class Definition2(
    val mapSize: Int,
    @Repeat("mapSize") val map: String,
    val N: Int,
    @Repeat("N") val coordinatePairs: String,
    @Repeat("N") val coordinatePairs2: String
)

@Definition(3)
class Definition3(
    val mapSize: Int,
    @Repeat("mapSize") val map: String,
    val N: Int,
    @Repeat("N") val coordinates: Line
)

@Definition(4)
class Definition4(
    val mapSize: Int,
    @Repeat("mapSize") val map: String,
    val N: Int,
    @Repeat("N") val coordinatePairs: String,
    @Repeat("N") val coordinatePairs2: String
)