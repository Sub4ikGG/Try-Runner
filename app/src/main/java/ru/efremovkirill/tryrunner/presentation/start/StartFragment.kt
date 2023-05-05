package ru.efremovkirill.tryrunner.presentation.start

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.efremovkirill.tryrunner.BuildConfig
import ru.efremovkirill.tryrunner.R
import ru.efremovkirill.tryrunner.data.downloadmanager.DownloadManagerHelper
import ru.efremovkirill.tryrunner.databinding.FragmentStartBinding
import ru.efremovkirill.tryrunner.presentation.BaseFragment
import ru.efremovkirill.tryrunner.presentation.utils.UIState
import java.io.File


class StartFragment : BaseFragment<FragmentStartBinding>(),
    DownloadManagerHelper.OnDownloadManagerHelperListener {

    private val readExternalStorageRequestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                writeExternalStorageRequestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                showWarning(message = "Без доступа к хранилищу - загрузка будет недоступна")
            }
        }

    private val writeExternalStorageRequestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.getAppVersion()
            } else {
                showWarning(message = "Без доступа к хранилищу - загрузка будет недоступна")
            }
        }

    private val downloadManagerHelper: DownloadManagerHelper by lazy {
        DownloadManagerHelper(
            downloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager,
            onDownloadManagerHelperListener = this
        )
    }

    private val viewModel: StartViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.codePanel.visibility = View.GONE
        readExternalStorageRequestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        collectAppVersion()
    }

    private fun collectAppVersion() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.appVersion.collect { state ->
                if (state is UIState.Success) {
                    val appVersion = state.data ?: return@collect
                    val currentVersionCode = BuildConfig.VERSION_CODE

                    if (appVersion.versionCode > currentVersionCode) {
                        Toast.makeText(requireContext(), "Следующие разрешения необходимо для обновления приложений", Toast.LENGTH_LONG).show()
                        downloadNewAppVersion()
                    } else
                        findNavController().navigate(R.id.action_startFragment_to_main_nav_graph)
                } else if (state is UIState.Error) {
                    showError(message = state.message)
                }
            }
        }
    }

    private fun downloadNewAppVersion() {
        binding.infoTextView.text = "Установка обновления..."
        binding.infoTextView.visibility = View.VISIBLE

        downloadManagerHelper.downloadApkFile(
            fileName = "try-runner",
            url = "https://api.yooyo.ru/debug/get-app?appId=${5}"
        )
    }

    override fun onResume() {
        super.onResume()

        requireActivity().registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    override fun onStop() {
        super.onStop()

        requireActivity().unregisterReceiver(onDownloadComplete)
    }

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(ctxt: Context, intent: Intent) {
            CoroutineScope(Dispatchers.Main).launch {
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

                val query = DownloadManager.Query()
                query.setFilterById(downloadId)
                val cursor: Cursor = downloadManagerHelper.getDownloadManager().query(query)

                if (cursor.moveToFirst()) {
                    val uriIndex: Int = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                    val uriString: String = cursor.getString(uriIndex)
                    val uri = Uri.parse(uriString)

                    val path = uri.path // это путь к загруженному файлу
                    val fileName = path?.substringAfterLast("/") // это название файла

                    val checkFile = File(
                        Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS
                        ), "try_runner/$fileName"
                    )

                    val apkUri =
                        FileProvider.getUriForFile(
                            requireContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            checkFile
                        )
                    try {
                        installApplication(apkUri)
                    }
                    catch (e: Exception) {
                        Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("InstallApplication", "onReceive: ${e.printStackTrace()}")
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to download update.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                cursor.close()
            }
        }
    }

    fun installApplication(apkUri: Uri) {
        val packageInstaller = requireContext().packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        val sessionId = packageInstaller.createSession(params)
        val session = packageInstaller.openSession(sessionId)
        val outputStream = session.openWrite("ru.efremovkirill.tryrunner", 0, -1)
        val inputStream = requireContext().contentResolver.openInputStream(apkUri)
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
        val pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, 0)
        session.commit(pendingIntent.intentSender)
    }

    override fun getViewBinding(): FragmentStartBinding {
        return FragmentStartBinding.inflate(layoutInflater)
    }

    override fun onDownloadStarted(fileName: String) {
    }

    override fun onDownloadFailed(error: String) {
        showError(message = error)
    }

}