package ru.efremovkirill.tryrunner.presentation.apps

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Build
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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import ru.efremovkirill.tryrunner.BuildConfig
import ru.efremovkirill.tryrunner.R
import ru.efremovkirill.tryrunner.data.downloadmanager.DownloadManagerHelper
import ru.efremovkirill.tryrunner.databinding.FragmentAppsBinding
import ru.efremovkirill.tryrunner.presentation.BaseFragment
import ru.efremovkirill.tryrunner.presentation.utils.UIState
import java.io.File


class AppsFragment : BaseFragment<FragmentAppsBinding>(), AppsAdapter.OnAppInteractionListener,
    DownloadManagerHelper.OnDownloadManagerHelperListener {

    private val appsAdapter = AppsAdapter(this)
    private val viewModel: AppsViewModel by viewModels()

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
                //
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

    private val storageRequestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
//                showSuccess(message = "Доступ к хранилищу получен. Спасибо!")
            } else {
                showWarning(message = "Без доступа к хранилищу - загрузка будет недоступна")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.titleTextView.text = "Приложения"
        binding.toolbar.backButton.visibility = View.GONE

        binding.appsRcView.apply {
            adapter = appsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getApps()
        }

        collectApps()
        readExternalStorageRequestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        storageRequestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onResume() {
        super.onResume()

        viewModel.getApps()
    }

    private fun collectApps() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.apps.collect { state ->
                when (state) {
                    is UIState.Success -> {
                        val apps = (state.data ?: emptyList()).map {
                            it.toModel(
                                getAppVersionOnDevice(it.packageName)
                            )
                        }

                        val oldApps = appsAdapter.unload()
                        if (apps != oldApps && oldApps.isNotEmpty()) showInfo(message = "Появились изменения в приложениях")

                        val diffUtil = AppsDiffUtil(
                            oldList = oldApps,
                            newList = apps
                        )
                        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)

                        appsAdapter.load(apps = apps)
                        diffUtilResult.dispatchUpdatesTo(appsAdapter)

                        binding.shimmerLayout.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    is UIState.Error -> {
                        showError(message = state.message)

                        binding.shimmerLayout.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    else -> {}
                }
            }
        }
    }

    private fun getAppVersionOnDevice(packageName: String): Long {
        var version = -1L

        try {
            val pm = requireContext().packageManager
            val pInfo = pm.getPackageInfo(packageName, 0)
            version = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode
            } else pInfo.versionCode.toLong()
        } catch (e: Exception) {
            Log.e("Download", "getAppVersionOnDevice: ${e.printStackTrace()}")
        }

        return version
    }

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(ctxt: Context, intent: Intent) {
            getFileNameAndOpenDownloadDirectory(intent)
        }
    }

    @SuppressLint("Range")
    private fun getFileNameAndOpenDownloadDirectory(intent: Intent) {
        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        val downloadManager =
            context?.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
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
            }
        }
        cursor?.close()
    }

    private fun installApplication(apkUri: Uri) {
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

    override fun getViewBinding(): FragmentAppsBinding {
        return FragmentAppsBinding.inflate(layoutInflater)
    }

    override fun onDestroy() {
        super.onDestroy()

        requireActivity().unregisterReceiver(onDownloadComplete)
    }

    override fun onAppClick(appJson: String) {
        val bundle = Bundle()
        bundle.putString("app-json", appJson)

        findNavController().navigate(R.id.action_appsFragment_to_appDetailFragment, bundle)
    }

    override fun onAppUpdate(appName: String, appId: Long) {
        downloadManagerHelper.downloadApkFile(
            fileName = appName, url = "https://api.yooyo.ru/debug/get-app?appId=${appId}"
        )
    }

    override fun onDownloadStarted(fileName: String) {
        Toast.makeText(requireContext(), "$fileName загружается...", Toast.LENGTH_LONG).show()
    }

    override fun onDownloadFailed(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
    }
}