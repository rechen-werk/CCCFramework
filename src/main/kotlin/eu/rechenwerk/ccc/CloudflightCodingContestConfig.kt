package eu.rechenwerk.ccc

import eu.rechenwerk.ccc.internal.CCCEngine
import eu.rechenwerk.ccc.internal.InvalidConfigException
import java.io.File

fun ccc(init: CloudflightCodingContestConfig.() -> Unit) {
    CloudflightCodingContestConfig.build(init)
}

class CloudflightCodingContestConfig {

    private var location: String? = null
    private var cookie: String? = null
    private var autoDownload = false
    private var autoUpload = false
    private var level: Int = 0

    fun location(value: String) {
        location = value
    }

    fun cookie(cookie: String, enable: (CloudflightCodingContestAutoConfig.() -> Unit)? = null) {
        this.cookie = cookie
        if(enable != null) {
            val cccac = CloudflightCodingContestAutoConfig()
            cccac.enable()
            this.autoDownload = cccac.autoDownload
            this.autoUpload = cccac.autoUpload
        }
    }

    class CloudflightCodingContestAutoConfig {
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
            cookie,
            autoDownload,
            autoUpload
        )
    }

    companion object {
        fun build(init: CloudflightCodingContestConfig.() -> Unit) {
            val cccc = CloudflightCodingContestConfig()
            cccc.init()
            return cccc.build().start()
        }
    }
}
