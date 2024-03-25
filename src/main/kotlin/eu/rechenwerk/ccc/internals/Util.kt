package eu.rechenwerk.ccc.internals

import eu.rechenwerk.ccc.internals.exceptions.SingleException

fun <T> Collection<T?>.only(exceptionMessage: String = "Expected only one of something."): T {
    if(this.size != 1) throw SingleException(exceptionMessage)
    return this.first()!!
}

operator fun String.times(other: Int) = this.repeat(other)
operator fun Int.times(other: String) = other.repeat(this)