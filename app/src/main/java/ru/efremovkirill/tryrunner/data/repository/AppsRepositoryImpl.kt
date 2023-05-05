package ru.efremovkirill.tryrunner.data.repository

import io.ktor.client.call.*
import ru.efremovkirill.tryrunner.data.ktor.KtorClient
import ru.efremovkirill.tryrunner.data.models.AppModelDTO
import ru.efremovkirill.tryrunner.data.models.AppVersionDTO
import ru.efremovkirill.tryrunner.domain.models.Response
import ru.efremovkirill.tryrunner.domain.repository.AppsRepository

class AppsRepositoryImpl: AppsRepository {
    override suspend fun getAppVersion(): Response<AppVersionDTO?> {
        val response: Response<AppVersionDTO?>?

        return try {
            response = KtorClient.get(
                path = "/debug/get-debug-version"
            )?.body()

            response ?: Response(999, "Ошибка getAppVersion", data = null)
        }
        catch (e: Exception) {
            Response(999, e.message ?: "Ошибка getAppVersion", data = null)
        }
    }

    override suspend fun getApps(): Response<List<AppModelDTO>?> {
        val response: Response<List<AppModelDTO>?>?

        return try {
            response = KtorClient.get(
                path = "/debug/get-apps"
            )?.body()

            response ?: Response(999, "Ошибка getApps", data = null)
        }
        catch (e: Exception) {
            Response(999, e.message ?: "Ошибка getApps", data = null)
        }
    }
}