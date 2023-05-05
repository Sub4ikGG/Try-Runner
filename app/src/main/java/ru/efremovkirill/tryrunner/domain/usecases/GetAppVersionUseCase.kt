package ru.efremovkirill.tryrunner.domain.usecases

import ru.efremovkirill.tryrunner.data.models.AppVersionDTO
import ru.efremovkirill.tryrunner.data.repository.AppsRepositoryImpl
import ru.efremovkirill.tryrunner.domain.models.Response
import ru.efremovkirill.tryrunner.domain.repository.AppsRepository

class GetAppVersionUseCase(
    private val repository: AppsRepository = AppsRepositoryImpl()
) {

    suspend operator fun invoke(): Response<AppVersionDTO?> = repository.getAppVersion()

}