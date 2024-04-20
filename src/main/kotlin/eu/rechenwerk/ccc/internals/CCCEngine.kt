package eu.rechenwerk.ccc.internals

import eu.rechenwerk.ccc.*
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import java.io.File
import java.lang.StringBuilder
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.zip.ZipFile
import kotlin.collections.HashMap
import kotlin.reflect.KClass

class CCCEngine internal constructor(private val packageName: String, private val folder: File) {

    fun run() = highestLevel()?.let { run(it) } ?: System.err.println("No method annotated with @Level(Int).")

    fun run(level: Int) {
        try {
            val method = method(level)
            val validator = if(method.isAnnotationPresent(Validated::class.java)) {
                validator(level) ?: throw ValidatorException("Expected Validator for @Level($level), but found none.")
            } else null
            val problems = scanners(level).filterKeys { name -> name.endsWith(".in") }

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
        } catch (e: EngineException) {
            System.err.println(e.message ?: throw e)
        }
    }

    private fun getZip(level: Int) = folder.listFiles()?.firstOrNull { it.name == "level$level.zip" } ?: throw NoZipException(level)

    private fun scanners(level: Int): Map<String, Scanner> {
        val zipFile = ZipFile(getZip(level))
        return zipFile.entries().asSequence().map {
            it.name to Scanner(zipFile.getInputStream(it))
        }.toMap()
    }

    private fun method(level: Int): Method {
        val method = Reflections(packageName, Scanners.MethodsAnnotated)
            .getMethodsAnnotatedWith(Level(level))
            .only{ "Expected exactly one method with @Level($level)." }

        if (method.returnType != CharSequence::class.java && method.returnType != String::class.java && method.returnType != Line::class.java) {
            throw NoCharSequenceReturned(level, method)
        }
        return method
    }

    private fun validator(level: Int): Method? {
        val method = Reflections(packageName, Scanners.MethodsAnnotated)
            .getMethodsAnnotatedWith(Validator(level))
            .onlyOrNull { "Expected at most one method with @Validator($level)." }

        method?.let {
            if (method.returnType != Boolean::class.java) {
                throw NoBooleanReturned(level, it)
            }
        }
        return method
    }

    private fun highestLevel(): Int? {
        return Reflections(packageName, Scanners.MethodsAnnotated)
            .getMethodsAnnotatedWith(Level::class.java)
            .maxOfOrNull { it.getAnnotation(Level::class.java).value }
    }

    private fun Scanner.apply(method: Method, validator: Method?): Pair<String, Boolean?> {
        val formPars = method.parameters
        val actPars = HashMap<String, Any>()
        actPars.fill(this, formPars)
        if(validator != null) {
            val ex = ValidatorException("@Validator ${validator.name} did not have the same parameters as @Level ${method.name}.")
            val validatorFormPars = validator.parameters
            if(formPars.size != validatorFormPars.size) throw ex
            formPars.forEachIndexed { index, parameter -> if(parameter.type != validatorFormPars[index].type) throw ex }
        }
        // I am not using actPars.values, because maps do not have to preserve ordering
        return Pair(
            method.invoke(null, *formPars.map { actPars[it.name] }.toTypedArray()).toString(),
            validator?.invoke(null, *formPars.map { actPars[it.name] }.toTypedArray()) as Boolean?
        )
    }

    private fun HashMap<String, Any>.fill(scanner: Scanner, parameters: Array<Parameter>) {
        parameters.forEach { param ->
            this[param.name] = when (param.type.kotlin) {
                List::class -> scanner.scanMany(param, this)
                else -> scanner.scan(param.type.kotlin)
            }
        }
    }

    private fun Scanner.scan(type: KClass<*>): Any {
        val next = when(type) {
            Byte::class -> nextByte()
            Short::class -> nextShort()
            Int::class -> nextInt()
            Long::class -> nextLong()
            Float::class -> nextFloat()
            Double::class -> nextDouble()
            Char::class -> next()[0]
            Line::class -> Line(nextLine().ifBlank { nextLine() })
            String::class -> next()
            else -> {
                val constructor = type.java.constructors[0]
                val formPars = constructor?.parameters ?: arrayOf()
                val actPars = HashMap<String, Any>()
                actPars.fill(this, formPars)
                // I am not using actPars.values, because maps do not have to preserve ordering
                constructor.newInstance(*formPars.map { actPars[it.name] }.toTypedArray())
            }
        }
        return next
    }

    private fun Scanner.scanMany(parameter: Parameter, actPars: Map<String, Any>): Any {
        val anno = parameter.getAnnotation(Many::class.java) ?: throw NoManyAnnotationException(parameter)
        val nTimes = actPars[anno.sizeParamName] as Int
        val list: MutableList<Any> = mutableListOf()

        repeat(nTimes) { list += scan(anno.type)}

        return list
    }

    private fun getExampleOutput(level: Int): String {
        val scanners = scanners(level)
        val outputScanner = scanners
            .filter { it.key.endsWith(".out") }
            .map { it.value }
            .first()

        val sb = StringBuilder()
        if (outputScanner.hasNextLine())
            sb.append(outputScanner.nextLine())
        while (outputScanner.hasNextLine())
            sb.append("\n").append(outputScanner.nextLine())

        return sb.toString()
    }

    private fun printExamples(method: Method, validator: Method?, problems: Map<String, Scanner>, level: Int) {
        val examples = method.getAnnotationsByType(Example::class.java).map { it.value }.sorted()
        examples.forEach { example ->
            val scanner = problems
                .filterKeys { filename -> filename == "level${level}_${if (example == 0) "example" else example}.in" }
                .map { it.value }
                .only { "Invalid value for @Example($example). Files for this level are \"${problems.map { it.key }.joinToString("\", \"")}\". Note: level${level}_example.in is default or value 0." }
            val (result, valid) = scanner.apply(method, validator)
            println("Level $level-$example${valid?.let { if(it) " (VALID according to @Validator)" else " (INVALID according to @Validator)" } ?: ""}:")
            println(result)
        }
    }

    private fun testExample(level: Int, results: Map<String, String>) {
        val exampleResult = results
            .filterKeys { it.contains("example") }
            .map { it.value }
            .only{ "Could not find an unique example level $level. Expecting exactly 1 '.in'-file with 'example' in its name." }


        val exampleSolution = getExampleOutput(level)
        val line = 103 * "-"
        if(exampleResult == exampleSolution) {
            println(line)
            println("The Example for level $level has been solved correctly. Writing all files.")
            println(line)
            val thisLevelDirectory = folder.resolve("level$level").toPath()
            Files.createDirectories(thisLevelDirectory)
            results
                .filterKeys { it.contains(".in")  }
                .forEach{
                    val file = thisLevelDirectory.resolve(it.key.replace("in", "out"))
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

    private fun testExamples(level: Int, results: Map<String, Pair<String, Boolean>>) {
        val line = 103 * "-"
        println(line)
        println("Testing your solutions according to @Validator($level).")

        val thisLevelDirectory = folder.resolve("level$level").toPath()
        Files.createDirectories(thisLevelDirectory)
        results.forEach{
            println(line)
            println("${it.key}: ${if(it.value.second) "VALID" else "INVALID."}")
            val file = thisLevelDirectory.resolve(it.key.replace("in", "out"))
            Files.deleteIfExists(file)
            if(it.value.second) {
                val result = it.value
                Files.writeString(file, result.first, StandardOpenOption.CREATE)
                println("Output has been written to $file.")
            } else {
                println("Annotate the level method with @Example(${it.key.replace("example", "0").split("_")[1].split(".")[0]}) to get more information about your output to the example.")
            }
        }
        println(line)
    }
}