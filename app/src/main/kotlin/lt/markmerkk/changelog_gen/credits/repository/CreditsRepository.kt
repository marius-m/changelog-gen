package lt.markmerkk.changelog_gen.credits.repository

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.reactivex.Single
import lt.markmerkk.changelog_gen.credits.entities.Credit
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import sun.misc.Launcher
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.util.jar.JarFile

class CreditsRepository() {

    private val gson: Gson = Gson()
    private val creditPath: String = "credits"
    private var credits: List<Credit> = emptyList()

    fun creditEntries(): Single<List<Credit>> {
        return if (credits.isEmpty()) {
            Single.defer {
                val jarFile = File(javaClass.protectionDomain.codeSource.location.path)
                if (jarFile.isFile) {
                    readFromJsonResource(jarFile)
                } else {
                    readFromLocalResource()
                }
            }.doOnSuccess { this.credits = it }
        } else {
            Single.just(credits)
        }
    }

    private fun readFromLocalResource(): Single<List<Credit>> {
        return Single.defer<List<Credit>> {
            val url = Launcher::class.java.getResource("/$creditPath")
            if (url == null) Single.error<List<Credit>>(IllegalStateException("Cannot figure URL"))
            try {
                val credits = File(url.toURI()).listFiles()
                    .mapNotNull {
                        try {
                            val contents = FileUtils.readFileToString(it)
                            Pair(it.name, contents)
                        } catch (e: IOException) {
                            logger.warn("Cannot read ${it.name}", e)
                            null
                        }
                    }
                    .mapNotNull { (fileName, fileContents) ->
                        toCredit(fileName, fileContents)
                    }
                Single.just(credits)
            } catch (ex: URISyntaxException) {
                ex.printStackTrace()
                Single.error<List<Credit>>(IllegalArgumentException("Error reading local resource"))
            }
        }
    }

    private fun readFromJsonResource(
        jarFile: File
    ): Single<List<Credit>> {
        return Single.defer {
            val jar = JarFile(jarFile)
            val entries = jar.entries()
            val jarEntries = entries.toList()
                .filter { it.name.startsWith("$creditPath/") }
                .mapNotNull {
                    try {
                        val contents = jarFileToString(it.name)
                        Pair(it.name, contents)
                    } catch (e: IOException) {
                        logger.warn("Cannot read ${it.name}", e)
                        null
                    }
                }.mapNotNull { (name, contents) ->
                    toCredit(name, contents)
                }
            jar.close()
            Single.just(jarEntries)
        }
    }

    @Throws(IOException::class)
    private fun jarFileToString(inputPath: String): String {
        val fileIs = javaClass.classLoader.getResourceAsStream(inputPath)
        return IOUtils.toString(fileIs, "UTF-8")
    }

    private fun toCredit(source: String, input: String): Credit? {
        return try {
            gson.fromJson(input, Credit::class.java)
        } catch (e: JsonSyntaxException) {
            logger.warn("Error trying to parse out json from $source", e)
            null
        }
    }

    companion object {
        val logger = LoggerFactory.getLogger(CreditsRepository::class.java)!!
    }

}