package ru.efremovkirill.tryrunner.data.ktor

import android.util.Log
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import ru.efremovkirill.tryrunner.data.ktor.KtorUtils.appendRequest

object KtorClient {

    private const val TAG = "Ktor-Client"
    private lateinit var client: HttpClient

    fun initialize() {
        client = HttpClient {
            install(ContentNegotiation) { json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }) }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }

    private fun getClient(): HttpClient = client

    suspend fun get(path: String, body: Any = "", stringValues: StringValues = StringValues.Empty): HttpResponse? {
        Log.w(TAG, "\n")
        Log.w(TAG, "GET: [$path]")
        Log.w(TAG, "QUERY: ${stringValues.entries()}")
        Log.w(TAG, "BODY: $body ---->")
        return try {
            val response = getClient().get {
                appendRequest(path = path, stringValues = stringValues)
                setBody(body)
            }

            Log.w(TAG, "<---- path [${path}]: ${response.status}")
            Log.w(TAG, "BODY: ${response.bodyAsText()}")
            Log.w(TAG, "HEADER: ${response.headers}")

            if(response.status == HttpStatusCode.Unauthorized)
                throw Exception("401")

            return response
        }
        catch (e: Exception) {
            checkUnauthorized(
                exception = e,
                unit = {
                    runBlocking {
                        get(path = path, body = body)
                    }
                }
            )
        }
    }

    suspend fun post(path: String, body: Any = "", stringValues: StringValues = StringValues.Empty): HttpResponse? {
        Log.w(TAG, "\n")
        Log.w(TAG, "POST: $path")
        Log.w(TAG, "QUERY: ${stringValues.entries()}")
        Log.w(TAG, "BODY: $body ---->")
        return try {
            val response = getClient().post {
                appendRequest(path = path, stringValues = stringValues)
                setBody(/*if(body is RefreshTokenDTO) getRefreshTokenDTOFromStorage() else*/ body)
            }

            Log.w(TAG, "<---- ${response.status}")
            Log.w(TAG, "BODY: ${response.bodyAsText()}")
            Log.w(TAG, "HEADERS: ${response.headers}")

            if(response.status == HttpStatusCode.Unauthorized)
                throw Exception("401")

            return response
        }
        catch (e: Exception) {
            checkUnauthorized(
                exception = e,
                unit = {
                    runBlocking {
                        post(path = path, body = body)
                    }
                }
            )
        }
    }

    /*private fun getRefreshTokenDTOFromStorage(): RefreshTokenDTO {
        val localStorage = LocalStorage.newInstance()
        Log.e(TAG, "getRefreshTokenDTOFromStorage: ${localStorage?.getRefreshToken()}", )
        return RefreshTokenDTO(refreshToken = localStorage?.getRefreshToken() ?: "")
    }*/

    fun closeClient() {
        client.close()
    }

    private suspend fun checkUnauthorized(
        exception: Exception,
        unit: () -> HttpResponse?
    ): HttpResponse? {
        /*if (exception.message?.contains("401") == true) {
            if (updateTokens())
                return unit()

            return null
        } else*/
            return null
    }

    /*private suspend fun updateTokens(): Boolean {
        val localStorage = LocalStorage.newInstance()
        val token = localStorage?.getToken() ?: return false
        val refreshToken = localStorage.getRefreshToken() ?: return false

        val tokens = TokensDTO(
            token = token,
            refreshToken = refreshToken
        )

        val newTokens: Response<TokensDTO?>

        return try {
            newTokens = post(
                "/token-refresh",
                body = tokens
            )!!.body()

            val tokensDTO = newTokens.data ?: return false
            saveTokens(localStorage, tokensDTO)
            return true
        } catch (_: Exception) {
            false
        }
    }

    private fun saveTokens(localStorage: LocalStorage, tokensDTO: TokensDTO) {
        localStorage.saveToken(token = tokensDTO.token)
        localStorage.saveRefreshToken(refreshToken = tokensDTO.refreshToken)
    }*/

}