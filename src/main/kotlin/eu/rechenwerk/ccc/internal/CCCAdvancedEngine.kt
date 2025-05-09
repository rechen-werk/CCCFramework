package eu.rechenwerk.ccc.internal

import org.springframework.http.*
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.ResourceAccessException

import eu.rechenwerk.ccc.Download.*
import eu.rechenwerk.ccc.Download
import eu.rechenwerk.ccc.Upload.*
import eu.rechenwerk.ccc.Upload

import java.io.File

internal class CCCAdvancedEngine(
    location: File,
    competitionUrl: String,
    private val cookie: String,
    private val download: Download,
    private val upload: Upload,
): CCCBaseEngine(location)
{
    private data class LevelInfoDto(
        val nrOfLevels: Int,
        val currentLevel: Int,
        val selectedLevel: Int,
        val gameFinished: Boolean,
        val lastLevel: Boolean
    )

    private data class ResourceLocationDto(
        val url: String,
        val requestMethod: String
    )

    private val id = "\\d+".toRegex().find(competitionUrl)?.value
    private val catCoderUrlBase = "https://catcoder.codingcontest.org/api/contest/$id"
    private val restTemplate = RestTemplate()
    private val entity: HttpEntity<String>
    private val maxLevel: Int
    private val level: Int
    private val finished: Boolean
    init {
        val headers = HttpHeaders().apply {
            set("Cookie", "SESSION=$cookie")
        }
        entity = HttpEntity(headers)
        val levelInfo = try {
            restTemplate.exchange(
                "https://catcoder.codingcontest.org/api/game/level/$id",
                HttpMethod.GET,
                entity,
                LevelInfoDto::class.java
            ).body ?: throw EngineException("Cookie too old or cookie/link invalid.")
        } catch (e: ResourceAccessException) {
            throw EngineException("You likely have no internet connection.")
        } catch (e: HttpClientErrorException) {
            throw EngineException("Cookie too old or cookie/link invalid.")
        }

        finished = levelInfo.gameFinished
        maxLevel = levelInfo.nrOfLevels
        level = levelInfo.currentLevel
    }

    override fun run() {
        if (finished) throw EngineException("You are already finished with this contest. Congratulations!!")

        println("Level from CatCoder: $level/$maxLevel")

        download(level)
        val result = run(level)
        submit(result)
    }

    private fun download(level: Int) {
        val destinationPdf = File(location, "Level ${level}.pdf")
        if(!destinationPdf.exists() && download in arrayOf(ASSIGNMENT, INPUT_AND_ASSIGNMENT)) {
            download("$catCoderUrlBase/file-request/description", MediaType.APPLICATION_PDF, destinationPdf)
        }

        val destinationZip = File(location, "level${level}.zip")
        if(!destinationZip.exists() && download in arrayOf(INPUT, INPUT_AND_ASSIGNMENT)) {
            download("$catCoderUrlBase/file-request/input", MediaType.APPLICATION_OCTET_STREAM, destinationZip)
        }
    }

    private fun download(url: String, mediaType: MediaType, destination: File) {
        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            ResourceLocationDto::class.java
        )
        val resourceUrl = response.body!!.url

        val requestCallback = RequestCallback { clientHttpRequest ->
            clientHttpRequest.headers.accept = listOf(mediaType)
        }

        val responseExtractor = { res: ClientHttpResponse ->
            res.body.use { inputStream ->
                destination.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        restTemplate.execute(resourceUrl, HttpMethod.GET, requestCallback, responseExtractor)
    }

    private fun submit(result: Output) {
        if (result.valid) {
            val uploaded = uploadSolution(result.results)
            if (uploaded) {
                uploadCode()

                download(level + 1)
                generateLevel(level + 1)
            }

        }
    }

    private fun uploadSolution(results: List<Result>): Boolean {
        val uploadFiles = upload in arrayOf(SOLUTION, SOLUTION_AND_CODE)
        if (!uploadFiles) return false

        for (result in results.filter { it.example != 0 }) {
            val boundary = "----WebKitFormBoundaryCCCFW${level}69152${result.example}"
            val headers = HttpHeaders().apply {
                set("Cookie", "SESSION=$cookie")
                contentType = MediaType.parseMediaType("multipart/form-data; boundary=$boundary")
            }
            val body = MultipartBodyBuilder()
            val resultFile = result.getFile(level, location.resolve("level$level"))
            body.part("file", resultFile.readBytes())
                .header("Content-Disposition", "form-data; name=\"file\"; filename=\"${resultFile.name}\"")
                .header("Content-Type", "application/octet-stream")
            val entity = HttpEntity(body.build(), headers)
            val uploadUrl = "https://catcoder.codingcontest.org/api/game/$id/upload/solution/level${level}_${result.example}"
            val response = restTemplate.exchange(
                uploadUrl,
                HttpMethod.POST,
                entity,
                String::class.java
            )
            if (response.statusCode == HttpStatus.OK) {
                println("Level $level - ${result.example} has been uploaded successfully")
            } else {
                throw EngineException("Failed to upload file level $level - ${result.example}")
            }
        }
        return true
    }

    private fun uploadCode() {
        val uploadFiles = upload in arrayOf(CODE, SOLUTION_AND_CODE)
        if (!uploadFiles) return

        val file = File("Level$level.kt")

        println(String(file.readBytes()))
        // Remove this if-branch once this is possible
        // Currently it would work if the only file is LevelX.kt, but this does not have to be the case
        if(true) {
            println("Uploading the code is not supported yet. Skipping the upload, please do that yourself.")
            return
        }

        val boundary = "----WebKitFormBoundaryCCCFW${level}69152code${upload.ordinal}"
        val headers = HttpHeaders().apply {
            set("Cookie", "SESSION=$cookie")
            contentType = MediaType.parseMediaType("multipart/form-data; boundary=$boundary")
        }
        val body = MultipartBodyBuilder()
        body.part("file", file.readBytes())
            .header("Content-Disposition", "form-data; name=\"file\"; filename=\"${file.name}\"")
            .header("Content-Type", "text/x-kotlin")
        val entity = HttpEntity(body.build(), headers)

        val uploadUrl = "https://catcoder.codingcontest.org/api/game/$id/$level/upload"
        val response = restTemplate.exchange(
            uploadUrl,
            HttpMethod.POST,
            entity,
            String::class.java
        )
        if (response.statusCode == HttpStatus.OK) {
            println("Code for level $level has been uploaded successfully")
        } else {
            throw EngineException("Failed to upload code for level $level")
        }

    }
}
