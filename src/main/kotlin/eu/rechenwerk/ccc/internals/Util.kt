package eu.rechenwerk.ccc.internals

import eu.rechenwerk.ccc.internals.exceptions.SingleException

fun <T> Collection<T?>.only(exceptionMessage: () -> String): T {
    if(this.size != 1) throw SingleException(exceptionMessage.invoke())
    return this.first()!!
}

fun <T> Collection<T?>.onlyOrNull(exceptionMessage: () -> String): T? {
    if(this.size > 1) throw SingleException(exceptionMessage.invoke())
    return this.firstOrNull()
}

operator fun String.times(other: Int) = this.repeat(other)
operator fun Int.times(other: String) = other.repeat(this)