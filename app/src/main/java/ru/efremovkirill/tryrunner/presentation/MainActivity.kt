package ru.efremovkirill.tryrunner.presentation

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Secure
import android.widget.Toast
import ru.efremovkirill.tryrunner.R
import ru.efremovkirill.tryrunner.data.ktor.KtorClient
import ru.efremovkirill.tryrunner.data.storage.LocalStorage
import java.security.Security

class MainActivity : AppCompatActivity() {

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LocalStorage.init(this)

        val localStorage = LocalStorage.newInstance()
        val deviceId = Secure.getString(this.contentResolver, Secure.ANDROID_ID)
        localStorage?.saveDeviceId(deviceId = deviceId)

        KtorClient.initialize()
    }

    override fun onDestroy() {
        super.onDestroy()

//        KtorClient.closeClient()
    }

}