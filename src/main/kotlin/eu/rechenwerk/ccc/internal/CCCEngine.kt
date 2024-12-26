package eu.rechenwerk.ccc.internal

import eu.rechenwerk.ccc.Example
import eu.rechenwerk.ccc.internal.services.*
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption

class CCCEngine internal constructor(
    private val location: File,
    private var level: Int,
    private val catCoder: CatCoder?
) {
    fun start() {
        catCoder?.downloadMaterials(location)

        val (method, validator, lev) = methods(level)
        this.level = lev

        val problems = input(location, level)

        if(method.getAnnotationsByType(Example::class.java).isNotEmpty()) {
            printExamples(method, validator, problems, level)
        } else {
            val results = problems.mapValues { (_, scanner) -> scanner.apply(method, validator) }
            if(validator != null) {
                testExamples(level, results.mapValues { Pair(it.value.first, it.value.second!!) })
            } else {
                testExample(level, results.mapValues { it.value.first })
            }
        }
    }

    private fun getExampleOutput(level: Int): String {
        val outputScanner = solution(location, level)[0]

        if(outputScanner != null) {
            val sb = StringBuilder()
            if (outputScanner.hasNextLine())
                sb.append(outputScanner.nextLine())
            while (outputScanner.hasNextLine())
                sb.append("\n").append(outputScanner.nextLine())

            return sb.toString()
        } else {
            throw EngineException("Could not find a solution for the example.")
        }
    }

    private fun testExample(level: Int, results: Map<Int, String>) {
        val exampleResult = results[0] ?: throw EngineException("Could not find an unique example level $level. Expecting exactly 1 '.in'-file with 'example' in its name.")

        val exampleSolution = getExampleOutput(level)
        val line = 103 * "-"
        if(exampleResult == exampleSolution) {
            println(line)
            println("The Example for level $level has been solved correctly. Writing all files.")
            println(line)
            val thisLevelDirectory = location.resolve("level$level").toPath()
            Files.createDirectories(thisLevelDirectory)
            results
                .forEach{
                    val file = thisLevelDirectory.resolve("level${level}_${it.key}.out".replace("_0.", "_example."))
                    Files.deleteIfExists(file)
                    Files.writeString(file, it.value, StandardOpenOption.CREATE)
                }
        } else {
            println(line)
            println("The Example has not been solved correctly, no files overwritten. Here are the outputs of both versions:")
            println("Example: ")
            println(exampleSolution)
            println(line)
            println("Your solution: ")
            println(exampleResult)
            println(line)
        }
    }

    private fun testExamples(level: Int, results: Map<Int, Pair<String, Boolean>>) {
        val line = 103 * "-"
        println(line)
        println("Testing your solutions according to @Validator($level).")

        val thisLevelDirectory = location.resolve("level$level").toPath()
        Files.createDirectories(thisLevelDirectory)
        results.forEach{
            println(line)
            println("${it.key}: ${if(it.value.second) "VALID" else "INVALID."}")
            val file = thisLevelDirectory.resolve("level${level}_${it.key}.out".replace("_0.", "_example."))
            Files.deleteIfExists(file)
            if(it.value.second) {
                val result = it.value
                Files.writeString(file, result.first, StandardOpenOption.CREATE)
                println("Output has been written to $file.")
            } else {
                println("Annotate the level method with @Example(${it.key}) to get more information about your output to the example.")
            }
        }
        println(line)
    }
}