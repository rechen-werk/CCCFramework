package eu.rechenwerk.ccc.internal.services

import eu.rechenwerk.ccc.external.Level
import eu.rechenwerk.ccc.external.Line
import eu.rechenwerk.ccc.external.Validator
import eu.rechenwerk.ccc.internal.EngineException
import eu.rechenwerk.ccc.internal.NoBooleanReturned
import eu.rechenwerk.ccc.internal.NoCharSequenceReturned
import eu.rechenwerk.ccc.internal.only
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import java.lang.reflect.Method

fun methods(wantedLevel: Int): Triple<Method, Method?, Int> {
    val packages: List<String> = packages()

    val highestLevel = packages
        .flatMap { pkg -> Reflections(pkg, Scanners.MethodsAnnotated)
            .getMethodsAnnotatedWith(Level::class.java) }
        .maxOfOrNull { it.getAnnotation(Level::class.java).value }

    val level = if (wantedLevel > 0) wantedLevel else highestLevel ?: throw EngineException("No method annotated with @Level(Int).")

    val method = packages
        .flatMap { pkg -> Reflections(pkg, Scanners.MethodsAnnotated)
            .getMethodsAnnotatedWith(Level(level)) }
        .distinct()
        .only{ "Expected exactly one method with @Level($level)." }

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