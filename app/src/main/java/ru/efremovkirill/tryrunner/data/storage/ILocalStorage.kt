package ru.efremovkirill.tryrunner.data.storage

interface ILocalStorage {

    fun save(key: String, data: String)

    fun has(key: String): Boolean

    fun get(key: String): String?

    fun saveToken(token: String)

    fun saveRefreshToken(refreshToken: String)

    fun saveCourierJson(json: String)

    fun saveDeviceId(deviceId: String)

    fun saveBaseAvatar(avatarId: Int)

    fun getDeviceId(): String?

    fun getBaseAvatar(): Int?

    fun getCourierJson(): String?

    fun getToken(): String?

    fun getRefreshToken(): String?

    fun clear()

}