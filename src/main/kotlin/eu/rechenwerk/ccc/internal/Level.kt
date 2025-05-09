package eu.rechenwerk.ccc.internal

import eu.rechenwerk.ccc.Line
import eu.rechenwerk.ccc.Many
import java.io.File
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.nio.file.Path
import java.util.*
import kotlin.reflect.KClass

internal data class Code(
    val examples: List<Int>,
    val code: Method,
    val validatorCode: Method?
)
{
    fun hasValidator() = validatorCode != null
}

internal data class Input(
    val level: Int,
    val problems: List<Problem>
)
{
    fun run (code: Code): Output {
        val line = "+" + 35 * " -"
        val runAll = code.examples.isEmpty()

        if (runAll) {
            if(code.hasValidator()) {
                println(line)
                println("| Testing your solutions for level $level according to @Validator($level).")
                val outputs = problems.map { it.run(code) }
                val valid = outputs.all { it.valid ?: throw IllegalStateException("Evaluation has valid set to null, even though validator is correct.") }

                outputs.sortedBy { it.example }.forEach {
                    println(line)
                    println("| level${level}_${it.example}: ${if(it.valid!!) "VALID" else "INVALID"}")
                    if(!it.valid) {
                        println("| Annotate the level method with @Example(${it.example}) to get more information about your output to the example.")
                    }
                }
                println(line)

                return Output(valid, outputs)
            } else {
                val groupedProblemsBySolution = problems.groupBy { it.hasSolution() }
                val testOutputs = groupedProblemsBySolution[true]!!.map { it.run(code) }

                val valid = testOutputs.all { it.valid ?: throw IllegalStateException("Evaluation has valid set to null, even though solution is provided.") }

                if (valid) {
                    val outputs = testOutputs + groupedProblemsBySolution[false]!!.map { it.run(code) }

                    println(line)
                    println("| The Example for level $level has been solved correctly.")
                    println(line)

                    return Output(true, outputs)
                } else {
                    println(line)
                    println("| The Example has not been solved correctly. Here are the outputs of both versions:")
                    println(line)
                    println("| Example: ")
                    println(line)
                    println("| " + groupedProblemsBySolution[true]!!.first().solutionString()?.replace("\n", "\n| "))
                    println(line)
                    println("| Your solution: ")
                    println(line)
                    println("| " + testOutputs.first().result.replace("\n", "\n| "))
                    println(line)

                    return Output(false, testOutputs)
                }
            }
        } else {
            println(line)
            println("| Testing your solution for Level($level).")
            println(line)
            val outputs = problems
                .filter { code.examples.contains(it.example) }
                .sortedBy { it.example }
                .map {
                    print("| @Example(${it.example}):")
                    val output = it.run(code)
                    println(" ${if(output.valid != null) if (output.valid) "VALID" else "INVALID" else "NO VALIDATOR"}")
                    println(line)
                    output
                }

            if (outputs.isEmpty()) {
                throw EngineException("Invalid value for @Example. None of your @Example-Annotations at level $level matched the available level examples (0 - ${problems.size - 1}).")
            }

            return Output(false, outputs)
        }
    }
}

internal data class Output(
    val valid: Boolean,
    val results: List<Result>
)

internal data class Problem(
    val example: Int,
    private val problem: Scanner,
    private val solution: Scanner?
)
{
    private var solutionString: String? = null
    internal fun hasSolution() = solution != null

    internal fun run(code: Code): Result {
        val method = code.code
        val validator = code.validatorCode
        val formalParameters = method.parameters
        val actualParameters = HashMap<String, Any>()

        actualParameters.fill(problem, formalParameters)

        if(validator != null) {
            val validatorFormPars = validator.parameters
            if(formalParameters.size != validatorFormPars.size) throw ValidatorException(code)
            formalParameters.forEachIndexed { index, parameter -> if(parameter.type != validatorFormPars[index].type) throw ValidatorException(code) }
        }
        val evaluation = method.invoke(null, *formalParameters.map { actualParameters[it.name] }.toTypedArray()).toString()
        val valid = (validator?.invoke(null, *formalParameters.map { actualParameters[it.name] }.toTypedArray()) as Boolean?) ?: validate(evaluation)

        return Result(example, evaluation, valid)
    }

    internal fun solutionString(): String? {
        if(solutionString != null) {
            return solutionString
        }
        if (solution != null) {
            val sb = StringBuilder()
            if (solution.hasNextLine())
                sb.append(solution.nextLine())
            while (solution.hasNextLine())
                sb.append("\n").append(solution.nextLine())

            solutionString = sb.toString()
            return solutionString
        } else {
            return null
        }
    }

    private fun validate(evaluation: String): Boolean? {
        return evaluation == (solutionString() ?: return null)
    }

    private fun HashMap<String, Any>.fill(scanner: Scanner, parameters: Array<Parameter>) {
        parameters.forEach { param ->
            this[param.name] = when (param.type.kotlin) {
                List::class -> scanner.scanMany(param, this)
                else -> scanner.scan(param.type.kotlin)
            }
        }
    }

    private fun Scanner.scan(type: KClass<*>) = when(type) {
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
            val formalParameters = constructor?.parameters ?: arrayOf()
            val actualParameters = HashMap<String, Any>()
            actualParameters.fill(this, formalParameters)
            constructor.newInstance(*formalParameters.map { actualParameters[it.name] }.toTypedArray())
        }
    }

    private fun Scanner.scanMany(parameter: Parameter, actPars: Map<String, Any>): Any {
        val manyAnnotation = parameter.getAnnotation(Many::class.java) ?: throw NoManyAnnotationException(parameter)
        val quantity = actPars[manyAnnotation.sizeParamName] as Int
        val list: MutableList<Any> = mutableListOf()

        repeat(quantity) { list += scan(manyAnnotation.type) }

        return list
    }
}

internal data class Result (
    val example: Int,
    val result: String,
    val valid: Boolean?
)
{
    internal fun getPath(level: Int, levelDirectory: Path): Path {
        return levelDirectory.resolve("level${level}_${example}.out".replace("_0.", "_example."))
    }
    internal fun getFile(level: Int, levelDirectory: File): File {
        return levelDirectory.resolve("level${level}_${example}.out".replace("_0.", "_example."))
    }
}