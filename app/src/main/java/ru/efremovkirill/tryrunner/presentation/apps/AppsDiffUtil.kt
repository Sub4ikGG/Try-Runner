package ru.efremovkirill.tryrunner.presentation.apps

import androidx.recyclerview.widget.DiffUtil
import ru.efremovkirill.tryrunner.domain.models.AppModel

class AppsDiffUtil(
    oldList: List<AppModel>,
    newList: List<AppModel>
) : DiffUtil.Callback() {
    private val oldList: List<AppModel>
    private val newList: List<AppModel>

    init {
        this.oldList = oldList
        this.newList = newList
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldApp: AppModel = oldList[oldItemPosition]
        val newApp: AppModel = newList[newItemPosition]

        return oldApp.id == newApp.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldApp: AppModel = oldList[oldItemPosition]
        val newApp: AppModel = newList[newItemPosition]

        return oldApp.name == newApp.name
                && oldApp.version == newApp.version
                && oldApp.versionCode == newApp.versionCode
                && oldApp.currentAppVersion == newApp.currentAppVersion
                && oldApp.description == newApp.description
                && oldApp.logoHref == newApp.logoHref
    }
}