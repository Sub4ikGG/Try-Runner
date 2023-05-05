package ru.efremovkirill.tryrunner.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.efremovkirill.tryrunner.R
import ru.efremovkirill.tryrunner.data.ktor.KtorClient

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        KtorClient.initialize()
    }

    override fun onDestroy() {
        super.onDestroy()

//        KtorClient.closeClient()
    }

}