package eu.rechenwerk.ccc.internal

import eu.rechenwerk.ccc.Example
import eu.rechenwerk.ccc.Level
import eu.rechenwerk.ccc.Line
import eu.rechenwerk.ccc.Validator
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.zip.ZipFile
import kotlin.collections.HashSet

internal abstract class CCCBaseEngine(
    private val location: File
) {
    protected val packages: List<String>

    init {
        packages = packages()
    }

    internal abstract fun run()

    protected fun run(level: Int) {
        val levelCode = methods(level)
        val levelInput = input(location, level)

        val levelOutput = levelInput.run(levelCode)
        if (levelOutput.valid) {
            val levelOutputDirectory = location.resolve("level$level").toPath()
            Files.createDirectories(levelOutputDirectory)
            levelOutput.exampleOutputs.forEach {
                val path = it.getPath(level, levelOutputDirectory)
                Files.deleteIfExists(path)
                Files.writeString(path, it.evaluation, StandardOpenOption.CREATE)
            }
            println("| Output for level $level has been written to $levelOutputDirectory.")
            println("+" + 35 * " -")
        }
    }

    private fun packages(): List<String> {
        val packageNames: MutableSet<String> = HashSet()
        val resources = Thread.currentThread().contextClassLoader.getResources("")
        while (resources.hasMoreElements()) {
            val resource = resources.nextElement()
            val rootDirectory = File(resource.file)
            if (rootDirectory.exists() && rootDirectory.isDirectory) {
                scanDirectory(rootDirectory, "", packageNames)
            }
        }
        return packageNames.filter { it != "META-INF" }.toList()
    }

    private fun scanDirectory(directory: File, currentPackage: String, packageNames: MutableSet<String>) {
        val files = directory.listFiles() ?: return

        files.forEach { file ->
            if (file.isDirectory) {
                val subPackage = if (currentPackage.isEmpty()) file.name else currentPackage + "." + file.name
                packageNames.add(subPackage)
                scanDirectory(file, subPackage, packageNames)
            }
        }
    }

    private fun methods(level: Int): LevelCode {
        val solution = packages
            .flatMap { pkg -> Reflections(pkg, Scanners.MethodsAnnotated)
                .getMethodsAnnotatedWith(Level(level)) }
            .distinct()
            .onlyOrGenerateIfNone{ level }

        if (solution.returnType != CharSequence::class.java && solution.returnType != String::class.java && solution.returnType != Line::class.java) {
            throw NoCharSequenceReturned(level, solution)
        }
        val examples = solution.getAnnotationsByType(Example::class.java).map { it.value }.sorted()

        val validator = packages
            .flatMap { pkg -> Reflections(pkg, Scanners.MethodsAnnotated)
                .getMethodsAnnotatedWith(Validator(level)) }
            .distinct()
            .firstOrNull()

        if (validator != null && validator.returnType != Boolean::class.java) {
            throw NoBooleanReturned(level, validator)
        }

        return LevelCode(examples, solution, validator)
    }

    private fun input(location: File, level: Int): LevelInput {
        val zipFile = ZipFile(location.listFiles()?.firstOrNull { it.name == "level$level.zip" } ?: throw NoZipException(level))
        val entries = zipFile.entries().asSequence()

        val exampleInputs = entries
            .groupBy { it.name.substringAfter("level${level}_").substringBefore(".") }
            .map {
                val example = it.key.toIntOrNull() ?: 0
                val input = it.value.first{ file -> file.name.endsWith("in") }
                val inputScanner = Scanner(zipFile.getInputStream(input))
                val output = it.value.firstOrNull{ file -> file.name.endsWith("out") }
                val outputScanner = if (output != null) Scanner(zipFile.getInputStream(output)) else null

                Problem(example, inputScanner, outputScanner)
        }.toList()

        return LevelInput(level, exampleInputs)
    }
}

