package eu.rechenwerk.ccc.internal

import eu.rechenwerk.ccc.internal.dtos.LevelInfoDto
import eu.rechenwerk.ccc.internal.dtos.ResourceLocationDto
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.RestTemplate
import java.io.File


class CatCoder(
    url: String,
    val cookie: String,
    val autoDownload: Boolean,
    val autoUpload: Boolean,
) {
    private val id = "\\d+".toRegex().find(url)?.value
    private val catCoderUrlBase = "https://catcoder.codingcontest.org/api/contest/$id"
    private val catCoderLevelUrl = "https://catcoder.codingcontest.org/api/game/level/$id"
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
        if(autoDownload) {
            val destinationPdf = File(location, "Level ${catcoderLevel}.pdf")
            if(!destinationPdf.exists()) {
                download(instructionsUrl, MediaType.APPLICATION_PDF, destinationPdf)
            }

            val destinationZip = File(location, "level${catcoderLevel}.zip")
            if(!destinationZip.exists()) {
                download(inputUrl, MediaType.APPLICATION_OCTET_STREAM, destinationZip)
            }
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

    fun uploadSolution() {

    }
}