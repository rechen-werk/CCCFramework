package eu.rechenwerk.ccc.internals

import java.io.File
import java.nio.file.Path

class CCCEngineBuilderWithPackageName internal constructor(private var packageName: String) {
    infix fun folderName(folderName: String) = CCCEngine(packageName, File(folderName))
    infix fun folderFile(folderFile: File) = CCCEngine(packageName, folderFile)
    infix fun folderPath(folderPath: Path) = CCCEngine(packageName, folderPath.toFile())
}