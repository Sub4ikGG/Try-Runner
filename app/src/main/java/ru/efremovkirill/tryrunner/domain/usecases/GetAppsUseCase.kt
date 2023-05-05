package ru.efremovkirill.tryrunner.domain.usecases

import ru.efremovkirill.tryrunner.data.models.AppModelDTO
import ru.efremovkirill.tryrunner.data.repository.AppsRepositoryImpl
import ru.efremovkirill.tryrunner.domain.models.Response
import ru.efremovkirill.tryrunner.domain.repository.AppsRepository

class GetAppsUseCase(
    private val repository: AppsRepository = AppsRepositoryImpl()
) {

    suspend operator fun invoke(): Response<List<AppModelDTO>?> = repository.getApps()

}