package eu.rechenwerk.ccc.internal

import java.io.File
import java.lang.reflect.Method

internal fun Collection<Method?>.onlyOrGenerateIfNone(exceptionMessage: () -> Int): Method {
    if(this.size != 1) {
        val level = exceptionMessage.invoke()
        var message = "Expected exactly one method with @Level($level)."
        if(this.isEmpty()) {
            generateLevel(level)
            message += " A template has been generated for you."
        }
        throw SingleException(message)
    }
    return this.first()!!
}

operator fun String.times(other: Int) = this.repeat(other)
operator fun Int.times(other: String) = other.repeat(this)

internal fun generateLevel(level: Int) {
    val file = File("Level$level.kt")
    if(file.exists()) return

    file.createNewFile()
    file.writeText("""
    package eu.rechenwerk
    
    import eu.rechenwerk.ccc.*
    
    //@Example(0)
    @Level($level) 
    fun level$level(
        
    ): String {
        return ""
    }
    
    //@Validator($level) 
    fun validator$level(
        
    ): Boolean {
        return true
    }
""".trimIndent())
}
