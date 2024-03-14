package eu.rechenwerk.ccc.internals

import eu.rechenwerk.ccc.internals.annotations.*
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import java.io.File
import java.lang.StringBuilder
import java.lang.reflect.Field
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.zip.ZipFile

val levelDirectory = File(System.getProperty("user.home")).resolve("ccc")

fun run(level: Int) {
    val methods = Reflections("eu.rechenwerk.ccc", Scanners.MethodsAnnotated).getMethodsAnnotatedWith(Level(level)).toSet()
    val definitions = Reflections("eu.rechenwerk.ccc").getTypesAnnotatedWith(Definition(level))
    val inputs = Reflections("eu.rechenwerk.ccc").getTypesAnnotatedWith(Input(level))
    val outputs = Reflections("eu.rechenwerk.ccc").getTypesAnnotatedWith(Output(level))
    val zips = getZips().filter { it.name.contains(level.toString()) }

    if(methods.size != 1) {
        throw IllegalStateException("Expected exactly one method with @Level($level), but found ${methods.size}.")
    }
    if(definitions.size != 1) {
        throw IllegalStateException("Expected exactly one class with @Definition($level), but found ${inputs.size}.")
    }
    if(inputs.size != 1) {
        throw IllegalStateException("Expected exactly one class with @Input($level), but found ${inputs.size}.")
    }
    if(outputs.size != 1) {
        throw IllegalStateException("Expected exactly one class with @Output($level), but found ${outputs.size}.")
    }
    if(zips.size != 1) {
        throw IllegalStateException("Expected exactly one zip for level $level in $levelDirectory.")
    }

    val method = methods.first()!!
    val input = inputs.first()!!
    val definition = definitions.first()!!
    val output = outputs.first()!!
    val zip = zips.first()

    if(method.parameters.size != 1) {
        throw IllegalStateException("Expected method with exactly 1 parameter for @Level($level), but it has ${method.parameters.size}.")
    }
    if(method.parameters[0].type != input) {
        throw IllegalStateException("Expected method with ${input.simpleName} as parameter.")
    }
    if(method.returnType != output) {
        throw IllegalStateException("Expected method with ${output.simpleName} as return type for @Level($level), but it has ${method.returnType.simpleName}.")
    }

    val results = generateInputs(zip, definition, input)
        .map {
            it.key to method.invoke(null, it.value)
        }
        .toMap()

    val exampleResult = results
        .filter { it.key.contains("example") }
        .map { it.value }
        .first()!!

    val example = getExampleOutput(zip)
    if(exampleResult.toString() == example) {
        println("------------------------------------------------------------------------")
        println("The Example has been solved correctly")
        println("------------------------------------------------------------------------")
    } else {
        println("------------------------------------------------------------------------")
        println("The Example has not been solved correctly, here are the outputs of both:")
        println("Example: ")
        println(example)
        println("------------------------------------------------------------------------")
        println("Your solution: ")
        println(exampleResult)
        println("-------------------------------------------------------------------------")
    }
    val thisLevelDirectory = levelDirectory.resolve("level$level").toPath()
    Files.createDirectories(thisLevelDirectory)
    results.forEach{
        val file = thisLevelDirectory.resolve(it.key.replace("in", "out"))
        Files.deleteIfExists(file)
        Files.writeString(file, it.value.toString(), StandardOpenOption.CREATE)
    }
}

private fun getZips(): List<File> {
    if(!levelDirectory.exists()) Files.createDirectory(levelDirectory.toPath())

    return levelDirectory.listFiles()!!.filter { it.name.startsWith("level") && it.name.endsWith(".zip") }
}

private fun generateInputs(zip: File, definition: Class<*>, input: Class<*>): Map<String, Any> {
    val scanners = getScannersFromZip(zip)
    val inputScanners = scanners.filter { it.key.endsWith(".in") }
    return inputScanners.map { entry ->
        entry.key to input
            .constructors
            .first()
            .newInstance(
                *generateInput(definition.declaredFields, entry.value).toTypedArray()
            )
    }.toMap()
}

private fun getExampleOutput(zip: File): String {
    val scanners = getScannersFromZip(zip)
    val outputScanner = scanners
        .filter { it.key.endsWith(".out") }
        .map { it.value }
        .first()

    val sb = StringBuilder()
    while (outputScanner.hasNextLine())
        sb.append(outputScanner.nextLine())

    return sb.toString()
}

private fun generateInput(formpars: Array<out Field>, scanner: Scanner): List<Any> {
    val fields = mutableMapOf<String, Any>()
    return formpars.map { formparMap(it, scanner, fields) }
}

private fun formparMap(formpar: Field, scanner: Scanner, fields: MutableMap<String, Any>): Any {
    val name = formpar.name!!
    val repeatAnno = formpar.getAnnotation(Repeat::class.java)
    val repeat = repeatAnno != null
    val length = if(repeat) fields[repeatAnno.field] as Int else 1
    return when(formpar.type.toString()) {
        "int" -> {
            if(repeat) {
                val list = mutableListOf<Int>()
                for (i in 0 until length) {
                    list += scanner.nextInt()
                }
                val value = list.toList()
                fields[name] = value
                value
            } else {
                val value = scanner.nextInt()
                fields[name] = value
                value
            }
        }
        "long" -> {
            if(repeat) {
                val list = mutableListOf<Long>()
                for (i in 0 until length) {
                    list += scanner.nextLong()
                }
                val value = list.toList()
                fields[name] = value
                value
            } else {
                val value = scanner.nextLong()
                fields[name] = value
                value
            }
        }
        "float" -> {
            if(repeat) {
                val list = mutableListOf<Float>()
                for (i in 0 until length) {
                    list += scanner.nextFloat()
                }
                val value = list.toList()
                fields[name] = value
                value
            } else {
                val value = scanner.nextFloat()
                fields[name] = value
                value
            }
        }
        "double" -> {
            if(repeat) {
                val list = mutableListOf<Double>()
                for (i in 0 until length) {
                    list += scanner.nextDouble()
                }
                val value = list.toList()
                fields[name] = value
                value
            } else {
                val value = scanner.nextDouble()
                fields[name] = value
                value
            }
        }
        "char" -> {
            if(repeat) {
                val list = mutableListOf<Char>()
                for (i in 0 until length) {
                    list += scanner.next()[0]
                }
                val value = list.toList()
                fields[name] = value
                value
            } else {
                val value = scanner.next()[0]
                fields[name] = value
                value
            }
        }
        "byte" -> {
            if(repeat) {
                val list = mutableListOf<Byte>()
                for (i in 0 until length) {
                    list += scanner.nextByte()
                }
                val value = list.toList()
                fields[name] = value
                value
            } else {
                val value = scanner.nextByte()
                fields[name] = value
                value
            }
        }
        "class java.lang.String" -> {
            if(repeat) {
                val list = mutableListOf<String>()
                for (i in 0 until length) {
                    list += scanner.next()
                }
                val value = list.toList()
                fields[name] = value
                value
            } else {
                val value = scanner.next()
                fields[name] = value
                value
            }
        }
        "class eu.rechenwerk.ccc.internals.Line" -> {
            scanner.nextLine()
            if(repeat) {
                val list = mutableListOf<String>()
                for (i in 0 until length) {
                    list += scanner.nextLine()
                }
                val value = list.toList()
                fields[name] = value
                value
            } else {
                val value = scanner.nextLine()
                fields[name] = value
                value
            }
        }
        else -> {
            if(repeat) {
                val list = mutableListOf<Any>()
                for (i in 0 until length) {
                    val classActPars = formpar.type.declaredFields.map { formparMap(it, scanner, fields) }
                    list += formpar.type.constructors.first().newInstance(*classActPars.toTypedArray())
                }
                val value = list.toList()
                fields[name] = value
                value
            } else {
                val classActPars = formpar.type.declaredFields.map { formparMap(it, scanner, fields) }
                val value = formpar.type.constructors.first().newInstance(*classActPars.toTypedArray())
                fields[name] = value
                value
            }
        }
    }
}

private fun getScannersFromZip(zip: File): Map<String, Scanner> {
    val zipFile = ZipFile(zip)
    return zipFile.entries().asSequence().map {
        it.name to Scanner(zipFile.getInputStream(it))
    }.toMap()
}

class Line