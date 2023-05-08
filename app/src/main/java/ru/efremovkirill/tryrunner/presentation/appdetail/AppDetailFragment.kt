package ru.efremovkirill.tryrunner.presentation.appdetail

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.efremovkirill.tryrunner.data.downloadmanager.DownloadManagerHelper
import ru.efremovkirill.tryrunner.databinding.FragmentAppDetailBinding
import ru.efremovkirill.tryrunner.domain.models.AppModel
import ru.efremovkirill.tryrunner.presentation.BaseFragment
import ru.efremovkirill.tryrunner.presentation.utils.JsonUtils
import ru.efremovkirill.tryrunner.presentation.utils.setOnCustomClickListener


class AppDetailFragment : BaseFragment<FragmentAppDetailBinding>(), DownloadManagerHelper.OnDownloadManagerHelperListener {

    private val downloadManagerHelper: DownloadManagerHelper by lazy {
        DownloadManagerHelper(
            downloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager,
            onDownloadManagerHelperListener = this
        )
    }

    private val screenshotsAdapter = ScreenshotsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()
        val appJson = args.getString("app-json") ?: return onDestroy()
        val app = JsonUtils.gson().fromJson(appJson, AppModel::class.java)

        Glide.with(binding.root)
            .load(app.logoHref)
            .into(binding.header.logoImageView)

        binding.toolbar.titleTextView.text = app.name
        binding.toolbar.backButton.setOnCustomClickListener { findNavController().popBackStack() }

        binding.header.appNameTextView.text = app.name
        binding.header.appDescriptionTextView.text = app.description

        binding.information.versionTextView.text = app.version
        binding.information.isReleaseTextView.text = "Да"

        screenshotsAdapter.load(screenshots = app.screenshots)

        binding.screenshots.rcView.apply {
            adapter = screenshotsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        checkAppVersion(app)

        binding.header.installOrUpdateOrOpenButton.setOnCustomClickListener {
            when (binding.header.installOrUpdateOrOpenButton.text.toString().toButtonState()) {
                ButtonState.OPEN -> {
                    downloadManagerHelper.openApplication(activity = requireActivity(), packageName = app.packageName)
                }

                ButtonState.INSTALL, ButtonState.UPDATE -> {
                    downloadManagerHelper.downloadApkFile(
                        fileName = app.packageName, url = "https://api.yooyo.ru/debug/get-app?appId=${app.id}"
                    )
                }
            }
        }

        binding.header.deleteButton.setOnCustomClickListener {
            val packageURI: Uri = Uri.parse("package:" + app.packageName)
            val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)
            startActivity(uninstallIntent)
        }

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

    private fun checkAppVersion(app: AppModel) {
        CoroutineScope(Dispatchers.Main).launch {
            while (this@AppDetailFragment.isVisible) {
                delay(50L)

                when (val currentAppVersion =
                    downloadManagerHelper.getAppVersionOnDevice(context = requireContext(), packageName = app.packageName)) {
                    -1L -> {
                        binding.header.installOrUpdateOrOpenButton.text = "Установить"
                        binding.header.deleteButton.visibility = View.GONE
                    }

                    else -> {
                        if (currentAppVersion > app.versionCode) {
                            binding.header.installOrUpdateOrOpenButton.text = "Обновить"
                            binding.header.deleteButton.visibility = View.VISIBLE
                        } else {
                            binding.header.installOrUpdateOrOpenButton.text = "Открыть"
                            binding.header.deleteButton.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(ctxt: Context, intent: Intent) {
            downloadManagerHelper.getFileNameAndOpenDownloadDirectory(requireContext(), intent)
        }
    }

    override fun getViewBinding(): FragmentAppDetailBinding {
        return FragmentAppDetailBinding.inflate(layoutInflater)
    }

    private fun String.toButtonState(): ButtonState {
        return when (this) {
            "Установить" -> ButtonState.INSTALL
            "Обновить" -> ButtonState.UPDATE
            "Открыть" -> ButtonState.OPEN
            else -> ButtonState.OPEN
        }
    }

    companion object {
        private enum class ButtonState {
            INSTALL,
            OPEN,
            UPDATE
        }
    }

    override fun onDownloadStarted(fileName: String) {
        showInfo(message = "$fileName загружается...")
    }

    override fun onDownloadFailed(error: String) {
        showError(message = error)
    }

}