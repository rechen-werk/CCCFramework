package eu.rechenwerk.ccc.internal.model

import java.lang.reflect.Method

class LevelCode(
    val solution: Method,
    val validator: Method?
) {
    operator fun component1() = solution
    operator fun component2() = validator
}
