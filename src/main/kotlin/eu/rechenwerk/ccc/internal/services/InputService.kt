package eu.rechenwerk.ccc.internal.services

import java.io.File
import java.util.*
import java.util.zip.ZipFile

fun input(location: File, level: Int) = scanners(location, level, ".in")

fun solution(location: File, level: Int) = scanners(location, level, ".out")

private fun scanners(location: File, level: Int, type: String): Map<Int, Scanner> {
    val zipFile = ZipFile(getZip(location, level))
    return zipFile
        .entries()
        .asSequence()
        .filter { it.name.contains(type) }
        .map {
            Pair(
                it.name
                    .substringAfter("level${level}_")
                    .substringBefore(type)
                    .toIntOrNull() ?: 0,
                Scanner(zipFile.getInputStream(it))
            )
        }.toMap()
}