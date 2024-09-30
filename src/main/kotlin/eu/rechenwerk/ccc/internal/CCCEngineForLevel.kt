package eu.rechenwerk.ccc.internal

import java.io.File

class CCCEngineForLevel internal constructor(folder: File, private val level: Int): CCCEngine(folder) {
    override fun start() {
        start(level)
    }
}