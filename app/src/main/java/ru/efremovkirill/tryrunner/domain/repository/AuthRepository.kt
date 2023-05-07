package ru.efremovkirill.tryrunner.domain.repository

import ru.efremovkirill.tryrunner.domain.models.Response

interface AuthRepository {

    suspend fun auth(code: String): Response<String?>

}