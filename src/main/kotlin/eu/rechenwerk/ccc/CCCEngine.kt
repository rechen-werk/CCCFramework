package eu.rechenwerk.ccc

import eu.rechenwerk.ccc.internals.CCCEngineBuilderWithFolder
import eu.rechenwerk.ccc.internals.CCCEngineBuilderWithPackageName
import java.io.File
import java.nio.file.Path

class CCCEngine {
    infix fun packageName(packageName: String) = CCCEngineBuilderWithPackageName(packageName)

    infix fun folderName(folderName: String) = CCCEngineBuilderWithFolder(File(folderName))
    infix fun folderFile(folderFile: File) = CCCEngineBuilderWithFolder(folderFile)
    infix fun folderPath(folderPath: Path) = CCCEngineBuilderWithFolder(folderPath.toFile())
}