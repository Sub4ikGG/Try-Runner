package ru.efremovkirill.tryrunner.presentation.start

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.efremovkirill.tryrunner.domain.models.AppVersion
import ru.efremovkirill.tryrunner.domain.usecases.AuthUseCase
import ru.efremovkirill.tryrunner.domain.usecases.GetAppVersionUseCase
import ru.efremovkirill.tryrunner.presentation.BaseViewModel
import ru.efremovkirill.tryrunner.presentation.utils.UIState

class StartViewModel: BaseViewModel() {

    private val authUseCase = AuthUseCase()
    private val getAppVersionUseCase = GetAppVersionUseCase()

    private val _auth: MutableStateFlow<UIState<Boolean>> = MutableStateFlow(UIState.Idle())
    val auth = _auth.asStateFlow()

    private val _appVersion: MutableStateFlow<UIState<AppVersion>?> = MutableStateFlow(null)
    val appVersion = _appVersion.asStateFlow()

    fun auth(code: String) = vms.launch(dio) {
        val response = authUseCase(code = code)

        if(response.code == 200)
            _auth.emit(UIState.Success(data = true))
        else
            _auth.emit(UIState.Error(message = response.message))
    }

    fun getAppVersion() = vms.launch(dio) {
        val response = getAppVersionUseCase()

        if(response.code == 200 && response.data != null)
            _appVersion.emit(UIState.Success(data = response.data.toModel()))
        else _appVersion.emit(UIState.Error(message = response.message))
    }

}