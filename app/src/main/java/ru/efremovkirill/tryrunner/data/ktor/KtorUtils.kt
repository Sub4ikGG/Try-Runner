package ru.efremovkirill.tryrunner.data.ktor

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*

object KtorUtils {

    private fun HeadersBuilder.appendHeaders() {
        /*val localStorage = LocalStorage.newInstance()

        append(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
        append(DEVICE_ID_HEADER, localStorage?.getDeviceId() ?: "")
        append(TOKEN_HEADER, localStorage?.getToken() ?: "")*/
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

    private const val CONTENT_TYPE_HEADER = "Content-Type"
    private const val CONTENT_TYPE_VALUE = "application/json"
    private const val DEVICE_ID_HEADER = "device-id"
    private const val TOKEN_HEADER = "token"

    private val PROTOCOL = URLProtocol.HTTPS
    private const val BASE_AUTH_HOST = "api.yooyo.ru"
    private const val BASE_AUTH_PORT = 8080

}