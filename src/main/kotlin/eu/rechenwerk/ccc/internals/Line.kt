package eu.rechenwerk.ccc.internals

/**
 * Class for annotations, this lets the scanner read an entire line.
 * If the next line would be empty, then the empty line is skipped.
 */
class Line(private val value: String): CharSequence, Comparable<String> by value {
    override val length: Int
        get() = value.length

    override fun get(index: Int) = value[index]

    override fun subSequence(startIndex: Int, endIndex: Int) = value.subSequence(startIndex, endIndex)

    override fun toString(): String {
        return value
    }

    operator fun plus(s: String) = value + s
    operator fun plus(c: Char) = value + c
    operator fun Char.plus(l: Line) = this + l.value
}