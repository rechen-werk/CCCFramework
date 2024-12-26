package eu.rechenwerk.ccc

import eu.rechenwerk.ccc.internal.CCCEngine
import eu.rechenwerk.ccc.internal.CatCoder
import eu.rechenwerk.ccc.internal.EngineException
import eu.rechenwerk.ccc.internal.InvalidConfigException
import java.io.File

fun ccc(init: CloudflightCodingContestConfig.() -> Unit) {
    CloudflightCodingContestConfig.build(init)
}

class CloudflightCodingContestConfig {

    private var location: String? = null
    private var catCoder: CatCoder? = null
    private var level: Int = 0

    fun location(value: String) {
        location = value
    }

    fun catcoder(init: (CloudflightCodingContestCatCoderConfig.() -> Unit)? = null) {
        if(init != null) {
            val cccccc = CloudflightCodingContestCatCoderConfig()
            cccccc.init()
            catCoder = CatCoder(
                cccccc.url ?: throw IllegalArgumentException("Url for CatCoder must be provided!"),
                cccccc.cookie ?: throw IllegalArgumentException("Cookie for CatCoder must be provided!"),
                cccccc.autoDownload,
                cccccc.autoUpload
            )
        }
    }

    class CloudflightCodingContestCatCoderConfig {
        var url: String? = null
        var cookie: String? = null
        var autoDownload = false
        var autoUpload = false
    }

    fun level(level: Int) {
        if(level < 0) throw IllegalArgumentException("Level must be positive!")

        if(level == 0) {
            this.level = 0
        } else {
            this.level = level
        }
    }

    private fun build(): CCCEngine {
        return CCCEngine(
            File(location ?: throw InvalidConfigException("Location must be specified!")),
            level,
            catCoder
        )
    }

    companion object {
        fun build(init: CloudflightCodingContestConfig.() -> Unit) {
            val cccc = CloudflightCodingContestConfig()
            cccc.init()
            try {
                cccc.build().start()
            } catch (e: EngineException) {
                System.err.println(e.message)
            }
        }
    }
}
