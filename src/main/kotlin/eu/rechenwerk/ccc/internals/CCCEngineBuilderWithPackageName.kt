package eu.rechenwerk.ccc.internals

import java.io.File

class CCCEngineBuilderWithPackageName internal constructor(private var packageName: String) {
    infix fun folderName(folderName: String) = CCCEngine(packageName, File(folderName))
}