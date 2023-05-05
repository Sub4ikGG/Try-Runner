package ru.efremovkirill.tryrunner.data.downloadmanager

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File

class DownloadManagerHelper(
    private val downloadManager: DownloadManager,
    private val onDownloadManagerHelperListener: OnDownloadManagerHelperListener
) {

    fun downloadApkFile(fileName: String, url: String): Boolean {
        if (!url.startsWith("http")) {
            return false
        }

        try {
            val file = File(Environment.getExternalStorageDirectory(), "Download")
            if (!file.exists()) {
                file.mkdirs()
            }

            val request = DownloadManager.Request(Uri.parse(url))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "try_runner/$fileName.apk")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            onDownloadManagerHelperListener.onDownloadStarted(fileName = fileName)
            downloadManager.enqueue(request)
        } catch (e: Exception) {
            onDownloadManagerHelperListener.onDownloadFailed(error = e.message.toString())
            Log.e(">>>>>", e.printStackTrace().toString())
            return false
        }
        return true
    }

    fun getDownloadManager() = downloadManager

    interface OnDownloadManagerHelperListener {
        fun onDownloadStarted(fileName: String)
        fun onDownloadFailed(error: String)
    }

}