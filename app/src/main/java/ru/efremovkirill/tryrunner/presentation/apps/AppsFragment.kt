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
                                downloadManagerHelper.getAppVersionOnDevice(context = requireContext(), it.packageName)
                            )
                        }

                        val oldApps = appsAdapter.unload()
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

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(ctxt: Context, intent: Intent) {
            downloadManagerHelper.getFileNameAndOpenDownloadDirectory(context = requireContext(), intent)
        }
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