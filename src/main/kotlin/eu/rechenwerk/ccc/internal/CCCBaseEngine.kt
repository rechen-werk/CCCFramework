package eu.rechenwerk.ccc.internal

import eu.rechenwerk.ccc.Example
import eu.rechenwerk.ccc.Level
import eu.rechenwerk.ccc.Line
import eu.rechenwerk.ccc.Validator
import eu.rechenwerk.ccc.internal.model.ExampleInput
import eu.rechenwerk.ccc.internal.model.ExampleSolution
import eu.rechenwerk.ccc.internal.model.LevelCode
import eu.rechenwerk.ccc.internal.model.LevelInput
import eu.rechenwerk.ccc.internal.services.apply
import eu.rechenwerk.ccc.internal.services.printExamples
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import java.io.File
import java.util.*
import java.util.zip.ZipFile
import kotlin.collections.HashSet

internal abstract class CCCBaseEngine(
    val location: File
) {
    protected val packages: List<String>

    init {
        packages = packages()
    }

    abstract fun run()

    protected fun run(level: Int) {
        val (solution, validator) = methods(level)

        val problems = input(location, level)

        if(solution.getAnnotationsByType(Example::class.java).isNotEmpty()) {
            printExamples(solution, validator, problems, level)
        } else {
            val results = problems.mapValues { (_, scanner) -> scanner.apply(solution, validator) }
            val result = if(validator != null) {
                testExamples(level, results.mapValues { Pair(it.value.first, it.value.second!!) })
            } else {
                testExample(level, results.mapValues { it.value.first })
            }
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
        val method = packages
            .flatMap { pkg -> Reflections(pkg, Scanners.MethodsAnnotated)
                .getMethodsAnnotatedWith(Level(level)) }
            .distinct()
            .onlyOrGenerateIfNone{ level }

        if (method.returnType != CharSequence::class.java && method.returnType != String::class.java && method.returnType != Line::class.java) {
            throw NoCharSequenceReturned(level, method)
        }

        val validator = packages
            .flatMap { pkg -> Reflections(pkg, Scanners.MethodsAnnotated)
                .getMethodsAnnotatedWith(Validator(level)) }
            .distinct()
            .firstOrNull()

        if (validator != null && validator.returnType != Boolean::class.java) {
            throw NoBooleanReturned(level, validator)
        }

        return LevelCode(method, validator)
    }

    private fun input(location: File, level: Int): LevelInput {
        val zipFile = ZipFile(location.listFiles()?.firstOrNull { it.name == "level$level.zip" } ?: throw NoZipException(level))
        val entries = zipFile
            .entries()
            .asSequence()
        val exampleInputs = entries.filter { it.name.endsWith(".in") }.toList()
        val exampleSolutions = entries.filter { it.name.endsWith(".out") }.toList()
        return LevelInput(
            level,
            exampleInputs.map {
                ExampleInput(
                    it.name
                        .substringAfter("level${level}_")
                        .substringBefore(".in")
                        .toIntOrNull() ?: 0,
                    Scanner(zipFile.getInputStream(it))
                )
            },
            exampleSolutions.map {
                ExampleSolution(
                    it.name
                        .substringAfter("level${level}_")
                        .substringBefore(".out")
                        .toIntOrNull() ?: 0,
                    Scanner(zipFile.getInputStream(it))
                )
            }
        )
    }
}