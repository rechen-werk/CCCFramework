package eu.rechenwerk.ccc.external

import eu.rechenwerk.ccc.internal.CCCEngine
import java.io.File
import java.nio.file.Path

class CCC {
    companion object {
        val ccc = CCC()
    }

    infix fun input(folderName: String) = CCCEngine(File(folderName))
    infix fun input(folderFile: File) = CCCEngine(folderFile)
    infix fun input(folderPath: Path) = CCCEngine(folderPath.toFile())
}