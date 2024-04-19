package eu.rechenwerk.ccc.internals.exceptions

import java.lang.reflect.Method
import java.lang.reflect.Parameter
open class EngineException(message: String) : Exception(message)

class NoZipException(level: Int) : EngineException("Could not find a zip for level $level.")

class SingleException(msg: String): EngineException(msg)

class WrongReturnValueException(level: Int, method: Method): EngineException("Expected method with String as return type for @Level($level) ${method.name}, but it has ${method.returnType.simpleName}.")

class NoManyAnnotationException(parameter: Parameter): EngineException("Expected parameter ${parameter.name} to have Many Annotation, because it is a List.")