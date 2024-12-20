package eu.rechenwerk.ccc.internal.services

import eu.rechenwerk.ccc.internal.NoZipException
import java.io.File

fun getZip(location: File, level: Int): File {
    return location
        .listFiles()
        ?.firstOrNull { it.name == "level$level.zip" } ?: throw NoZipException(level)
}