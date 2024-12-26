package eu.rechenwerk.ccc.internal.services

import eu.rechenwerk.ccc.Example
import eu.rechenwerk.ccc.internal.only
import java.lang.reflect.Method
import java.util.*

fun printExamples(method: Method, validator: Method?, problems: Map<Int, Scanner>, level: Int) {
    val examples = method.getAnnotationsByType(Example::class.java).map { it.value }.sorted()
    examples.forEach { example ->
        val scanner = problems
            .filterKeys { problem -> problem == example }
            .map { it.value }
            .only { "Invalid value for @Example($example). Files for this level are \"${problems.map { it.key }.joinToString("\", \"")}\". Note: level${level}_example.in is default or value 0." }
        val (result, valid) = scanner.apply(method, validator)
        println("Level $level-$example${valid?.let { if(it) " (VALID according to @Validator)" else " (INVALID according to @Validator)" } ?: ""}:")
        println(result)
    }
}