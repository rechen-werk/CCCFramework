package eu.rechenwerk.ccc.internal

import eu.rechenwerk.ccc.internal.services.generateLevel
import java.lang.reflect.Method

internal fun <T> Collection<T?>.only(exceptionMessage: () -> String): T {
    if(this.size != 1) throw SingleException(exceptionMessage.invoke())
    return this.first()!!
}

internal fun Collection<Method?>.onlyOrGenerateIfNone(exceptionMessage: () -> Int): Method {
    if(this.size != 1) {
        val level = exceptionMessage.invoke()
        var message = "Expected exactly one method with @Level($level)."
        if(this.isEmpty()) {
            generateLevel(level)
            message += " A method has been generated for you."
        }
        throw SingleException(message)
    }
    return this.first()!!
}

internal operator fun String.times(other: Int) = this.repeat(other)
internal operator fun Int.times(other: String) = other.repeat(this)