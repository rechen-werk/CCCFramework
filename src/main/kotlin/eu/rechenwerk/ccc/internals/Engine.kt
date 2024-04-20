package eu.rechenwerk.ccc.internals

import eu.rechenwerk.ccc.internals.annotations.Example
import eu.rechenwerk.ccc.internals.annotations.Level
import eu.rechenwerk.ccc.internals.annotations.Many
import eu.rechenwerk.ccc.internals.exceptions.EngineException
import eu.rechenwerk.ccc.internals.exceptions.WrongReturnValueException
import eu.rechenwerk.ccc.internals.exceptions.NoZipException
import eu.rechenwerk.ccc.internals.exceptions.NoManyAnnotationException
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

private val levelDirectory = File(System.getProperty("user.home")).resolve("ccc")
private const val pkg = "eu.rechenwerk.ccc"

fun run() = highestLevel()?.let { run(it) } ?: System.err.println("No method annotated with @Level(Int).")

fun run(level: Int) {
    try {
        val method = method(level)
        val problems = scanners(level).filterKeys { name -> name.endsWith(".in") }

        if(method.getAnnotationsByType(Example::class.java).isNotEmpty()) {
            val examples = method.getAnnotationsByType(Example::class.java).map { it.value }.sorted()
            examples.forEach { example ->
                val scanner = problems
                    .filterKeys { filename -> filename == "level${level}_${if (example == 0) "example" else example}.in" }
                    .map { it.value }
                    .only { "Invalid value for @Example($example). Files for this level are \"${problems.map { it.key }.joinToString("\", \"")}\". Note: level${level}_example.in is default or value 0." }
                println("Example $example:")
                println(scanner.apply(method))
            }
        } else {
            val results = problems.mapValues { (_, scanner) -> scanner.apply(method) }
            testExample(level, results)
        }
    } catch (e: EngineException) {
        System.err.println(e.message ?: throw e)
    }
}

private fun getZip(level: Int) = levelDirectory.listFiles()?.firstOrNull { it.name == "level$level.zip" } ?: throw NoZipException(level)

private fun scanners(level: Int): Map<String, Scanner> {
    val zipFile = ZipFile(getZip(level))
    return zipFile.entries().asSequence().map {
        it.name to Scanner(zipFile.getInputStream(it))
    }.toMap()
}

private fun method(level: Int): Method {
    val method = Reflections(pkg, Scanners.MethodsAnnotated)
        .getMethodsAnnotatedWith(Level(level))
        .only{ "Expected exactly one method with @Level($level)." }

    if (method.returnType != CharSequence::class.java && method.returnType != String::class.java && method.returnType != Line::class.java) {
        throw WrongReturnValueException(level, method)
    }
    return method
}

private fun highestLevel(): Int? {
    return Reflections(pkg, Scanners.MethodsAnnotated)
        .getMethodsAnnotatedWith(Level::class.java)
        .maxOfOrNull { it.getAnnotation(Level::class.java).value }
}

private fun Scanner.apply(method: Method): String {
    val formPars = method.parameters
    val actPars = HashMap<String, Any>()
    actPars.fill(this, formPars)
    // I am not using actPars.values, because maps do not have to preserve ordering
    return method.invoke(null, *formPars.map { actPars[it.name] }.toTypedArray()).toString()
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

    return if(parameter.type.kotlin == Array::class) {
        list.toTypedArray()
    } else {
        list
    }
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

private fun testExample(level: Int, results: Map<String, String>) {
    val exampleResult = results
        .filterKeys { it.contains("example") }
        .map { it.value }
        .only{ "Could not find an unique example level $level. Expecting exactly 1 '.in'-file with 'example' in its name." }
        .toString()

    val exampleSolution = getExampleOutput(level)
    val line = 103 * "-"
    if(exampleResult == exampleSolution) {
        println(line)
        println("The Example for level $level has been solved correctly. Writing all files.")
        println(line)
        val thisLevelDirectory = levelDirectory.resolve("level$level").toPath()
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
