package eu.rechenwerk.ccc

import eu.rechenwerk.ccc.internals.CCCEngineBuilderWithFolder
import eu.rechenwerk.ccc.internals.CCCEngineBuilderWithPackageName

class CCCEngine {
    infix fun packageName(packageName: String) = CCCEngineBuilderWithPackageName(packageName)

    infix fun folderName(folderName: String) = CCCEngineBuilderWithFolder(folderName)
}