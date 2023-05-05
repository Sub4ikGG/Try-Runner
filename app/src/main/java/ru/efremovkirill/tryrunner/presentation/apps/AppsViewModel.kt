package ru.efremovkirill.tryrunner.presentation.apps

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.efremovkirill.tryrunner.data.models.AppModelDTO
import ru.efremovkirill.tryrunner.domain.usecases.GetAppsUseCase
import ru.efremovkirill.tryrunner.presentation.BaseViewModel
import ru.efremovkirill.tryrunner.presentation.utils.UIState

class AppsViewModel : BaseViewModel() {

    private val getAppsUseCase = GetAppsUseCase()

    private val _apps: MutableStateFlow<UIState<List<AppModelDTO>>> = MutableStateFlow(UIState.Idle())
    val apps = _apps.asStateFlow()

    fun getApps() = vms.launch(dio) {
        val response = getAppsUseCase()

        if (response.code == 200)
            _apps.emit(
                UIState.Success(
                    message = response.message,
                    data = response.data ?: emptyList()
                )
            )
        else _errorMessage.emit(response.message)
    }

}