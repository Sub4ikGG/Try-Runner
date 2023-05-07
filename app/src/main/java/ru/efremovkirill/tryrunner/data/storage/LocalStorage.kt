package ru.efremovkirill.tryrunner.data.storage

import android.content.Context
import android.content.SharedPreferences

class LocalStorage(
    private val sharedPreferences: SharedPreferences
) : ILocalStorage {

    override fun save(key: String, data: String) {
        sharedPreferences.edit().apply {
            putString(key, data)
        }.apply()
    }

    override fun has(key: String): Boolean =
        sharedPreferences.getString(key, null) != null

    override fun get(key: String): String? =
        sharedPreferences.getString(key, null)

    override fun saveToken(token: String) {
        sharedPreferences.edit().apply {
            putString(TOKEN, token)
        }.apply()
    }

    override fun saveRefreshToken(refreshToken: String) {
        sharedPreferences.edit().apply {
            putString(REFRESH_TOKEN, refreshToken)
        }.apply()
    }

    override fun saveCourierJson(json: String) {
        sharedPreferences.edit().apply {
            putString(COURIER, json)
        }.apply()
    }

    override fun saveDeviceId(deviceId: String) {
        if (get(DEVICE_ID) == null)
            sharedPreferences.edit().apply {
                putString(DEVICE_ID, deviceId)
            }.apply()
    }

    override fun saveBaseAvatar(avatarId: Int) {
        sharedPreferences.edit().apply {
            putString(AVATAR_ID, avatarId.toString())
        }.apply()
    }

    override fun getDeviceId(): String? =
        sharedPreferences.getString(DEVICE_ID, null)

    override fun getBaseAvatar(): Int? =
        get(AVATAR_ID)?.toInt()

    override fun getCourierJson(): String? =
        sharedPreferences.getString(COURIER, null)

    override fun getToken(): String? =
        get(TOKEN)

    override fun getRefreshToken(): String? =
        get(REFRESH_TOKEN)

    override fun clear() {
        sharedPreferences.edit().apply {
            remove(TOKEN)
            remove(REFRESH_TOKEN)
            remove(COURIER)
            apply()
        }
    }

    companion object {

        private const val STORAGE = "local-storage"
        private var INSTANCE: LocalStorage? = null

        private const val TOKEN = "token"
        private const val REFRESH_TOKEN = "refresh-token"
        private const val COURIER = "courier"
        private const val DEVICE_ID = "device-id"

        private const val AVATAR_ID = "avatar-id"

        fun init(context: Context) {
            val sharedPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
            INSTANCE = LocalStorage(sharedPreferences = sharedPreferences)
        }

        fun newInstance(): LocalStorage? {
            return if (INSTANCE != null) INSTANCE!!
            else null
        }

    }

}