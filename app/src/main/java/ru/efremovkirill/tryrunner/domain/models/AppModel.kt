package ru.efremovkirill.tryrunner.domain.models

@kotlinx.serialization.Serializable
data class AppModel(
    val id: Long,
    val name: String,
    val packageName: String,
    val description: String,
    val version: String,
    val versionCode: Long,
    val currentAppVersion: Long,
    val logoHref: String,
    val screenshots: List<String>
)
