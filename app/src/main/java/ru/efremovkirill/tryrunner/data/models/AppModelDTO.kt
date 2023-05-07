package ru.efremovkirill.tryrunner.data.models

import ru.efremovkirill.tryrunner.domain.models.AppModel

@kotlinx.serialization.Serializable
data class AppModelDTO(
    val id: Long,
    val name: String,
    val packageName: String,
    val description: String,
    val version: String,
    val versionCode: Long,
    val logoHref: String,
    val screenshots: List<String>
) {
    fun toModel(
        currentAppVersion: Long
    ) = AppModel(
        id = id,
        name = name,
        packageName = packageName,
        description = description,
        version = version,
        versionCode = versionCode,
        currentAppVersion = currentAppVersion,
        logoHref = logoHref,
        screenshots = screenshots
    )
}
