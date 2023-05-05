package ru.efremovkirill.tryrunner.presentation.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object JsonUtils {

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    fun Any.toJson(): String = gson.toJson(this)

    fun gson(): Gson = gson

}