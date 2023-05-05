package ru.efremovkirill.tryrunner.presentation.start

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.efremovkirill.tryrunner.domain.models.AppVersion
import ru.efremovkirill.tryrunner.domain.usecases.GetAppVersionUseCase
import ru.efremovkirill.tryrunner.presentation.BaseViewModel
import ru.efremovkirill.tryrunner.presentation.utils.UIState

class StartViewModel: BaseViewModel() {

    private val getAppVersionUseCase = GetAppVersionUseCase()

    private val _appVersion: MutableStateFlow<UIState<AppVersion>?> = MutableStateFlow(null)
    val appVersion = _appVersion.asStateFlow()

    fun getAppVersion() = vms.launch(dio) {
        val response = getAppVersionUseCase()

        if(response.code == 200 && response.data != null)
            _appVersion.emit(UIState.Success(data = response.data.toModel()))
        else _appVersion.emit(UIState.Error(message = response.message))
    }

}