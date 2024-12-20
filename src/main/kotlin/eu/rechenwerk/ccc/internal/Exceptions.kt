package eu.rechenwerk.ccc.internal

import java.lang.reflect.Method
import java.lang.reflect.Parameter

internal open class EngineException(message: String) : Exception(message)

internal class NoZipException(level: Int) : EngineException("Could not find a zip for level $level.")

internal class SingleException(msg: String): EngineException(msg)

internal class NoCharSequenceReturned(level: Int, method: Method): EngineException("Expected method with CharSequence as return type for @Level($level) ${method.name}, but it has ${method.returnType.simpleName}.")

internal class NoBooleanReturned(level: Int, method: Method): EngineException("Expected method with Boolean as return type for @Validator($level) ${method.name}, but it has ${method.returnType.simpleName}.")

internal class NoManyAnnotationException(parameter: Parameter): EngineException("Expected parameter ${parameter.name} to have Many Annotation, because it is a List.")

internal class ValidatorException(msg: String): EngineException(msg)

internal class InvalidConfigException(msg: String): Exception(msg)