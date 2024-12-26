package eu.rechenwerk.ccc.internal.services

import java.io.File

fun generateLevel(level: Int, language: Language = Language.KOTLIN) {
    val file = File("Level$level.kt")
    if(!file.exists()) {
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
}

enum class Language {
    JAVA, KOTLIN
}