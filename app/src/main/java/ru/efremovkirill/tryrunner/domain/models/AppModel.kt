package ru.efremovkirill.tryrunner.domain.models

data class AppModel(
    val id: Long,
    val name: String,
    val packageName: String,
    val description: String,
    val version: String,
    val versionCode: Long,
    val currentAppVersion: Long,
    val logoHref: String
)
