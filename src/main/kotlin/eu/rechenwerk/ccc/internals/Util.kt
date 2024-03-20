package eu.rechenwerk.ccc.internals

fun <T> Collection<T?>.only(exceptionMessage: String = "Expected only one of something."): T {
    if(this.size != 1) throw SingleException(exceptionMessage)
    return this.first()!!
}