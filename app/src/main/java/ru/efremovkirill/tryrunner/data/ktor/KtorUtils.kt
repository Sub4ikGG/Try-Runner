package ru.efremovkirill.tryrunner.data.ktor

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import ru.efremovkirill.tryrunner.data.storage.LocalStorage

object KtorUtils {

    private fun HeadersBuilder.appendHeaders() {
        val localStorage = LocalStorage.newInstance()

        append("device-id", localStorage?.getDeviceId() ?: "")
    }

    private fun URLBuilder.appendUrl(host: String = BASE_AUTH_HOST, port: Int = BASE_AUTH_PORT, path: String) {
        this.protocol = PROTOCOL
        this.host = host
//        this.port = port
        path(path)
    }

    fun HttpRequestBuilder.appendRequest(path: String, stringValues: StringValues) {
        println("StringValues: $stringValues")
        url {
            appendUrl(path = path)
            parameters.appendAll(stringValues)
        }

        headers {
            appendHeaders()
        }

        contentType(ContentType.Application.Json)
    }

    private val PROTOCOL = URLProtocol.HTTPS
    private const val BASE_AUTH_HOST = "api.yooyo.ru"
    private const val BASE_AUTH_PORT = 8080

}