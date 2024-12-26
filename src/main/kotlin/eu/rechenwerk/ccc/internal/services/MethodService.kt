package eu.rechenwerk.ccc.internal.services

import eu.rechenwerk.ccc.Level
import eu.rechenwerk.ccc.Line
import eu.rechenwerk.ccc.Validator
import eu.rechenwerk.ccc.internal.*
import eu.rechenwerk.ccc.internal.EngineException
import eu.rechenwerk.ccc.internal.NoBooleanReturned
import eu.rechenwerk.ccc.internal.NoCharSequenceReturned
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import java.lang.reflect.Method

fun methods(wantedLevel: Int): Triple<Method, Method?, Int> {
    val packages: List<String> = packages()

    val level = if (wantedLevel <= 0) {
        val highestLevel = packages
            .flatMap { pkg -> Reflections(pkg, Scanners.MethodsAnnotated)
                .getMethodsAnnotatedWith(Level::class.java) }
            .maxOfOrNull { it.getAnnotation(Level::class.java).value }
        if (highestLevel == null) {
            generateLevel(1)
            throw EngineException("No method annotated with @Level(Int). A method for level 1 has been generated for you.")
        } else highestLevel
    } else wantedLevel


    val method = packages
        .flatMap { pkg -> Reflections(pkg, Scanners.MethodsAnnotated)
            .getMethodsAnnotatedWith(Level(level)) }
        .distinct()
        .onlyOrGenerateIfNone{ level }

    if (method.returnType != CharSequence::class.java && method.returnType != String::class.java && method.returnType != Line::class.java) {
        throw NoCharSequenceReturned(level, method)
    }

    val validator = packages
            .flatMap { pkg -> Reflections(pkg, Scanners.MethodsAnnotated)
                .getMethodsAnnotatedWith(Validator(level)) }
            .distinct()
            .firstOrNull()

    if (validator != null && validator.returnType != Boolean::class.java) {
        throw NoBooleanReturned(level, validator)
    }

    return Triple(method, validator, level)
}