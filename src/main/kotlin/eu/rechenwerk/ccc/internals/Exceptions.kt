package eu.rechenwerk.ccc.internals.exceptions

import java.lang.reflect.Method
import java.lang.reflect.Parameter
open class EngineException(message: String) : Exception(message)

class NoZipException(level: Int) : EngineException("Could not find a zip for level $level.")

class SingleException(msg: String): EngineException(msg)

class NoCharSequenceReturned(level: Int, method: Method): EngineException("Expected method with CharSequence as return type for @Level($level) ${method.name}, but it has ${method.returnType.simpleName}.")

class NoBooleanReturned(level: Int, method: Method): EngineException("Expected method with Boolean as return type for @Validator($level) ${method.name}, but it has ${method.returnType.simpleName}.")

class NoManyAnnotationException(parameter: Parameter): EngineException("Expected parameter ${parameter.name} to have Many Annotation, because it is a List.")

class ValidatorException(msg: String): EngineException(msg)