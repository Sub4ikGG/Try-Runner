package ru.efremovkirill.tryrunner.domain.usecases

import ru.efremovkirill.tryrunner.data.repository.AuthRepositoryImpl
import ru.efremovkirill.tryrunner.domain.models.Response
import ru.efremovkirill.tryrunner.domain.repository.AuthRepository

class AuthUseCase(
    private val repository: AuthRepository = AuthRepositoryImpl()
) {

    suspend operator fun invoke(code: String): Response<String?> =
        repository.auth(code = code)

}