package ru.efremovkirill.tryrunner.data.repository

import io.ktor.client.call.*
import ru.efremovkirill.tryrunner.data.ktor.KtorClient
import ru.efremovkirill.tryrunner.data.models.AccessCodeDTO
import ru.efremovkirill.tryrunner.domain.models.Response
import ru.efremovkirill.tryrunner.domain.repository.AuthRepository

class AuthRepositoryImpl: AuthRepository {

    override suspend fun auth(code: String): Response<String?> {
        val response: Response<String?>?

        return try {
            response = KtorClient.post(
                path = "/debug/auth",
                body = AccessCodeDTO(code = code)
            )?.body()

            response ?: Response(999, "Ошибка auth", data = null)
        }
        catch (e: Exception) {
            Response(999, e.message ?: "Ошибка auth", data = null)
        }
    }

}