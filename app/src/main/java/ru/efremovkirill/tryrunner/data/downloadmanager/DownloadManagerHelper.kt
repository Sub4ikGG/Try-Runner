package ru.efremovkirill.tryrunner.data.downloadmanager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import ru.efremovkirill.tryrunner.BuildConfig
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
            file.deleteRecursively()
            file.delete()

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

    fun openApplication(activity: Activity, packageName: String) {
        val launchIntent: Intent? = activity.packageManager
            .getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            activity.startActivity(launchIntent)
        }
    }

    fun getAppVersionOnDevice(context: Context, packageName: String): Long {
        var version = -1L

        try {
            val pm = context.packageManager
            val pInfo = pm.getPackageInfo(packageName, 0)
            version = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode
            } else pInfo.versionCode.toLong()
        } catch (e: Exception) {
            Log.e("Download", "getAppVersionOnDevice: ${e.printStackTrace()}")
        }

        return version
    }

    @SuppressLint("Range")
    fun getFileNameAndOpenDownloadDirectory(context: Context, intent: Intent) {
        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager?.query(query)
        if (cursor != null && cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                val uriString =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))

                val uri = Uri.fromFile(File(uriString))
                val path = uri.path // это путь к загруженному файлу
                val fileName = path?.substringAfterLast("/") // это название файла

                val checkFile = File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    ), "try_runner/$fileName"
                )

                val apkUri =
                    FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        checkFile
                    )

                try {
                    installApplication(context, apkUri)
                }
                catch (e: Exception) {
                    Toast.makeText(context, "${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("InstallApplication", "onReceive: ${e.printStackTrace()}")
                }
            }
        }
        cursor?.close()
    }

    fun installApplication(context: Context, apkUri: Uri) {
        val packageInstaller = context.packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        val sessionId = packageInstaller.createSession(params)
        val session = packageInstaller.openSession(sessionId)
        val outputStream = session.openWrite("ru.efremovkirill.tryrunner", 0, -1)
        val inputStream = context.contentResolver.openInputStream(apkUri)
        val buffer = ByteArray(65536)
        var c: Int

        while (inputStream!!.read(buffer).also { c = it } != -1) {
            outputStream.write(buffer, 0, c)
        }

        session.fsync(outputStream)
        inputStream.close()
        outputStream.close()

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        session.commit(pendingIntent.intentSender)
    }

    fun getDownloadManager() = downloadManager

    interface OnDownloadManagerHelperListener {
        fun onDownloadStarted(fileName: String)
        fun onDownloadFailed(error: String)
    }

}