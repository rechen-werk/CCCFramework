package eu.rechenwerk

import eu.rechenwerk.ccc.external.Level
import eu.rechenwerk.ccc.external.Many
fun dirCounter(
    path: String
): String {
     return "${path.count { it == 'W' }} ${path.count { it == 'D' }} ${path.count { it == 'S' }} ${path.count { it == 'A' }}"
}

@Level(1) fun level1(
    n: Int,
    @Many("n", String::class) paths: List<String>
): String {
    return paths
        .map { path -> dirCounter(path) }
        .joinToString("\n")
}