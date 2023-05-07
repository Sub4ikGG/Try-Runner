package ru.efremovkirill.tryrunner.presentation.appdetail

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import ru.efremovkirill.tryrunner.databinding.FragmentAppDetailBinding
import ru.efremovkirill.tryrunner.domain.models.AppModel
import ru.efremovkirill.tryrunner.presentation.BaseFragment
import ru.efremovkirill.tryrunner.presentation.utils.JsonUtils
import ru.efremovkirill.tryrunner.presentation.utils.setOnCustomClickListener

class AppDetailFragment : BaseFragment<FragmentAppDetailBinding>() {

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

        when (val currentAppVersion = getAppVersionOnDevice(packageName = app.packageName)) {
            -1L -> {
                binding.header.installOrUpdateOrOpenButton.text = "Установить"
                binding.header.deleteButton.visibility = View.GONE
            }

            else -> {
                if (currentAppVersion > app.versionCode) {
                    binding.header.installOrUpdateOrOpenButton.text = "Обновить"
                    binding.header.deleteButton.visibility = View.VISIBLE
                }
                else {
                    binding.header.installOrUpdateOrOpenButton.text = "Открыть"
                    binding.header.deleteButton.visibility = View.VISIBLE
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

    override fun getViewBinding(): FragmentAppDetailBinding {
        return FragmentAppDetailBinding.inflate(layoutInflater)
    }

}