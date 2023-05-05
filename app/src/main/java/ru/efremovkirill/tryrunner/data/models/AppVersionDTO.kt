package ru.efremovkirill.tryrunner.data.models

import ru.efremovkirill.tryrunner.domain.models.AppVersion

@kotlinx.serialization.Serializable
data class AppVersionDTO(
    val versionCode: Long
) {
    fun toModel() = AppVersion(versionCode = versionCode)
}
