package eu.rechenwerk.ccc.internals

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

val levelDirectory = File(System.getProperty("user.home")).resolve("ccc")
const val pkg = "eu.rechenwerk.ccc"

fun run(level: Int) {
    val method = method(level)
    val problems = scanners(level).filterKeys { name -> name.endsWith(".in") }
    val results = problems.mapValues { (_, scanner) -> scanner.apply(method) }
    testExample(level, results)
}

private fun getZip(level: Int) = levelDirectory.listFiles()!!.first { it.name == "level$level.zip" } ?: throw NoZipException(level)

private fun scanners(level: Int): Map<String, Scanner> {
    val zipFile = ZipFile(getZip(level))
    return zipFile.entries().asSequence().map {
        it.name to Scanner(zipFile.getInputStream(it))
    }.toMap()
}

private fun method(level: Int): Method {
    val method = Reflections(pkg, Scanners.MethodsAnnotated)
        .getMethodsAnnotatedWith(Level(level))
        .only("Expected exactly one method with @Level($level).")

    if(method.returnType != String::class.java) {
        throw WrongReturnValueException(level, method)
    }
    return method
}

private fun Scanner.apply(method: Method): String {
    val formPars = method.parameters
    val actPars = HashMap<String, Any>()
    actPars.fill(this, formPars)
    // I am not using actPars.values, because maps do not have to preserve ordering
    return method.invoke(null, *formPars.map { actPars[it.name] }.toTypedArray()) as String
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
    return when(type) {
        Byte::class -> this.nextByte()
        Short::class -> this.nextShort()
        Int::class -> this.nextInt()
        Long::class -> this.nextLong()
        Float::class -> this.nextFloat()
        Double::class -> this.nextDouble()
        Char::class -> this.next()[0]
        String::class -> this.next()
        else -> {
            val constructor = type.java.constructors[0]
            val formPars = constructor?.parameters ?: arrayOf()
            val actPars = HashMap<String, Any>()
            actPars.fill(this, formPars)
            // I am not using actPars.values, because maps do not have to preserve ordering
            constructor.newInstance(*formPars.map { actPars[it.name] }.toTypedArray())
        }
    }
}

private fun Scanner.scanMany(parameter: Parameter, actPars: Map<String, Any>): Any {
    val anno = parameter.getAnnotation(Many::class.java) ?: throw NoListOfAnnotationException(parameter)
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
    while (outputScanner.hasNextLine())
        sb.append(outputScanner.nextLine())

    return sb.toString()
}

private fun testExample(level: Int, results: Map<String, String>) {
    val exampleResult = results
        .filterKeys { it.contains("example") }
        .map { it.value }.only("Could not find an unique example level $level. Expecting exactly 1 '.in'-file with 'example' in its name.")

    val exampleSolution = getExampleOutput(level)
    val line = "------------------------------------------------------------------------"
    if(exampleResult == exampleSolution) {
        println(line)
        println("The Example has been solved correctly")
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
        println("The Example has not been solved correctly, here are the outputs of both:")
        println("Example: ")
        println(exampleSolution)
        println(line)
        println("Your solution: ")
        println(exampleResult)
        println(line)
    }
}
