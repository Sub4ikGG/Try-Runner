package ru.efremovkirill.tryrunner.domain.models

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Response<T>(
    @SerialName("code")
    val code: Int,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: T
)
