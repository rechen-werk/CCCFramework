package eu.rechenwerk.ccc.internals

import java.lang.reflect.Method
import java.lang.reflect.Parameter

class NoZipException(level: Int) : Exception("Could not find a zip for level $level")

class SingleException(msg: String): Exception(msg)

class WrongReturnValueException(level: Int, method: Method): Exception("Expected method with String as return type for @Level($level) ${method.name}, but it has ${method.returnType.simpleName}.")

class NoListOfAnnotationException(parameter: Parameter): Exception("Expected parameter ${parameter.name} to have ListOf Annotation, because it is a List.")