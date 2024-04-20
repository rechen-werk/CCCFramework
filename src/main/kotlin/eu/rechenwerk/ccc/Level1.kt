package eu.rechenwerk.ccc

import eu.rechenwerk.ccc.internals.annotations.Level
import eu.rechenwerk.ccc.internals.annotations.Many

@Level(1) fun level1(count: Int, @Many("count", String::class) paths: List<String>): String {
    return paths.joinToString("\n") { line ->
        val wCount = line.count { c -> c == 'W' }
        val aCount = line.count { c -> c == 'A' }
        val sCount = line.count { c -> c == 'S' }
        val dCount = line.count { c -> c == 'D' }
        "$wCount $dCount $sCount $aCount"
    }
}
