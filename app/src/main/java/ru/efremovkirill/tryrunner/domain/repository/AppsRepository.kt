package ru.efremovkirill.tryrunner.domain.repository

import ru.efremovkirill.tryrunner.data.models.AppModelDTO
import ru.efremovkirill.tryrunner.data.models.AppVersionDTO
import ru.efremovkirill.tryrunner.domain.models.Response

interface AppsRepository {

    suspend fun getAppVersion(): Response<AppVersionDTO?>
    suspend fun getApps(): Response<List<AppModelDTO>?>

}