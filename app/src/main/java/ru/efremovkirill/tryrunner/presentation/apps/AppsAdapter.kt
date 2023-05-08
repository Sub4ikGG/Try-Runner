package ru.efremovkirill.tryrunner.presentation.apps

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ru.efremovkirill.tryrunner.R
import ru.efremovkirill.tryrunner.databinding.AppLayoutBinding
import ru.efremovkirill.tryrunner.domain.models.AppModel
import ru.efremovkirill.tryrunner.presentation.utils.AppVersionStatus
import ru.efremovkirill.tryrunner.presentation.utils.JsonUtils
import ru.efremovkirill.tryrunner.presentation.utils.setOnCustomClickListener

class AppsAdapter(
    private val onAppInteractionListener: OnAppInteractionListener
) : RecyclerView.Adapter<AppsAdapter.ViewHolder>() {
    private var apps = listOf<AppModel>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = AppLayoutBinding.bind(view)

        fun bind(app: AppModel) {
            val version = "Версия: ${app.version}"
            val status = when(app.currentAppVersion) {
                -1L -> AppVersionStatus.NEED_TO_INSTALL
                else -> {
                    if (app.versionCode > app.currentAppVersion)
                        AppVersionStatus.UPDATE
                    else AppVersionStatus.UP_TO_DATE
                }
            }

            binding.appNameTextView.text = app.name
            binding.appDescriptionTextView.text = app.description
            binding.appVersionTextView.text = version
            binding.appVersionStatusTextView.text = status.toStatus()

            Glide.with(binding.root)
                .load(app.logoHref)
                .into(binding.logoImageView)

            if (status == AppVersionStatus.NEED_TO_INSTALL || status == AppVersionStatus.UPDATE) {
                binding.appVersionStatusTextView.setOnCustomClickListener {
                    onAppInteractionListener.onAppUpdate(appName = app.packageName, appId = app.id)
                    /*binding.appVersionStatusTextView.text = "Установка..."
                    binding.appVersionStatusTextView.isEnabled = false*/
                }
            }

            binding.seeAppButton.setOnCustomClickListener {
                onAppInteractionListener.onAppClick(
                    JsonUtils.gson().toJson(app)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(apps[position])
    }

    override fun getItemCount(): Int = apps.size

    fun unload() = apps

    @SuppressLint("NotifyDataSetChanged")
    fun load(apps: List<AppModel>) {
        this.apps = apps
        notifyDataSetChanged()
    }

    private fun AppVersionStatus.toStatus(): String {
        return when(this) {
            AppVersionStatus.UPDATE -> "Обновить приложение"
            AppVersionStatus.UP_TO_DATE -> "Установлена актуальная версия"
            AppVersionStatus.NEED_TO_INSTALL -> "Установить"
        }
    }

    interface OnAppInteractionListener {
        fun onAppClick(appJson: String)
        fun onAppUpdate(appName: String, appId: Long)
    }
}
