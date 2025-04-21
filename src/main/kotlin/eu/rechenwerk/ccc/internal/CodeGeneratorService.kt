package eu.rechenwerk.ccc.internal

import java.io.File

fun generateLevel(level: Int, language: Language = Language.KOTLIN) {
    if(language == Language.KOTLIN) {
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
    } else {
        val file = File("Level$level.java")
        if(file.exists()) return

        file.createNewFile()
        file.writeText("""
        package eu.rechenwerk;

        import eu.rechenwerk.ccc.*;

        public class Template {
            //@Example(0)
            @Level($level)
            String level$level(
                
            ) {
                return "";
            }

            //@Validator($level)
            boolean validator$level(
                
            ) {
                return true;
            }
        }
    """.trimIndent())
    }
}

enum class Language {
    JAVA, KOTLIN
}