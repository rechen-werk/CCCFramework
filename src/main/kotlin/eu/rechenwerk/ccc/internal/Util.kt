package eu.rechenwerk.ccc.internal

internal fun <T> Collection<T?>.only(exceptionMessage: () -> String): T {
    if(this.size != 1) throw SingleException(exceptionMessage.invoke())
    return this.first()!!
}

internal fun <T> Collection<T?>.onlyOrNull(exceptionMessage: () -> String): T? {
    if(this.size > 1) throw SingleException(exceptionMessage.invoke())
    return this.firstOrNull()
}

internal operator fun String.times(other: Int) = this.repeat(other)
internal operator fun Int.times(other: String) = other.repeat(this)