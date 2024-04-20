package eu.rechenwerk.ccc.internals

import java.io.File

class CCCEngineBuilderWithFolder internal constructor(private var folder: File) {
    infix fun packageName(packageName: String) = CCCEngine(packageName, folder)
}