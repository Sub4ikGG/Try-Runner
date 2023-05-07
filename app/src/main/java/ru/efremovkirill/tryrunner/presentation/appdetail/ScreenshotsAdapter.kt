package ru.efremovkirill.tryrunner.presentation.appdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.efremovkirill.tryrunner.R
import ru.efremovkirill.tryrunner.databinding.ScreenshotItemBinding

class ScreenshotsAdapter : RecyclerView.Adapter<ScreenshotsAdapter.ViewHolder>() {
    private var screenshots = listOf<String>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ScreenshotItemBinding.bind(view)

        fun bind(screenshotHref: String) {

            Glide.with(binding.root)
                .load(screenshotHref)
                .into(binding.screenshotImageView)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.screenshot_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(screenshots[position])
    }

    override fun getItemCount(): Int = screenshots.size

    fun load(screenshots: List<String>) {
        this.screenshots = screenshots
    }

}