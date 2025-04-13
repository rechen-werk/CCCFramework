package eu.rechenwerk.ccc.internal.config

import eu.rechenwerk.ccc.internal.*
import eu.rechenwerk.ccc.internal.EngineException
import java.io.File
import kotlin.system.exitProcess

fun simpleEngine(init: PlainEngineConfig.() -> Unit) {
    PlainEngineConfig.build(init)
}

fun advancedEngine(init: CatCoderConfig.() -> Unit) {
    CatCoderConfig.build(init)
}

class CatCoderConfig {
    lateinit var location: String
    lateinit var competitionUrl: String
    lateinit var cookie: String
    lateinit var download: Download
    lateinit var upload: Upload

    private fun validate() {
        var error = false
        if (!::competitionUrl.isInitialized) {
            System.err.println("You must specify a the competition url of the contest!")
            error = true
        }
        if (!::cookie.isInitialized) {
            System.err.println("You must specify the cookie in order to get the data from CatCoder!")
            error = true
        }
        if (!::download.isInitialized) {
            System.err.println("You must specify a download configuration!")
            error = true
        }
        if (!::upload.isInitialized) {
            System.err.println("You must specify an upload configuration!")
            error = true
        }
        if (!::location.isInitialized) {
            System.err.println("You must specify a location for your files in the CCC Framework!")
            error = true
        }

        if (error) {
            exitProcess(-1)
        }
    }

    private fun build() = CCCAdvancedEngine(File(location), CatCoder(competitionUrl, cookie, download, upload))

    internal companion object {
        fun build(init: CatCoderConfig.() -> Unit) {
            val config = CatCoderConfig()
            config.init()
            config.validate()
            try {
                config.build().run()
            } catch (e: EngineException) {
                System.err.println(e.message)
            }
        }
    }
}

class PlainEngineConfig {
    lateinit var location: String
    var level: Int? = null

    private fun validate() {
        var error = false
        if (!::location.isInitialized) {
            System.err.println("You must specify a location for your files in the CCC Framework!")
            error = true
        }
        if (level != null && level!! <= 0) {
            System.err.println("Level must be positive (omitted to calculate the highest available level based on @Level annotation).")
            error = true
        }

        if (error) {
            exitProcess(-1)
        }
    }

    private fun build() = CCCSimpleEngine(File(location), level)

    internal companion object {
        fun build(init: PlainEngineConfig.() -> Unit) {
            val config = PlainEngineConfig()
            config.init()
            config.validate()
            try {
                config.build().run()
            } catch (e: EngineException) {
                System.err.println(e.message)
            }
        }
    }
}
