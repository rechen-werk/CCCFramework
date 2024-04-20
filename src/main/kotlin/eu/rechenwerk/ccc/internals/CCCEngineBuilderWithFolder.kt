package eu.rechenwerk.ccc.internals

import java.io.File

class CCCEngineBuilderWithFolder internal constructor(private var folder: String) {
    infix fun packageName(packageName: String) = CCCEngine(packageName, File(folder))
}