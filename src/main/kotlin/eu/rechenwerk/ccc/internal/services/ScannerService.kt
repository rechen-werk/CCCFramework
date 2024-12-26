package eu.rechenwerk.ccc.internal.services

import eu.rechenwerk.ccc.Line
import eu.rechenwerk.ccc.Many
import eu.rechenwerk.ccc.internal.NoManyAnnotationException
import eu.rechenwerk.ccc.internal.ValidatorException
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.util.*
import kotlin.reflect.KClass


fun Scanner.apply(method: Method, validator: Method?): Pair<String, Boolean?> {
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