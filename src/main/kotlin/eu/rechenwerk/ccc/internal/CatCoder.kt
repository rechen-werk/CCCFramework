package eu.rechenwerk.ccc.internal

import eu.rechenwerk.ccc.internal.config.Download
import eu.rechenwerk.ccc.internal.config.Upload
import eu.rechenwerk.ccc.internal.dtos.LevelInfoDto
import eu.rechenwerk.ccc.internal.dtos.ResourceLocationDto
import org.springframework.http.*
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.RestTemplate
import java.io.File

internal class CatCoder(
    competitionUrl: String,
    val cookie: String,
    val download: Download,
    val upload: Upload,
) {
    private val id = "\\d+".toRegex().find(competitionUrl)?.value
    private val catCoderUrlBase = "https://catcoder.codingcontest.org/api/contest/$id"
    private val catCoderLevelUrl = "https://catcoder.codingcontest.org/api/game/level/$id"
    private val catCoderUploadBase = "https://catcoder.codingcontest.org/api/game/$id/upload/solution/level"
    private val inputUrl = "$catCoderUrlBase/file-request/input"
    private val instructionsUrl = "$catCoderUrlBase/file-request/description"
    private val restTemplate = RestTemplate()
    private val entity: HttpEntity<String>
    private val catCoderHighestLevel: Int
    private val catcoderLevel: Int
    init {
        val headers = HttpHeaders().apply {
            set("Cookie", "SESSION=$cookie")
        }
        entity = HttpEntity(headers)
        val levelInfo = restTemplate.exchange(
            catCoderLevelUrl,
            HttpMethod.GET,
            entity,
            LevelInfoDto::class.java
        ).body ?: throw EngineException("Cookie too old or cookie/link invalid.")

        catCoderHighestLevel = levelInfo.nrOfLevels
        catcoderLevel = levelInfo.currentLevel
    }

    fun downloadMaterials(location: File) {
        if(download == Download.NONE) return;

        val destinationPdf = File(location, "Level ${catcoderLevel}.pdf")
        if(!destinationPdf.exists()) {
            download(instructionsUrl, MediaType.APPLICATION_PDF, destinationPdf)
        }

        val destinationZip = File(location, "level${catcoderLevel}.zip")
        if(!destinationZip.exists()) {
            download(inputUrl, MediaType.APPLICATION_OCTET_STREAM, destinationZip)
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

    fun uploadSolution(level: Int, results: Map<Int, File>) {
        if(upload == Upload.NONE) return
        val levelUrl = "$catCoderUploadBase${level}_"

        for (result in results.filter { it.key != 0 }) {
            val boundary = "----WebKitFormBoundaryCCCFW$level-${result.key}-692000"
            val headers = HttpHeaders().apply {
                set("Cookie", "SESSION=$cookie")
                contentType = MediaType.parseMediaType("multipart/form-data; boundary=$boundary")
            }
            val body = MultipartBodyBuilder()
            body.part("file", result.value.readBytes())
                .header("Content-Disposition", "form-data; name=\"file\"; filename=\"${result.value.name}\"")
                .header("Content-Type", "application/octet-stream")
            val entity = HttpEntity(body.build(), headers)
            val theurl = levelUrl + result.key
            val response = restTemplate.exchange(
                theurl,
                HttpMethod.POST,
                entity,
                String::class.java
            )
            if (response.statusCode == HttpStatus.OK) {
                println("File uploaded successfully")
                println("body: " + entity.body)
                println("headers: " + entity.headers)
                println(response.body)
            } else {
                println("Failed to upload file")
            }
        }
    }
    fun uploadCode(level: Int, code: File) {
        //------WebKitFormBoundaryKIXcFAegEQ2KVoa5
        //Content-Disposition: form-data; name="file"; filename="Level1.kt"
        //Content-Type: text/x-kotlin
        //
        //
        //------WebKitFormBoundaryKIXcFAegEQ2KVoa5--

        // oder

        //------WebKitFormBoundary5TzUz9dehDqGcSTv
        //Content-Disposition: form-data; name="file"; filename="Level1.zip"
        //Content-Type: application/zip
        //
        //
        //------WebKitFormBoundary5TzUz9dehDqGcSTv--
    }
}