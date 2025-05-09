package eu.rechenwerk.ccc.internal

import eu.rechenwerk.ccc.Level
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import java.io.File

internal class CCCSimpleEngine(
    location: File,
    level: Int?
): CCCBaseEngine(location) {
    private val level: Int
    init {
        this.level = level ?: getHighestLevel()
    }

    override fun run() {
        run(level)
    }

    private fun getHighestLevel(): Int {
        val highestLevel = packages
            .flatMap { pkg -> Reflections(pkg, Scanners.MethodsAnnotated)
                .getMethodsAnnotatedWith(Level::class.java) }
            .maxOfOrNull { it.getAnnotation(Level::class.java).value }

        if (highestLevel == null) {
            generateLevel(1)
            throw EngineException("No method annotated with @Level(Int). A method for level 1 has been generated for you.")
        }

        return highestLevel
    }

}